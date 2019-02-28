package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Elevator.Setpoint;

public class GrabStationBall extends CommandGroup{

    @Override
    protected void initialize() {
        Robot.overrideVisionToBall = true;
    }

    public GrabStationBall(){
        addSequential(new SafeElevatorMove(Setpoint.FUEL_STATION));
        addSequential(new GrabBall());
        addSequential(new SafeElevatorMove(Setpoint.FUEL_LOW));
    }

    @Override
    public synchronized void cancel() {
        this.end();
        super.cancel();
    }

    @Override
    protected void interrupted() {
        this.end();
    }

    @Override
    protected void end() {
        Robot.overrideVisionToBall = false;
    }
    
}