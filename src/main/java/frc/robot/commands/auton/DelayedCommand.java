package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.vision.VisionHelper;
import frc.util.Limelight.LED_STATE;

public class DelayedCommand extends CommandGroup{

    public DelayedCommand(Command command, double delay){
        addSequential(new WaitCommand(delay));
        addSequential(command);
    }

}