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

    private static final double kTrackingGain = 0.01;
    private static final double kGyroGain = -0.00;
    //-------------------------------

    private static double mTracking = 0;
    private static double mGyroTarget = 0;
    private static boolean hasLock = false;

    private static Limelight mActiveCam = Robot.limePanel;
    
    public static boolean grabVisionData(){
        mActiveCam.setCamMode(CAM_MODE.VISION);
        mActiveCam.setLED(LED_STATE.ON);
        if (mActiveCam.hasTarget()){
            mGyroTarget =  Drivetrain.getInstance().getGyroAngle()  - mActiveCam.tX();
            hasLock = true;
            return true;
        }
        hasLock = false;
        return false;
    }

    public static double turnCorrection(){

        if (!hasLock){
            if (!grabVisionData()){
                return 0; //check to make sure you have target
            }else{
                hasLock = true;
            }
        }else{
            if (mActiveCam.tArea() < 8){
                if (mActiveCam.hasTarget()){
                    mGyroTarget =  Drivetrain.getInstance().getGyroAngle()  - mActiveCam.tX();
                }
            }
        }

        return ((mGyroTarget - Drivetrain.getInstance().getGyroAngle()) * kTrackingGain);

        // if (!mActiveCam.hasTarget() && mActiveCam.tArea() < 5){
        //     mActiveCam.setCamMode(CAM_MODE.VISION);
        //     mActiveCam.setLED(LED_STATE.ON);
            
        //     return (Drivetrain.getAngleCorrected(Drivetrain.getInstance().getGyroAngle()) * kGyroGain);
        // }else{
        //     Drivetrain.getInstance().zeroGyro();
        //     return (mActiveCam.tX() * kTrackingGain);
        // }

        // if (!hasLock){
        //     Drivetrain.getInstance().zeroGyro();
            
        //    
        //     if (mActiveCam.hasTarget()){
        //         mGyroTarget = mActiveCam.tX();
        //         hasLock = true;
        //     }
        // }

        // if (!hasLock){
        //     return 0;
        // }
        // mTracking = (Drivetrain.getAngleCorrected(Drivetrain.getInstance().getGyroAngle()) - mGyroTarget) * kTrackingGain;

        // System.out.println("EROR: " + (Drivetrain.getAngleCorrected(Drivetrain.getInstance().getGyroAngle()) - mGyroTarget));
        // return (mTracking);
    }

    public static void resetLock(){
        hasLock = false;
        mActiveCam.setLED(LED_STATE.OFF);
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