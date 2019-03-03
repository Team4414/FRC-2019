package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.Timer;
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

        private double initTime;

        @Override
        protected void initialize() {
            initTime = Timer.getFPGATimestamp();
            super.initialize();
        }

        @Override
        protected boolean isFinished() {
            return super.isFinished() || (Timer.getFPGATimestamp() > 1 + initTime);
        }

        @Override
        protected void end() {
            super.end();
        }
    }

}