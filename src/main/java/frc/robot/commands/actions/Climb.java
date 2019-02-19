package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Climber;

public class Climb extends CommandGroup{
    public Climb(){
        // addSequential(Climber.getInstance().deployPistonCommand(true));
        // addSequential(Climber.getInstance().climbCommand(true));
        // addSequential(new WaitCommand(0.5));
        // addSequential(Climber.getInstance().pullCommand(true));
        // addSequential(Climber.getInstance().climbCommand(true));
    }
}