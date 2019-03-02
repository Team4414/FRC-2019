package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

public class DriveForwardAfterClimb extends Command{

    private static final double kDrivePower = -0.25;
    private static final double kPullPower = -0.25;

    @Override
    protected void initialize() {
        Robot.isClimbing = true;
        Climber.getInstance().setPullRaw(kPullPower);
        Drivetrain.getInstance().setRawSpeed(kDrivePower, kDrivePower);
    }

    @Override
    protected void interrupted() {
        Climber.getInstance().setPullRaw(0);
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}