package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Finger;

public class ScorePanel extends CommandGroup{

    public ScorePanel(){
        addSequential(Finger.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(0.5));
        addSequential(Finger.getInstance().setFingerCommand(false));
    }

}