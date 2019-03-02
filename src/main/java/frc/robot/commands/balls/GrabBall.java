package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Hand.HandState;

public class GrabBall extends CommandGroup{
    public GrabBall(){
        addSequential(Hand.getInstance().setHandCommand(HandState.INTAKING));
        addSequential(new WaitForBall());
        addSequential(new WaitCommand(0.5));
        addSequential(Hand.getInstance().setHandCommand(HandState.HOLDING));
    }
}