package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.util.Limelight.LED_STATE;

public class StationGrab extends CommandGroup{

    public StationGrab(){
        addSequential(new SafeElevatorMove(Setpoint.STOW));
        addSequential(Finger.getInstance().setArmCommand(true));
        addSequential(Finger.getInstance().setFingerCommand(false));
    }

    @Override
    protected void interrupted() {
        this.end();
    }

    @Override
    protected boolean isFinished() {
        return !Finger.getInstance().getSwitch();
    }

    @Override
    @SuppressWarnings("resource")
    protected void end() {
        new RetractPanel().start();
    }

    private class RetractPanel extends CommandGroup{
        public RetractPanel(){
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.BLINK));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.BLINK));
            addSequential(new WaitCommand(0.25));
            addSequential(Elevator.getInstance().slowElevator(true));
            addSequential(new SafeElevatorMove(Setpoint.PANEL_GRAB));
            addSequential(Finger.getInstance().setFingerCommand(true));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.OFF));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
            addSequential(new WaitCommand(0.20));
            addSequential(Finger.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(1));
            addSequential(new SafeElevatorMove(Setpoint.STOW));
            addSequential(Elevator.getInstance().slowElevator(false));
        }
    }

}