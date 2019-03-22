package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.VisionHelper;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight.LED_STATE;

public class DriveManual extends Command{

    private CheesyDriveHelper drive;

    private double[] operatorSignal;
    private boolean doVision;

    public DriveManual(){
        operatorSignal = new double[]{0,0};
        doVision = false;
        drive = new CheesyDriveHelper();
    }

    @Override
    protected void execute() {
        operatorSignal = drive.cheesyDrive(
            OI.getInstance().getForward(), 
            OI.getInstance().getLeft(),
            OI.getInstance().getQuickTurn(), 
            false
        );
        if (doVision){
            if (Robot.activeSide == Side.BALL){
                VisionHelper.setActiveCam(Robot.limeBall);
                if (VisionHelper.getActiveCam().hasTarget()){
                    operatorSignal[0] -= VisionHelper.turnCorrection();
                    operatorSignal[1] += VisionHelper.turnCorrection();
                }
            }
        }

        Drivetrain.getInstance().setRawSpeed(operatorSignal);
    }

    @Override
    protected void interrupted() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    public void enableVisionCorrection(boolean enable){
        doVision = enable;
    }

}