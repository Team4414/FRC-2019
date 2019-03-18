package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
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
        retract = new RetractFinger();
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(0.5));
    }

    @Override
    public synchronized void cancel() {
        retract.start();
        super.cancel();
    }

    public class RetractFinger extends CommandGroup{
        public RetractFinger(){
            addSequential(PPintake.getInstance().setPPCommand(PPState.SCORE));
            addSequential(new WaitCommand(0.25));   
            addSequential(PPintake.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(1));
            addSequential(PPintake.getInstance().setPPCommand(PPState.OFF));
        }
    }

}