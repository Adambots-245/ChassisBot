package com.adambots;

import java.io.File;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.commands.PathPlannerAuto;
import com.pathplanner.lib.path.PathPlannerPath;

import com.adambots.lib.subsystems.SwerveConfig;
import com.adambots.lib.subsystems.SwerveSubsystem;
import com.adambots.lib.utils.Buttons;
import com.adambots.lib.utils.Buttons.InputCurve;
import com.adambots.subsystems.ShooterSubsystem;

import edu.wpi.first.wpilibj.RobotBase;

/**
 * RobotContainer for ChassisBot testing platform.
 * Uses Logitech Extreme 3D Pro joystick for driving.
 */
public class RobotContainer {

    // Subsystems
    private final SwerveSubsystem swerve;
    private final ShooterSubsystem shooter;

    // Autonomous chooser
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public RobotContainer() {
        // Configure swerve with PathPlanner PID values (P=5.0 defaults work well)
        // Disable cosine compensation, feedforward, and manual odometry for simulation
        SwerveConfig swerveConfig = new SwerveConfig()
            .withTranslationPID(5.0, 0.0, 0.0)
            .withRotationPID(5.0, 0.0, 0.0)
            .withCosineCompensation(!RobotBase.isSimulation())
            .withFeedforward(!RobotBase.isSimulation())
            .withManualOdometry(!RobotBase.isSimulation());

        // Initialize swerve subsystem with YAGSL config directory and config
        swerve = new SwerveSubsystem(
            new File(Filesystem.getDeployDirectory(), "swerve"),
            swerveConfig
        );

        // Initialize shooter subsystem
        shooter = new ShooterSubsystem();

        // Configure default commands
        setupDefaultCommands();

        // Configure button bindings for testing
        configureBindings();

        // Setup autonomous chooser
        setupAutonomousChooser();

        // Add telemetry
        setupDashboard();
    }

    /**
     * Configure default drive command using joystick.
     *
     * Logitech Extreme 3D Pro:
     *   - Y axis = forward/backward
     *   - X axis = strafe left/right
     *   - Z axis (twist) = rotation
     */
    private void setupDefaultCommands() {
        swerve.setDefaultCommand(
            swerve.driveCommand(
                // Forward/backward - uses Buttons factory method
                Buttons.createForwardSupplier(
                    Constants.DriveConstants.kDeadzone,
                    InputCurve.CUBIC
                ),
                // Strafe left/right - uses Buttons factory method
                Buttons.createStrafeSupplier(
                    Constants.DriveConstants.kDeadzone,
                    InputCurve.CUBIC
                ),
                // Rotation - uses Buttons factory method
                Buttons.createRotationSupplier(
                    Constants.DriveConstants.kRotationDeadzone,
                    InputCurve.CUBIC
                )
            )
        );
    }

