package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Hand.HandState;

public class ScoreBall extends CommandGroup{

    public ScoreBall(){
        addSequential(Hand.getInstance().setHandCommand(HandState.DROP));
    }

    @Override
    protected void interrupted() {
        Hand.getInstance().set(HandState.OFF);
    }

}