package com.adambots;

import java.io.File;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import com.adambots.lib.subsystems.SwerveSubsystem;
import com.adambots.lib.utils.Buttons;
import com.adambots.lib.utils.Buttons.ControllerType;
import com.adambots.lib.utils.Buttons.InputCurve;
import com.adambots.subsystems.QuestNavSubsystem;

/**
 * RobotContainer for ChassisBot testing platform.
 * Uses Logitech Extreme 3D Pro joystick for driving.
 */
public class RobotContainer {

    // Subsystems
    private final SwerveSubsystem swerve;
    private final QuestNavSubsystem questNav;

    // Autonomous chooser
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public RobotContainer() {
        // Initialize buttons/controllers - Joystick on port 0, no operator controller
        Buttons.init(0, -1, ControllerType.EXTREME_3D_PRO, ControllerType.NONE);

        // Initialize swerve subsystem with YAGSL config directory
        swerve = new SwerveSubsystem(
            new File(Filesystem.getDeployDirectory(), "swerve")
        );

        // Initialize QuestNav subsystem for Meta Quest odometry
        questNav = new QuestNavSubsystem(swerve);

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
     *   Button 7:           Reset QuestNav pose
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

        // Button 7 - Reset QuestNav pose to current odometry
        Buttons.JoystickButton7.onTrue(
            Commands.runOnce(() -> questNav.resetPose())
                .withName("ResetQuestNavPose")
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

        // Add PathPlanner autos as they are created
        // Example: autoChooser.addOption("Test Path", swerve.getAutonomousCommand("TestPath"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Add telemetry to dashboard.
     */
    private void setupDashboard() {
        SmartDashboard.putData("Swerve Drive", swerve);
        SmartDashboard.putData("QuestNav", questNav);
    }

    /**
     * Get the selected autonomous command.
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
