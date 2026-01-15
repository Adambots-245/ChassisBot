package com.adambots;

/**
 * RobotMap defines all hardware port assignments for ChassisBot.
 * CAN IDs, controller ports, and other hardware constants.
 *
 * NOTE: Update these values when the actual robot is built.
 */
public class RobotMap {

    // Controller ports
    public static final int kJoystickPort = 0;

    // ==================== Swerve Drive CAN IDs ====================
    // Front Left Module
    public static final int kFrontLeftDrivePort = 1;
    public static final int kFrontLeftTurnPort = 2;
    public static final int kFrontLeftEncoderPort = 9;

    // Front Right Module
    public static final int kFrontRightDrivePort = 3;
    public static final int kFrontRightTurnPort = 4;
    public static final int kFrontRightEncoderPort = 10;

    // Back Left Module
    public static final int kBackLeftDrivePort = 5;
    public static final int kBackLeftTurnPort = 6;
    public static final int kBackLeftEncoderPort = 11;

    // Back Right Module
    public static final int kBackRightDrivePort = 7;
    public static final int kBackRightTurnPort = 8;
    public static final int kBackRightEncoderPort = 12;

    // ==================== IMU ====================
    public static final int kPigeonPort = 0;
    public static final boolean kPigeonOnCANivore = true;

    // ==================== CANivore Bus Name ====================
    // Use "*" for CANivore auto-detection, or specify bus name
    public static final String kCANivoreBusName = "*";
}
