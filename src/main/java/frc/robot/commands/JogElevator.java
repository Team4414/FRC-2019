package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Elevator.Position;

public class JogElevator extends Command{

    private Superstructure mMoveCommand;
    private Position mPos;

    public JogElevator(Position pos){
        mPos = pos;
    }

    @Override
    protected void initialize() {
        mMoveCommand = new Superstructure(Elevator.getSignal(mPos, Robot.activeSide));
        mMoveCommand.start();
    }

    @Override
    protected boolean isFinished() {
        return !mMoveCommand.isRunning();
    }

}