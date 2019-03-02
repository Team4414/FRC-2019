package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;

public class RetractGroup extends CommandGroup{
    public RetractGroup(){
        addSequential(new RetractClimber());
        addSequential(new DriveForwardAfterClimb());
    }

    @Override
    protected void end() {
        Robot.isClimbing = false;
    }

    @Override
    protected void interrupted() {
        Robot.isClimbing = false;
    }
}