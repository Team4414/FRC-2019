package frc.robot.vision;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.PPintake.PPState;

public class AutoScoreCommand extends CommandGroup{

    public AutoScoreCommand(){
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(new AutoDriveIn());
        addSequential(PPintake.getInstance().setPPCommand(PPState.SCORE));
        addSequential(new WaitCommand(0.5));
        addSequential(PPintake.getInstance().setArmCommand(false));
        addSequential(PPintake.getInstance().setPPCommand(PPState.OFF));
    }

    @Override
    protected void interrupted() {
        PPintake.getInstance().setPP(PPState.OFF);
        PPintake.getInstance().setArm(false);
        Robot.autoPlace = false;
    }
    @Override
    public synchronized void cancel() {
        this.interrupted();
        super.cancel();
    }

    @Override
    protected void end() {
    }
}

