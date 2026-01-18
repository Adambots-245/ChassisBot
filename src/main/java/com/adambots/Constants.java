package com.adambots;

import edu.wpi.first.math.util.Units;

/**
 * Constants for ChassisBot testing platform.
 * Configured for 2025 robot chassis (TalonFX drive + SparkMax NEO angle).
 * Organized by subsystem/feature using nested classes.
 */
public final class Constants {

    /**
     * Drive subsystem constants.
     */
    public static final class DriveConstants {
        // Max speeds
        public static final double kMaxSpeedMetersPerSecond = 4.0;
        public static final double kMaxAngularSpeedRadiansPerSecond = Math.PI * 2;

        // Teleop rotation speed multiplier
        public static final double kTeleopRotationalSpeed = 10.0;

        // Input processing
        public static final double kDeadzone = 0.05;
        public static final double kRotationDeadzone = 0.1;

        // Track width and wheel base (meters) - 24" x 24" chassis
        public static final double kTrackWidth = Units.inchesToMeters(24.0);
        public static final double kWheelBase = Units.inchesToMeters(24.0);
    }

    /**
     * Swerve module constants.
     * 2025 Robot: TalonFX (Kraken) drive motors + SparkMax NEO angle motors
     */
    public static final class ModuleConstants {
        // Drive gear ratio (TalonFX Kraken)
        public static final double kDriveGearRatio = 1.0 / 5.9;

        // Steering gear ratio for SparkMax NEO (21.43:1)
        public static final double kTurnGearRatio = 21.43;

        // Wheel dimensions (4 inch wheel)
        public static final double kWheelDiameterMeters = Units.inchesToMeters(4.0);
        public static final double kWheelRadiusMeters = kWheelDiameterMeters / 2.0;
        public static final double kWheelCircumferenceMeters = kWheelDiameterMeters * Math.PI;

        // Current limits
        public static final double kDriveCurrentLimit = 40.0;
        public static final double kTurnCurrentLimit = 20.0;

        // Coefficient of friction for wheel grip
        public static final double kWheelGripCoefficientOfFriction = 1.19;
    }

    /**
     * Autonomous/PathPlanner constants.
     */
    public static final class AutoConstants {
        // Translation PID (X, Y movement)
        public static final double kPTranslation = 3.0;
        public static final double kITranslation = 0.0;
        public static final double kDTranslation = 0.05;

        // Rotation PID (heading control)
        public static final double kPRotation = 1.0;
        public static final double kIRotation = 0.0;
        public static final double kDRotation = 0.06;

        // Max auto speeds
        public static final double kMaxAutoSpeed = 3.0;
        public static final double kMaxAutoAcceleration = 2.0;
    }

    /**
     * Vision constants for PhotonVision.
     * 2025 Robot: 2 front cameras mounted on swerve module structure.
     */
    public static final class VisionConstants {
        // Left camera (mounted on front-left, angled left)
        public static final String kLeftCameraName = "Left";
        public static final double kLeftCameraXOffset = Units.inchesToMeters(15.0);   // Forward from center
        public static final double kLeftCameraYOffset = Units.inchesToMeters(11.75);  // Left of center
        public static final double kLeftCameraZOffset = Units.inchesToMeters(8.0);    // Height from floor
        public static final double kLeftCameraPitch = 0.0;
        public static final double kLeftCameraYaw = -30.0;  // Angled left

        // Right camera (mounted on front-right, angled right)
        public static final String kRightCameraName = "Right";
        public static final double kRightCameraXOffset = Units.inchesToMeters(15.0);   // Forward from center
        public static final double kRightCameraYOffset = Units.inchesToMeters(-11.75); // Right of center
        public static final double kRightCameraZOffset = Units.inchesToMeters(8.0);    // Height from floor
        public static final double kRightCameraPitch = 0.0;
        public static final double kRightCameraYaw = 30.0;  // Angled right
    }
}
