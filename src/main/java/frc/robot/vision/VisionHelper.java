package frc.robot.vision;

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
    private static final double kInPoint = 6;
    private static final double kOutPoint = 1;

    private static final double kTrackingGain = 0.017;
    private static final double kDerivativeGain = -0.17;
    //-------------------------------

    private static double mGyroTarget = 0;
    private static double mError = 0;
    private static double mPastError = 0;
    private static boolean hasLock = false;
    private static RollingAverage roller = new RollingAverage(3);

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

    public static double turnCorrection(){
        
        mActiveCam.setTargetMode(Robot.targetMode);

        if (!hasLock){
            if (!grabVisionData()){
                return 0; //check to make sure you have target
            }else{
                hasLock = true;
            }
        }else{
            if (mActiveCam.tArea() < 8){
                if (mActiveCam.hasTarget()){
                    roller.add(mActiveCam.tX());
                    mGyroTarget =  Drivetrain.getInstance().getGyroAngle()  - roller.getAverage();
                }
            }else{
                mActiveCam.setLED(LED_STATE.OFF);
                mActiveCam.setCamMode(CAM_MODE.DRIVER);
            }
        }

        mPastError = mError;
        mError = (mGyroTarget - Drivetrain.getInstance().getGyroAngle());

        return (mError * kTrackingGain) + ((mPastError - mError) * kDerivativeGain);
        // if (mActiveCam.tArea() < 2){    
        //     return (mActiveCam.tX() * -kTrackingGain);
        // }else{
        //     return 0;
        // }
    }

    public static void resetLock(){
        hasLock = false;
        mActiveCam.setLED(LED_STATE.OFF);
        mActiveCam.setCamMode(CAM_MODE.DRIVER);
    }

    private static double getScaler(double dist){
        //(out - in) - (distance - in)
        //----------------------------
        //        (out - in)

        return 
            Math.max(0, 
            Math.min(1, 
                (((kOutPoint - kInPoint) - (dist - kInPoint)) / (kOutPoint - kInPoint))
            )
            );
    }

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static boolean hasLock(){ return hasLock; }
}