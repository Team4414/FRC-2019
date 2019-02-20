package frc.robot.commands.elevator;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Elevator;

public class ZeroElevator extends Command{

    private static final double kLowerPwr = -0.166;

    @Override
    protected void initialize() {
        if (Elevator.getInstance().checkNeedsZero()){
            Elevator.getInstance().setRaw(kLowerPwr);
        }
    }

    @Override
    protected boolean isFinished() {
        return !Elevator.getInstance().checkNeedsZero();
    }

    @Override
    protected void end() {
        Elevator.getInstance().setRaw(0);
    }

}