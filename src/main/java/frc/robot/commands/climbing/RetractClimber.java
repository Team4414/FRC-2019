package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Climber;

public class RetractClimber extends Command{

    private static final double kRetractPower = -0.5;

    @Override
    protected void execute() {
        Climber.getInstance().setClimbRaw(kRetractPower);
    }

    @Override
    protected boolean isFinished() {
        return Climber.getInstance().getTopSwitch();
    }

    @Override
    protected void interrupted() {
        this.end();
    }

    @Override
    protected void end() {
        Climber.getInstance().setClimbRaw(0);
    }

}