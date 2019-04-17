package frc.robot.vision;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.util.Limelight.LED_STATE;

public class AutoDriveIn extends Command{

    private static final double kTargetYDist = 6.5; //2.7
    private static final double kYDeadband = 1;
    private static final double kXDeadband = 1;

    @Override
    protected void initialize() {
        VisionHelper.getActiveCam().setLED(LED_STATE.ON);
        VisionHelper.setTargetDist(kTargetYDist);
    }

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(VisionHelper.getDriveSignal());
    }

    @Override
    protected boolean isFinished() {
        return VisionHelper.getActiveCam().tArea() >= kTargetYDist
              && Math.abs(VisionHelper.getActiveCam().tX()) <= kXDeadband;
            //   && Elevator.getInstance().isAtSetpoint();
    }

    @Override
    protected void end() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
        VisionHelper.getActiveCam().setLED(LED_STATE.OFF);
    }

    @Override
    protected void interrupted() {
    }

    public static class AutoDriveInForPanel extends AutoDriveIn{
        @Override
        protected boolean isFinished() {
            return PPintake.getInstance().hasPanel();
        }
    }

}