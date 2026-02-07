package com.adambots;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;

import com.adambots.lib.vision.PhotonVision;
import com.adambots.lib.vision.VisionSystem;
import com.adambots.lib.vision.config.VisionCameraConfig.CameraPurpose;
import com.adambots.lib.vision.config.VisionConfigBuilder;
import com.adambots.lib.vision.config.VisionSystemConfig;

/**
 * RobotContainer for standalone vision testing.
 * No swerve subsystem required - uses a dummy pose and field.
 * Connects to a local PhotonVision instance to test AprilTag detection.
 *
 * PhotonVision Setup:
 *   1. Run PhotonVision locally (java -jar photonvision.jar)
 *   2. Open http://localhost:5800 in browser
 *   3. Configure a camera named "Front" in the PV UI
 *   4. Run this project in simulation (./gradlew simulateJava)
 *   5. Open Shuffleboard/SmartDashboard to see vision data
 */
public class RobotContainer {

    // Dummy pose and field for vision (no swerve needed)
    private Pose2d robotPose = new Pose2d();
    private final Field2d field = new Field2d();

    // Vision system
    private VisionSystem vision;

    public RobotContainer() {
        // Setup vision system with local PhotonVision
        setupVision();

        // Add telemetry
        setupDashboard();
    }

    /**
     * Configure the vision system using AdambotsLib's VisionSystem abstraction.
     * Uses a dummy pose supplier and field instead of a full swerve subsystem.
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
            () -> robotPose,
            field
        );
    }

    /**
     * Add telemetry to dashboard for vision testing.
     */
    private void setupDashboard() {
        SmartDashboard.putData("Field", field);

        // Vision telemetry - runs every cycle to update dashboard
        if (vision != null) {
            Commands.run(() -> {
                SmartDashboard.putBoolean("Vision/Has Target", vision.hasTarget());
                SmartDashboard.putNumber("Vision/Closest Tag ID", vision.getClosestVisibleTag());

                // Log data for each visible tag (check common tag IDs)
                for (int tagId = 1; tagId <= 22; tagId++) {
                    if (vision.isTagVisible(tagId)) {
                        double dist = vision.getDistanceFromAprilTag(tagId);
                        SmartDashboard.putNumber("Vision/Tag " + tagId + " Distance (m)", dist);
                    }
                }

                // Show dummy pose on field
                field.setRobotPose(robotPose);
                SmartDashboard.putNumber("Vision/Robot X (m)", robotPose.getX());
                SmartDashboard.putNumber("Vision/Robot Y (m)", robotPose.getY());
                SmartDashboard.putNumber("Vision/Robot Heading (deg)", robotPose.getRotation().getDegrees());
            }).ignoringDisable(true).withName("VisionTelemetry").schedule();
        }
    }

    /**
     * Get the selected autonomous command.
     */
    public Command getAutonomousCommand() {
        return Commands.none();
    }
}
