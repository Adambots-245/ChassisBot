package com.adambots;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

import com.adambots.lib.utils.Buttons;
import com.adambots.lib.utils.Buttons.ControllerType;

/**
 * Main Robot class for ChassisBot testing platform.
 */
public class Robot extends TimedRobot {
    private Command m_autonomousCommand;
    private RobotContainer m_robotContainer;

    @Override
    public void robotInit() {
        // Silence joystick connection warnings
        DriverStation.silenceJoystickConnectionWarning(true);

        // Initialize Buttons BEFORE creating RobotContainer
        // Port 0 = Joystick (Logitech Extreme 3D Pro)
        // Port -1 = No operator controller
        Buttons.init(
            RobotMap.kJoystickPort,
            -1,
            ControllerType.EXTREME_3D_PRO,
            ControllerType.NONE
        );

        // Create robot container
        m_robotContainer = new RobotContainer();
    }

    @Override
    public void robotPeriodic() {
        CommandScheduler.getInstance().run();
    }

    @Override
    public void disabledInit() {}

    @Override
    public void disabledPeriodic() {}

    @Override
    public void disabledExit() {}

    @Override
    public void autonomousInit() {
        // Cancel any running commands
        CommandScheduler.getInstance().cancelAll();

        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        if (m_autonomousCommand != null) {
            CommandScheduler.getInstance().schedule(m_autonomousCommand);
        }
    }

    @Override
    public void autonomousPeriodic() {}

    @Override
    public void autonomousExit() {}

    @Override
    public void teleopInit() {
        // Cancel autonomous command if running
        if (m_autonomousCommand != null) {
            m_autonomousCommand.cancel();
        }
    }

    @Override
    public void teleopPeriodic() {}

    @Override
    public void teleopExit() {}

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {}

    @Override
    public void testExit() {}
}
