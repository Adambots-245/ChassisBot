package com.adambots;

import edu.wpi.first.math.util.Units;

/**
 * Constants for ChassisBot testing platform.
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

        // Track width and wheel base (meters) - update with actual measurements
        public static final double kTrackWidth = Units.inchesToMeters(24.0);
        public static final double kWheelBase = Units.inchesToMeters(24.0);
    }

    /**
     * Swerve module constants.
     */
    public static final class ModuleConstants {
        // MK4i L2+ gear ratio for Kraken X60
        public static final double kDriveGearRatio = 1.0 / 5.9;

        // Steering gear ratio (MK4i)
        public static final double kTurnGearRatio = 150.0 / 7.0;

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
     */
    public static final class VisionConstants {
        // Camera name (must match PhotonVision configuration)
        public static final String kCameraName = "Front";

        // Camera position relative to robot center (meters)
        // Update these when camera is mounted
        public static final double kCameraXOffset = 0.0;
        public static final double kCameraYOffset = 0.0;
        public static final double kCameraZOffset = 0.5;

        // Camera rotation (degrees)
        public static final double kCameraPitch = 0.0;
        public static final double kCameraYaw = 0.0;
    }
}
