package com.adambots.subsystems;

import com.adambots.Constants.QuestNavConstants;
import com.adambots.lib.subsystems.SwerveSubsystem;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Pose3d;
import edu.wpi.first.math.geometry.Rotation3d;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import gg.questnav.questnav.QuestNav;
import gg.questnav.questnav.PoseFrame;

/**
 * Subsystem for QuestNav Meta Quest odometry integration.
 * Reads pose data from the Quest headset via NetworkTables and
 * feeds it to the swerve drive pose estimator.
 */
public class QuestNavSubsystem extends SubsystemBase {
    private final QuestNav questNav;
    private final SwerveSubsystem swerve;

    // Cache the last Quest pose for telemetry
    private Pose3d lastQuestPose = new Pose3d();

    /**
     * Creates a new QuestNavSubsystem.
     *
     * @param swerve The swerve subsystem to feed pose data to
     */
    public QuestNavSubsystem(SwerveSubsystem swerve) {
        this.swerve = swerve;
        this.questNav = new QuestNav();
    }

    @Override
    public void periodic() {
        // Must be called every loop for QuestNav to function
        questNav.commandPeriodic();

        if (!QuestNavConstants.QUESTNAV_ENABLED) {
            updateTelemetry();
            return;
        }

        // Process all unread pose frames from the Quest
        PoseFrame[] frames = questNav.getAllUnreadPoseFrames();

        for (PoseFrame frame : frames) {
            if (frame.isTracking()) {
                // Cache the Quest pose for telemetry
                lastQuestPose = frame.questPose3d();

                // Transform Quest pose to robot-centric pose
                Pose3d robotPose3d = lastQuestPose.transformBy(
                    QuestNavConstants.ROBOT_TO_QUEST.inverse()
                );
                Pose2d robotPose = robotPose3d.toPose2d();

                // Feed to the swerve drive pose estimator
                swerve.getSwerveDrive().addVisionMeasurement(
                    robotPose,
                    frame.dataTimestamp(),
                    QuestNavConstants.QUESTNAV_STD_DEVS
                );
            }
        }

        // Update dashboard telemetry
        updateTelemetry();
    }

    /**
     * Update SmartDashboard with QuestNav status.
     */
    private void updateTelemetry() {
        SmartDashboard.putBoolean("QuestNav/Connected", questNav.isConnected());
        SmartDashboard.putBoolean("QuestNav/Tracking", questNav.isTracking());
        SmartDashboard.putBoolean("QuestNav/Enabled", QuestNavConstants.QUESTNAV_ENABLED);
        SmartDashboard.putNumber("QuestNav/Latency", questNav.getLatency());

        // Battery percentage (if available)
        questNav.getBatteryPercent().ifPresent(
            battery -> SmartDashboard.putNumber("QuestNav/Battery", battery)
        );

        // Show last known Quest pose
        SmartDashboard.putNumber("QuestNav/X", lastQuestPose.getX());
        SmartDashboard.putNumber("QuestNav/Y", lastQuestPose.getY());
        SmartDashboard.putNumber("QuestNav/Z", lastQuestPose.getZ());
        SmartDashboard.putNumber("QuestNav/Yaw", Math.toDegrees(lastQuestPose.getRotation().getZ()));
    }

    /**
     * Check if Quest is connected.
     *
     * @return true if Quest is connected, false otherwise
     */
    public boolean isConnected() {
        return questNav.isConnected();
    }

    /**
     * Check if Quest is actively tracking.
     *
     * @return true if Quest is tracking, false otherwise
     */
    public boolean isTracking() {
        return questNav.isTracking();
    }

    /**
     * Reset Quest pose to match the current robot pose.
     * Call this when the robot is at a known position on the field.
     */
    public void resetPose() {
        Pose2d currentPose = swerve.getPose();
        // Transform robot pose to Quest frame for reset
        Pose3d questPose = new Pose3d(currentPose).transformBy(QuestNavConstants.ROBOT_TO_QUEST);
        questNav.setPose(questPose);
    }

    /**
     * Reset Quest pose to a specific pose.
     *
     * @param pose The pose to reset to (in robot frame)
     */
    public void resetPose(Pose2d pose) {
        Pose3d questPose = new Pose3d(pose).transformBy(QuestNavConstants.ROBOT_TO_QUEST);
        questNav.setPose(questPose);
    }

    /**
     * Get the last known Quest pose for debugging.
     *
     * @return The Quest's last known pose in 3D space
     */
    public Pose3d getQuestPose() {
        return lastQuestPose;
    }
}
