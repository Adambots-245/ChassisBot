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
import com.adambots.lib.vision.PhotonVision;
import com.adambots.lib.vision.VisionSystem;
import com.adambots.lib.vision.config.VisionCameraConfig.CameraPurpose;
import com.adambots.lib.vision.config.VisionConfigBuilder;
import com.adambots.lib.vision.config.VisionSystemConfig;

/**
 * RobotContainer for ChassisBot vision testing.
 * Connects to a local PhotonVision instance to test AprilTag detection
 * and vision-based pose estimation.
 *
 * PhotonVision Setup:
 *   1. Run PhotonVision locally (java -jar photonvision.jar)
 *   2. Open http://localhost:5800 in browser
 *   3. Configure a camera named "Front" in the PV UI
 *   4. Run this project in simulation (./gradlew simulateJava)
 *   5. Open Shuffleboard/SmartDashboard to see vision data
 */
public class RobotContainer {

    // Subsystems
    private final SwerveSubsystem swerve;
    private VisionSystem vision;

    // Autonomous chooser
    private final SendableChooser<Command> autoChooser = new SendableChooser<>();

    public RobotContainer() {
        // Initialize buttons/controllers - Joystick on port 0, no operator controller
        Buttons.init(0, -1, ControllerType.EXTREME_3D_PRO, ControllerType.NONE);

        // Initialize swerve subsystem with YAGSL config directory
        swerve = new SwerveSubsystem(
            new File(Filesystem.getDeployDirectory(), "swerve")
        );

        // Setup vision system with local PhotonVision
        setupVision();

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
     * Configure the vision system using AdambotsLib's VisionSystem abstraction.
     *
     * Steps:
     *   1. Build a VisionSystemConfig with camera positions
     *   2. Create a PhotonVision instance (implements VisionSystem)
     *   3. Pass it to swerve via setupVision()
     *
     * Update camera position/rotation values in Constants.VisionConstants
     * to match your actual camera mount.
     */
    private void setupVision() {
        VisionSystemConfig visionConfig = VisionConfigBuilder.create()
            .addCamera(Constants.VisionConstants.kCameraName)
                .position(
                    Constants.VisionConstants.kCameraX,
                    Constants.VisionConstants.kCameraY,
                    Constants.VisionConstants.kCameraZ
                )
                .rotation(
                    edu.wpi.first.units.Units.Degrees.of(0),        // roll
                    Constants.VisionConstants.kCameraPitch,
                    Constants.VisionConstants.kCameraYaw
                )
                .purpose(CameraPurpose.BOTH)
                .maxTagDistance(Constants.VisionConstants.kMaxTagDistance)
                .done()
            .build();

        vision = new PhotonVision(
            visionConfig,
            swerve::getPose,
            swerve.getField()
        );

        swerve.setupVision(vision);
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

        // Add PathPlanner autos as they are created
        // Example: autoChooser.addOption("Test Path", swerve.getAutonomousCommand("TestPath"));

        SmartDashboard.putData("Auto Chooser", autoChooser);
    }

    /**
     * Add telemetry to dashboard for vision testing.
     */
    private void setupDashboard() {
        SmartDashboard.putData("Swerve Drive", swerve);
        SmartDashboard.putData("Field", swerve.getField());

        // Vision telemetry - runs every cycle to update dashboard
        if (vision != null) {
            Commands.run(() -> {
                SmartDashboard.putBoolean("Vision/Has Target", vision.hasTarget());
                SmartDashboard.putBoolean("Vision/Available", swerve.isVisionAvailable());
                SmartDashboard.putNumber("Vision/Closest Tag ID", vision.getClosestVisibleTag());

                // Log data for each visible tag (check common tag IDs)
                for (int tagId = 1; tagId <= 22; tagId++) {
                    if (vision.isTagVisible(tagId)) {
                        double dist = vision.getDistanceFromAprilTag(tagId);
                        SmartDashboard.putNumber("Vision/Tag " + tagId + " Distance (m)", dist);
                    }
                }

                // Log robot pose from odometry
                var pose = swerve.getPose();
                SmartDashboard.putNumber("Vision/Robot X (m)", pose.getX());
                SmartDashboard.putNumber("Vision/Robot Y (m)", pose.getY());
                SmartDashboard.putNumber("Vision/Robot Heading (deg)", pose.getRotation().getDegrees());
            }).ignoringDisable(true).withName("VisionTelemetry").schedule();
        }
    }

    /**
     * Get the selected autonomous command.
     */
    public Command getAutonomousCommand() {
        return autoChooser.getSelected();
    }
}
