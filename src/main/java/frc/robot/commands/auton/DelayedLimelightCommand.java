package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.vision.VisionHelper;
import frc.util.Limelight.LED_STATE;

public class DelayedLimelightCommand extends CommandGroup{

    public DelayedLimelightCommand(double delay){
        addSequential(new WaitCommand(delay));
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.ON));
    }

}