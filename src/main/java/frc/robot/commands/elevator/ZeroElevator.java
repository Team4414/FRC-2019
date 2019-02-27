package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;

public class ZeroElevator extends Command{

    private static final double kLowerPwr = -0.166;
    private boolean mForceZero;

    public ZeroElevator(boolean forceZero){
        mForceZero = forceZero;
    }

    public ZeroElevator(){
        mForceZero = false;
    }

    @Override
    protected void initialize() {

        if (mForceZero){
            Elevator.getInstance().setRaw(kLowerPwr);
            Elevator.getInstance().lockElevator(true);
        }

        if (Elevator.getInstance().checkNeedsZero()){
            Elevator.getInstance().setRaw(kLowerPwr);
            Elevator.getInstance().lockElevator(true);
        }

    }

    @Override
    protected boolean isFinished() {
        if (!mForceZero){
            return !Elevator.getInstance().checkNeedsZero();
        }else{
            return Elevator.getInstance().getSwitch();
        }
    }

    @Override
    protected void end() {
        Elevator.getInstance().zero();
        Elevator.getInstance().setRaw(0);
        Elevator.getInstance().lockElevator(false);
    }

}