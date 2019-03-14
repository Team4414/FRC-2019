package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.PPintake.PPState;

public class ScorePanel extends CommandGroup{

    private static RetractFinger retract;

    @Override
    protected void initialize() {
        if (retract == null)
            retract = new RetractFinger();
    }

    public ScorePanel(){
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(0.5));
        addSequential(PPintake.getInstance().setPPCommand(PPState.SCORE));
        addSequential(new WaitCommand(0.25));
    }

    @Override
    public synchronized void cancel() {
        retract.start();
        super.cancel();
    }

    public class RetractFinger extends CommandGroup{
        public RetractFinger(){   
            addSequential(PPintake.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(0.5));
            addSequential(PPintake.getInstance().setPPCommand(PPState.OFF));
        }
    }

}