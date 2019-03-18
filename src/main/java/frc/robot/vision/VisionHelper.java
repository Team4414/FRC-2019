package frc.robot.vision;

import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;
import frc.util.Limelight;
import frc.util.RollingAverage;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;

/**
 * 
 */
public class VisionHelper{

    //---------- Constants ----------

    private static final double kPanelAutoScoreY = 3;

    private static final double kTrackingGain = 0.017;
    private static final double kDerivativeGain = -0.17;

    private static final double kDriveGain = 0.07;
    private static final double kDriveDerivGain = -0.7;
    //-------------------------------

    private static double mGyroTarget = 0;
    private static double mDistTarg = 0;

    private static double mDistError = 0;
    private static double mDistPastError = 0;

    private static double mError = 0;
    private static double mPastError = 0;
    private static boolean hasLock = false;
    private static RollingAverage roller = new RollingAverage(3);
    private static RollingAverage distRoller = new RollingAverage(5);

    private static Limelight mActiveCam = Robot.limePanel;
    
    public static boolean grabVisionData(){
        mActiveCam.setCamMode(CAM_MODE.VISION);
        mActiveCam.setLED(LED_STATE.ON);
        if (mActiveCam.hasTarget()){
            roller.add(mActiveCam.tX());
            mGyroTarget =  Drivetrain.getInstance().getGyroAngle() - roller.getAverage();
            hasLock = true;
            return true;
        }
        hasLock = false;
        return false;
    }

    public static double throttleCorrection(){

        mActiveCam.setTargetMode(Robot.targetMode);

        if (!hasLock){
            if (!grabVisionData()){
                return 0; //check to make sure you have target
            }else{
                hasLock = true;
            }
        }else{
            if (mActiveCam.hasTarget()){
                distRoller.add(mActiveCam.tY());
                mDistPastError = mDistError;
                mDistError = mDistTarg - distRoller.getAverage();
            }else{
                return 0;
            }
        }

        // return 0;
        return (mDistPastError * kDriveGain) + ((mDistPastError - mDistError) * kDriveDerivGain);
    }

    public static double turnCorrection(){
        
        mActiveCam.setTargetMode(Robot.targetMode);

        if (!hasLock){
            if (!grabVisionData()){
                return 0; //check to make sure you have target
            }else{
                hasLock = true;
            }
        }else{
            if (mActiveCam.hasTarget()){
                    roller.add(mActiveCam.tX());
                    mGyroTarget =  Drivetrain.getInstance().getGyroAngle()  - roller.getAverage();
            }
        }

        mPastError = mError;
        mError = (mGyroTarget - Drivetrain.getInstance().getGyroAngle());

        return (mError * kTrackingGain) + ((mPastError - mError) * kDerivativeGain);
    }

    public static double[] getDriveSignal(){
        return new double[]{
            VisionHelper.throttleCorrection() - VisionHelper.turnCorrection(),
            VisionHelper.throttleCorrection() + VisionHelper.turnCorrection()
        };
    }

    public static void attemptAutoScore(){

        if (isCloseToScore()){
            Robot.autoPlace = true;
        }else{
            Robot.autoPlace = false;
        }

    }

    public static Limelight getActiveCam(){
        return mActiveCam;
    }

    public static void resetLock(){
        hasLock = false;
        mActiveCam.setLED(LED_STATE.OFF);
        mActiveCam.setCamMode(CAM_MODE.DRIVER);
    }

    public static boolean isCloseToScore(){
        return (mActiveCam.tY() < kPanelAutoScoreY);
    }

    public static void setTargetDist(double setPoint){
        mDistTarg = setPoint;
    }

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static boolean hasLock(){ return hasLock; }
}