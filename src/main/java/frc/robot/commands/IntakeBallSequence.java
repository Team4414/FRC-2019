package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.commands.actions.WaitForBall;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Elevator.Setpoint;

public class IntakeBallSequence extends CommandGroup{

    public IntakeBallSequence(){
        addSequential(new Superstructure(Superstructure.intakeBall));
        addSequential(new WaitForBall());
        addSequential(new Superstructure(Setpoint.FUEL_LOW));
        addSequential(new Superstructure(Superstructure.ballStow));
    }

}