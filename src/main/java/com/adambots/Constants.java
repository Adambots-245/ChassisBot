package com.adambots;

import static edu.wpi.first.units.Units.*;
import edu.wpi.first.units.measure.*;

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
        public static final LinearVelocity kMaxSpeed = MetersPerSecond.of(4.0);
        public static final AngularVelocity kMaxAngularSpeed = RadiansPerSecond.of(Math.PI * 2);

        // Teleop rotation speed multiplier
        public static final AngularVelocity kTeleopRotationalSpeed = RadiansPerSecond.of(10.0);

        // Input processing
        public static final double kDeadzone = 0.05;
        public static final double kRotationDeadzone = 0.1;

        // Track width and wheel base - 24" x 24" chassis
        public static final Distance kTrackWidth = Inches.of(24);
        public static final Distance kWheelBase = Inches.of(24);
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
     * 2025 Robot: 2 front cameras mounted on swerve module structure.
     */
    public static final class VisionConstants {
        // Left camera (mounted on front-left, angled left)
        public static final String kLeftCameraName = "Left";
        public static final Distance kLeftCameraX = Inches.of(15);      // Forward from center
        public static final Distance kLeftCameraY = Inches.of(11.75);   // Left of center
        public static final Distance kLeftCameraZ = Inches.of(8);       // Height from floor
        public static final Angle kLeftCameraPitch = Degrees.of(0);
        public static final Angle kLeftCameraYaw = Degrees.of(-30);     // Angled left

        // Right camera (mounted on front-right, angled right)
        public static final String kRightCameraName = "Right";
        public static final Distance kRightCameraX = Inches.of(15);     // Forward from center
        public static final Distance kRightCameraY = Inches.of(-11.75); // Right of center
        public static final Distance kRightCameraZ = Inches.of(8);      // Height from floor
        public static final Angle kRightCameraPitch = Degrees.of(0);
        public static final Angle kRightCameraYaw = Degrees.of(30);     // Angled right
    }

    /**
     * Shooter prototype constants.
     */
    public static final class ShooterConstants {
        // Motor speeds (duty cycle -1.0 to 1.0)
        public static final double kShooterSpeed = 0.75;
        public static final double kUptakeSpeed = 0.5;

        // Current limits
        public static final Current kShooterCurrentLimit = Amps.of(60);
        public static final Current kUptakeCurrentLimit = Amps.of(40);
    }
}
