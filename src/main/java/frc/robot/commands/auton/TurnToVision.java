package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.VisionHelper;

public class TurnToVision extends Command{

    private static final double kPwr = 0.35;
    private double mPwr;

    public TurnToVision(boolean cw){
        mPwr = (cw) ? kPwr : -kPwr;
    }

    @Override
    protected void initialize() {
        Ramsete.getInstance().stop();
        Drivetrain.getInstance().setBrakeMode(true);
    }

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(mPwr, -mPwr);
    }

    @Override
    protected boolean isFinished() {
        return VisionHelper.getActiveCam().hasTarget();
    }

    @Override
    protected void end() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

}