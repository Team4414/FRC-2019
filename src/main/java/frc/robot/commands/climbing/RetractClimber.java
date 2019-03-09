package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

public class RetractClimber extends Command{

    private static final double kRetractPower = -1.0;
    private static final double kDrivePower = -0.15;

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(kDrivePower, kDrivePower);
        
        if (!Climber.getInstance().getTopSwitch()){
            Climber.getInstance().setClimbRaw(kRetractPower);
        }else{
            Climber.getInstance().setClimbRaw(0);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    @Override
    protected void interrupted() {
        this.end();
        Robot.isClimbing = false;
    }

    @Override
    protected void end() {
        Climber.getInstance().setClimbRaw(0);
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

}