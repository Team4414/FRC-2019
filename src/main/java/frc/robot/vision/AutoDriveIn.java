package frc.robot.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;

public class AutoDriveIn extends Command{

    private static final double kTargetYDist = 1.8; //2.7
    private static final double kYDeadband = 1;
    private static final double kXDeadband = 0.5;

    @Override
    protected void initialize() {
        VisionHelper.setTargetDist(kTargetYDist);
    }

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(VisionHelper.getDriveSignal());
    }

    @Override
    protected boolean isFinished() {
        return Math.abs(VisionHelper.getActiveCam().tY() - kTargetYDist) <= kYDeadband
              && Math.abs(VisionHelper.getActiveCam().tX()) <= kXDeadband;
            //   && Elevator.getInstance().isAtSetpoint();
    }

    @Override
    protected void end() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    @Override
    protected void interrupted() {
    }

}