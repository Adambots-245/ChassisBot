package com.adambots;

import static edu.wpi.first.units.Units.*;
import edu.wpi.first.units.measure.*;

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
        public static final LinearVelocity kMaxSpeed = MetersPerSecond.of(4.0);
        public static final AngularVelocity kMaxAngularSpeed = RadiansPerSecond.of(Math.PI * 2);

        // Teleop rotation speed multiplier
        public static final AngularVelocity kTeleopRotationalSpeed = RadiansPerSecond.of(10.0);

        // Input processing
        public static final double kDeadzone = 0.05;
        public static final double kRotationDeadzone = 0.1;

        // Track width and wheel base - update with actual measurements
        public static final Distance kTrackWidth = Inches.of(24);
        public static final Distance kWheelBase = Inches.of(24);
    }

    /**
     * Swerve module constants.
     * Using SDS MK5n modules with Kraken X60 (drive) and X44 (turn)
     */
    public static final class ModuleConstants {
        // MK5n drive gear ratio - UPDATE when gear option is selected (L1/L2/L3)
        public static final double kDriveGearRatio = 1.0 / 5.9;

        // Steering gear ratio (MK5n: 287:11)
        public static final double kTurnGearRatio = 287.0 / 11.0;

        // Wheel dimensions (4 inch wheel)
        public static final Distance kWheelDiameter = Inches.of(4);
        public static final Distance kWheelRadius = kWheelDiameter.div(2);
        public static final Distance kWheelCircumference = kWheelDiameter.times(Math.PI);

        // Current limits
        public static final Current kDriveCurrentLimit = Amps.of(40);
        public static final Current kTurnCurrentLimit = Amps.of(20);

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
        public static final LinearVelocity kMaxAutoSpeed = MetersPerSecond.of(3.0);
        public static final LinearAcceleration kMaxAutoAcceleration = MetersPerSecondPerSecond.of(2.0);
    }

    /**
     * Vision constants for PhotonVision.
     */
    public static final class VisionConstants {
        // Camera name (must match PhotonVision configuration)
        public static final String kCameraName = "Front";

        // Camera position relative to robot center
        // Update these when camera is mounted
        public static final Distance kCameraX = Meters.of(0);
        public static final Distance kCameraY = Meters.of(0);
        public static final Distance kCameraZ = Meters.of(0.5);

        // Camera rotation
        public static final Angle kCameraPitch = Degrees.of(0);
        public static final Angle kCameraYaw = Degrees.of(0);
    }
}
