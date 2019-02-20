package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;

public class ScorePanel extends CommandGroup{

    private static RetractFinger retract;

    @Override
    protected void initialize() {
        if (retract == null)
            retract = new RetractFinger();
    }

    public ScorePanel(){
        addSequential(Finger.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(0.5));
        addSequential(Finger.getInstance().setFingerCommand(false));
        addSequential(new WaitCommand(0.25));
    }

    @Override
    public synchronized void cancel() {
        retract.start();
        super.cancel();
    }

    public class RetractFinger extends CommandGroup{
        public RetractFinger(){   
            addSequential(Elevator.getInstance().dropElevatorABit(true));
            addSequential(Finger.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(0.5));
            addSequential(Finger.getInstance().setFingerCommand(true));
            addSequential(Elevator.getInstance().dropElevatorABit(false));
        }
    }

}