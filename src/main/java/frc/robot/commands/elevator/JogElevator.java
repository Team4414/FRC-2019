package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.Position;

public class JogElevator extends Command{

    private Command mMoveCommand;
    private Position mPos;

    public JogElevator(Position pos){
        mPos = pos;
    }

    @Override
    protected void initialize() {
        mMoveCommand = new SafeElevatorMove(Elevator.getSignal(mPos, Robot.activeSide));
        mMoveCommand.start();
    }

    @Override
    protected boolean isFinished() {
        return !mMoveCommand.isRunning();
    }

}