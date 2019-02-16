package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.subsystems.Superstructure;

public class Score extends Command{

    private Superstructure mScoreCommand;

    @Override
    protected void initialize() {
        if (Robot.activeSide == Side.BALL){
            mScoreCommand = new Superstructure(Superstructure.ballScore);
        }else{
            mScoreCommand = new Superstructure(Superstructure.panelScore);
        }
        mScoreCommand.start();
    }

    @Override
    protected boolean isFinished() {
        return !mScoreCommand.isRunning();
    }

}