package frc.robot.vision;

import org.opencv.photo.AlignExposures;

import frc.robot.OI;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.PPintake.ArmState;
import frc.robot.subsystems.PPintake.PPState;
import frc.util.Limelight;
import frc.util.RollingAverage;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;

public class VisionHelper{

    //---------- Constants ----------

    private static final double kPanelAutoScoreY = 3; //3
    private static final double kBallAutoScoreY = 0.5;

    private static final double kTrackingGain = 0.017;
    private static final double kDerivativeGain = -0.17;

    private static final double kDriveGain = 0.1;
    private static final double kDriveDerivGain = -0.15;

    private static final double kSkewGain = 0;
    //-------------------------------

    private static double mGyroTarget = 0;
    private static double mDistTarg = 0;

    private static double mDistError = 0;
    private static double mDistPastError = 0;

    private static double mError = 0;
    private static double mPastError = 0;
    private static boolean hasLock = false;
    private static boolean alreadyScored = false;
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


        return Math.max(-0.5, Math.min(0.5, (mDistPastError * kDriveGain) + ((mDistPastError - mDistError) * kDriveDerivGain)));
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

    public static double skewCorrection(){
        mActiveCam.setTargetMode(Robot.targetMode);

        if (!hasLock){
            if (!grabVisionData()){
                return 0; //check to make sure you have target
            }else{
                hasLock = true;
            }
        }else{
            if (mActiveCam.hasTarget()){
                    return  mActiveCam.tS() * kSkewGain;
            }
        }

        mPastError = mError;
        mError = (mGyroTarget - Drivetrain.getInstance().getGyroAngle());

        return (mError * kTrackingGain) + ((mPastError - mError) * kDerivativeGain);
    }

    public static double[] getDriveSignal(){
        double turnSigner = 1;
        double skewSigner = 1;
        double turnSignal = VisionHelper.turnCorrection();
        if (Robot.activeSide == Side.BALL){
            turnSigner = -1;
        }else{
            turnSigner = 1;
        }

        if (turnSignal > 0){
            skewSigner = -1;
        }else{
            skewSigner = 1;
        }
        
        return new double[]{
            (turnSigner * VisionHelper.throttleCorrection()) - turnSignal, //- (skewSigner * VisionHelper.skewCorrection()),
            (turnSigner * VisionHelper.throttleCorrection()) + turnSignal  //+ (skewSigner * VisionHelper.skewCorrection())
        };
    }

    public static void attemptAutoScore(){

        if (!mActiveCam.hasTarget()){
            return;
        }

        if (Robot.isStationGrab){
            return;
        }

        if (alreadyScored){
            return;
        }

        if (Robot.activeSide == Side.BALL){
            if (!OI.getInstance().getStationButton()){
                if (isCloseToScore()){
                    Hand.getInstance().set(HandState.DROP);
                    alreadyScored = true;
                }else{
                    Hand.getInstance().set(HandState.HOLDING);
                    alreadyScored = false;
                }
            }
        }else{
            if (!OI.getInstance().getStationButton()){
                PPintake.getInstance().setArm(ArmState.EXTENDED);

                if (isCloseToScore()){
                    PPintake.getInstance().setPP(PPState.SCORE);
                    alreadyScored = true;
                }else{
                    PPintake.getInstance().setPP(PPState.HOLDING);
                    alreadyScored = false;
                }
            }
        }
    }

    public static Limelight getActiveCam(){
        return mActiveCam;
    }

    public static void resetLock(){
        if (hasLock){
            // PPintake.getInstance().setArm(ArmState.RETRACTED);
            // PPintake.getInstance().setPP(PPState.OFF);
            // if (Hand.getInstance().hasBall()){
            //     Hand.getInstance().set(HandState.HOLDING);
            // }else{
            //     Hand.getInstance().set(HandState.OFF);
            // }
        }
        hasLock = false;
        alreadyScored = false;
        mActiveCam.setLED(LED_STATE.OFF);
    }

    public static boolean isCloseToScore(){
        if (Robot.activeSide == Side.BALL){
            return (mActiveCam.tY() <= kBallAutoScoreY);
        }else{
            return (mActiveCam.tY() <= kPanelAutoScoreY);
        }
    }

    public static void setTargetDist(double setPoint){
        mDistTarg = setPoint;
    }

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static boolean hasLock(){ return hasLock; }
}