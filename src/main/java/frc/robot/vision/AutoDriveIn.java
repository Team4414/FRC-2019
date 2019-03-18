package frc.robot.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;

public class AutoDriveIn extends Command{

    @Override
    protected void initialize() {
        VisionHelper.setTargetDist(0);
    }

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(VisionHelper.throttleCorrection() - VisionHelper.turnCorrection(),
                                             VisionHelper.throttleCorrection() + VisionHelper.turnCorrection());
    }

    @Override
    protected boolean isFinished() {
        return VisionHelper.isCloseToScore();
    }

    @Override
    protected void end() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    @Override
    protected void interrupted() {
    }

}