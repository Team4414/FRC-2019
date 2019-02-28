package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Hand.HandState;

public class ScoreBall extends CommandGroup{

    public ScoreBall(){
        addSequential(Hand.getInstance().setHandCommand(HandState.DROP));
    }

    @Override
    public synchronized void cancel() {
        Hand.getInstance().set(HandState.HOLDING);
        new ReGrabBall().start();
        super.cancel();
    }

    private class ReGrabBall extends GrabBall{
        public ReGrabBall(){
            setTimeout(1);
        }
    }

}