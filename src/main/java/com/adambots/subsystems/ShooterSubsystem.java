package com.adambots.subsystems;

import com.adambots.Constants.ShooterConstants;
import com.adambots.RobotMap;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.controls.Follower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.MotorAlignmentValue;
import com.ctre.phoenix6.signals.NeutralModeValue;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Shooter prototype subsystem with two Kraken X60 motors (left/right)
 * and one Kraken X44 uptake motor.
 */
public class ShooterSubsystem extends SubsystemBase {

    private final TalonFX leftMotor;
    private final TalonFX rightMotor;
    private final TalonFX uptakeMotor;

    public ShooterSubsystem() {
        leftMotor = new TalonFX(RobotMap.kShooterLeftPort);
        rightMotor = new TalonFX(RobotMap.kShooterRightPort);
        uptakeMotor = new TalonFX(RobotMap.kUptakePort);

        configureMotors();
    }

    private void configureMotors() {
        // Configure left motor (leader)
        TalonFXConfiguration leftConfig = new TalonFXConfiguration();
        leftConfig.CurrentLimits.SupplyCurrentLimit = ShooterConstants.kShooterCurrentLimit.in(edu.wpi.first.units.Units.Amps);
        leftConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        leftConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        leftMotor.getConfigurator().apply(leftConfig);

        // Configure right motor (follower) - inverted to spin opposite direction
        TalonFXConfiguration rightConfig = new TalonFXConfiguration();
        rightConfig.CurrentLimits.SupplyCurrentLimit = ShooterConstants.kShooterCurrentLimit.in(edu.wpi.first.units.Units.Amps);
        rightConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        rightConfig.MotorOutput.NeutralMode = NeutralModeValue.Coast;
        rightMotor.getConfigurator().apply(rightConfig);

        // Set right motor to follow left motor (opposed direction for shooter)
        rightMotor.setControl(new Follower(RobotMap.kShooterLeftPort, MotorAlignmentValue.Opposed));

        // Configure uptake motor
        TalonFXConfiguration uptakeConfig = new TalonFXConfiguration();
        uptakeConfig.CurrentLimits.SupplyCurrentLimit = ShooterConstants.kUptakeCurrentLimit.in(edu.wpi.first.units.Units.Amps);
        uptakeConfig.CurrentLimits.SupplyCurrentLimitEnable = true;
        uptakeConfig.MotorOutput.NeutralMode = NeutralModeValue.Brake;
        uptakeMotor.getConfigurator().apply(uptakeConfig);
    }

    /**
     * Run the shooter at the configured speed.
     */
    public void runShooter() {
        leftMotor.set(ShooterConstants.kShooterSpeed);
    }

    /**
     * Run the shooter in reverse.
     */
    public void reverseShooter() {
        leftMotor.set(-ShooterConstants.kShooterSpeed);
    }

    /**
     * Stop the shooter motors.
     */
    public void stopShooter() {
        leftMotor.set(0);
    }

    /**
     * Run the uptake at the configured speed.
     */
    public void runUptake() {
        uptakeMotor.set(ShooterConstants.kUptakeSpeed);
    }

    /**
     * Run the uptake in reverse.
     */
    public void reverseUptake() {
        uptakeMotor.set(-ShooterConstants.kUptakeSpeed);
    }

    /**
     * Stop the uptake motor.
     */
    public void stopUptake() {
        uptakeMotor.set(0);
    }

    /**
     * Command to run the shooter while held.
     */
    public Command runShooterCommand() {
        return runEnd(this::runShooter, this::stopShooter)
            .withName("Run Shooter");
    }

    /**
     * Command to reverse the shooter while held.
     */
    public Command reverseShooterCommand() {
        return runEnd(this::reverseShooter, this::stopShooter)
            .withName("Reverse Shooter");
    }

    /**
     * Command to run the uptake while held.
     */
    public Command runUptakeCommand() {
        return runEnd(this::runUptake, this::stopUptake)
            .withName("Run Uptake");
    }

    /**
     * Command to reverse the uptake while held.
     */
    public Command reverseUptakeCommand() {
        return runEnd(this::reverseUptake, this::stopUptake)
            .withName("Reverse Uptake");
    }

    /**
     * Command to stop the shooter (instant).
     */
    public Command stopShooterCommand() {
        return runOnce(this::stopShooter)
            .withName("Stop Shooter");
    }

    /**
     * Command to stop the uptake (instant).
     */
    public Command stopUptakeCommand() {
        return runOnce(this::stopUptake)
            .withName("Stop Uptake");
    }

    @Override
    public void periodic() {
        // Telemetry can be added here if needed
    }
}
