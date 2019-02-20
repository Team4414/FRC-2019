package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class ClimbingGroup extends CommandGroup{
    public ClimbingGroup(){
        addSequential(new Climb());
        addSequential(new DriveForwardAfterClimb());
    }
}