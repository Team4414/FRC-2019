package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.Position;

public class JogElevator extends Command{

    private Command mMoveCommand;
    private Position mPos;
    private boolean mWantsCargo;

    public JogElevator(Position pos){
        mPos = pos;
        mWantsCargo = false;
    }

    public JogElevator(Position pos, boolean wantsToCargo){
        mPos = pos;
        mWantsCargo = wantsToCargo;
    }

    @Override
    protected void initialize() {
        mMoveCommand = new SafeElevatorMove(Elevator.getSignal(mPos, Robot.activeSide));
        mMoveCommand.start();
        Robot.wantsToCargoOnScore = mWantsCargo;
    }

    @Override
    protected boolean isFinished() {
        return !mMoveCommand.isRunning();
    }

}