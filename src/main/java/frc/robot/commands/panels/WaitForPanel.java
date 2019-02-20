package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.DustPan;

public class WaitForPanel extends Command{

    @Override
    protected boolean isFinished() {
        return DustPan.getInstance().hasPanel();
    }

    @Override
    protected void end() {
    }

}