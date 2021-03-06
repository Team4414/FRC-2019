package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.balls.ScoreBall;
import frc.robot.commands.panels.ScorePanel;

public class Score extends Command{

    private Command mScoreCommand;

    @Override
    protected void initialize() {
        if (Robot.activeSide == Side.BALL){
            mScoreCommand = new ScoreBall();
        }else{
            mScoreCommand = new ScorePanel();
        }
        mScoreCommand.start();
    }

    @Override
    protected boolean isFinished() {
        return !mScoreCommand.isRunning();
    }

    @Override
    public synchronized void cancel() {
        mScoreCommand.cancel();
        super.cancel();
    }

}