    /**
     * Configure joystick button bindings for testing.
     *
     * Logitech Extreme 3D Pro button layout:
     *   Button 1 (Trigger): Reserved
     *   Button 2 (Thumb):   Zero gyro
     *   Button 3:           Lock wheels (X pattern)
     *   Button 4:           Center modules
     *   Button 5:           Enable vision
     *   Button 6:           Disable vision
     *   POV Hat:            Snap to cardinal headings
     */
    private void configureBindings() {
        // Button 2 (Thumb) - Zero gyro (reset heading to 0)
        Buttons.JoystickButton2.onTrue(
            Commands.runOnce(() -> swerve.zeroGyro())
                .withName("ZeroGyro")
        );

        // Button 3 - Lock wheels in X pattern (defense)
        Buttons.JoystickButton3.whileTrue(
            Commands.run(() -> swerve.lock(), swerve)
                .withName("LockWheels")
        );

        // Button 4 - Center all modules (wheels straight)
        Buttons.JoystickButton4.onTrue(
            swerve.centerModulesCommand()
        );

        // Button 5 - Enable vision pose updates
        Buttons.JoystickButton5.onTrue(
            swerve.enableVisionCommand()
        );

        // Button 6 - Disable vision pose updates
        Buttons.JoystickButton6.onTrue(
            swerve.disableVisionCommand()
        );

        // POV Hat - Snap to cardinal headings (useful for testing)
        Buttons.JoystickPOVUp.onTrue(
            Commands.runOnce(() -> swerve.resetOdometry(
                new Pose2d(
                    swerve.getPose().getTranslation(),
                    Rotation2d.fromDegrees(0)
                )
            )).withName("SnapTo0deg")
        );

        Buttons.JoystickPOVRight.onTrue(
            Commands.runOnce(() -> swerve.resetOdometry(
                new Pose2d(
                    swerve.getPose().getTranslation(),
                    Rotation2d.fromDegrees(-90)
                )
            )).withName("SnapTo-90deg")
        );

        Buttons.JoystickPOVDown.onTrue(
            Commands.runOnce(() -> swerve.resetOdometry(
                new Pose2d(
                    swerve.getPose().getTranslation(),
                    Rotation2d.fromDegrees(180)
                )
            )).withName("SnapTo180deg")
        );

        Buttons.JoystickPOVLeft.onTrue(
            Commands.runOnce(() -> swerve.resetOdometry(
                new Pose2d(
                    swerve.getPose().getTranslation(),
                    Rotation2d.fromDegrees(90)
                )
            )).withName("SnapTo90deg")
        );
    }

    /**
     * Setup autonomous command chooser.
     */
    private void setupAutonomousChooser() {
        autoChooser.setDefaultOption("Do Nothing", Commands.none());

        // Add PathPlanner autos
        autoChooser.addOption("Test Auto", new PathPlannerAuto("TestAuto"));

        // Alternative: Direct path following with explicit pose reset
        try {
            PathPlannerPath testPath = PathPlannerPath.fromPathFile("TestPath");

            // Get the starting pose from the path
            Pose2d startPose = testPath.getStartingHolonomicPose().orElse(
                new Pose2d(2.0, 2.0, Rotation2d.fromDegrees(0))
            );

            // Create command that resets pose THEN follows path
            Command resetAndFollow = Commands.sequence(
                Commands.runOnce(() -> swerve.resetOdometry(startPose)),
                Commands.waitSeconds(0.1), // Brief delay for pose to settle
                AutoBuilder.followPath(testPath)
            ).withName("Test Path (Reset+Follow)");

            autoChooser.addOption("Test Path Direct", resetAndFollow);
        } catch (Exception e) {
            System.err.println("Failed to load TestPath: " + e.getMessage());
        }

        // Simple drive forward test (uses same method as teleop)
        autoChooser.addOption("Drive Forward Test",
            Commands.run(() -> swerve.drive(
                new edu.wpi.first.math.geometry.Translation2d(1.0, 0.0),
                0.0,
                true), swerve)
                .withTimeout(3.0)
                .withName("DriveForward3Sec"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Add telemetry to dashboard.
     */
    private void setupDashboard() {
        SmartDashboard.putData("Swerve Drive", swerve);
        SmartDashboard.putData("Field", swerve.getSwerveDrive().field);

        // Shooter commands for Shuffleboard testing
        SmartDashboard.putData("Shooter/Run Shooter", shooter.runShooterCommand());
        SmartDashboard.putData("Shooter/Stop Shooter", shooter.stopShooterCommand());
        SmartDashboard.putData("Shooter/Reverse Shooter", shooter.reverseShooterCommand());
        SmartDashboard.putData("Shooter/Run Uptake", shooter.runUptakeCommand());
        SmartDashboard.putData("Shooter/Stop Uptake", shooter.stopUptakeCommand());
        SmartDashboard.putData("Shooter/Reverse Uptake", shooter.reverseUptakeCommand());
    }

    /**
     * Get the selected autonomous command.
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
