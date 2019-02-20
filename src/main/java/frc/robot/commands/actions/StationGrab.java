package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Elevator.Setpoint;

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
    protected void end() {
        new RetractPanel().start();
    }

    private class RetractPanel extends CommandGroup{
        public RetractPanel(){
            addSequential(new SafeElevatorMove(Setpoint.PANEL_GRAB));
            addSequential(Finger.getInstance().setFingerCommand(true));
            addSequential(new WaitCommand(0.5));
            addSequential(Finger.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(1));
            addSequential(new SafeElevatorMove(Setpoint.STOW));
        }
    }

}