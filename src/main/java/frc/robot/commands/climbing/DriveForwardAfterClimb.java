package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

public class DriveForwardAfterClimb extends Command{

    private static final double kDrivePower = -0.2;
    private static final double kPullPower = -1;
    private static final double kClimbHoldPower = 0.7;

    @Override
    protected void initialize() {
        Robot.isClimbing = true;
        Climber.getInstance().setPullRaw(kPullPower);
        Drivetrain.getInstance().setRawSpeed(kDrivePower, kDrivePower);
    }

    @Override
    protected void execute() {
        Climber.getInstance().setClimbRaw(kClimbHoldPower);
        System.out.println(Robot.pdp.getCurrent(RobotMap.ClimberMap.kClimber - 1));
    }

    @Override
    protected void interrupted() {
        Climber.getInstance().setPullRaw(0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}