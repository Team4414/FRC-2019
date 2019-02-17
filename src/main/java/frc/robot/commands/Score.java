package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.actions.ScoreBall;
import frc.robot.commands.actions.ScorePanel;
import frc.robot.subsystems.Superstructure;

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
    protected void interrupted() {
        mScoreCommand.cancel();
    }

}