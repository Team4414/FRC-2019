package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.DustPan;

public class IntakePanel extends Command{

    @Override
    protected boolean isFinished() {
        return DustPan.getInstance().hasPanel();
    }

    @Override
    protected void end() {
    }

}