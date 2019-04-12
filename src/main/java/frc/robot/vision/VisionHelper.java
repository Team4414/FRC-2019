package frc.robot.vision;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
import frc.util.interpolation.InterpolatingDouble;
import frc.util.interpolation.InterpolatingTreeMap;

public class VisionHelper{

    //---------- Constants ----------

    private static final double kPanelAutoScoreY = 2.4; //3
    private static final double kBallAutoScoreY = 0.5;

    private static final double kTrackingGain = 0.0225; //0.0225
    private static final double kTurnIGain = 0.0000;
    private static final double kTrackingMuliplier = 1;
    // private static final double kTurnBallIGain = 0.0125;
    private static final double kDerivativeGain = -0.2; //-0.2

    public static final double kDriveGain = 1.1;
    private static final double kDriveDerivGain = 0;//-1.4
    private static final double kSkewGain = 0;

    private static final double kHasTargetRollerThreshold = 0.8;
    //-------------------------------

    private static double mGyroTarget = 0;
    private static double mDistTarg = 0;

    private static double mDistError = 0;
    private static double mDistPastError = 0;

    private static double mError = 0;
    private static double mPastError = 0;
    private static double mTurnErrorIAccum = 0;
    private static final double kTurnIZone = 8;
    private static boolean hasLock = false;
    private static boolean alreadyScored = false;
    private static RollingAverage roller = new RollingAverage(3);
    private static RollingAverage distRoller = new RollingAverage(5);
    private static RollingAverage hasTargetRoller = new RollingAverage(10);

    private static InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble> distanceLookup = new InterpolatingTreeMap<InterpolatingDouble, InterpolatingDouble>();

    private static Limelight mActiveCam = Robot.limePanel;

    public static void buildLookupTable(){
        distanceLookup.put(new InterpolatingDouble(0d), new InterpolatingDouble(0d));
    }
    
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
                distRoller.add(1 / mActiveCam.tArea());
                mDistPastError = mDistError;
                mDistError = distRoller.getAverage();
            }else{
                return 0;
            }
        }

        return Math.max(-0.8, Math.min(-0.2, -((mDistPastError * kDriveGain) + ((mDistPastError - mDistError) * kDriveDerivGain))));
    }

    public static void debugMessage(){
        SmartDashboard.putNumber("OUTPUT VEL", Math.max(-0.8, Math.min(-0.2, (mDistPastError * kDriveGain) + ((mDistPastError - mDistError) * kDriveDerivGain))));
        
        SmartDashboard.putNumber("Unbounded Output Vel", -(mDistPastError * kDriveGain) + ((mDistPastError - mDistError) * kDriveDerivGain));
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

                    if (Math.abs(roller.getAverage()) <= kTurnIZone){
                        mTurnErrorIAccum += roller.getAverage();
                    }else{
                        mTurnErrorIAccum = 0;
                    }

                    mGyroTarget =  roller.getAverage();
            }
        }

        mPastError = mError;
        mError = (mGyroTarget);

        return (mError * -kTrackingGain) * (isCloseToScore() ? kTrackingMuliplier : 1) 
        + ((mPastError - mError) * -kDerivativeGain) * (isCloseToScore() ? kTrackingMuliplier : 1)
        + (mTurnErrorIAccum * ((Robot.activeSide == Side.PANEL) ? kTurnIGain : -kTurnIGain));
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
        double turnSignal = VisionHelper.turnCorrection();
        if (Robot.activeSide == Side.BALL){
            turnSigner = -1;
        }else{
            turnSigner = 1;
        }
        
        return new double[]{
            (turnSigner * (VisionHelper.throttleCorrection())) - turnSignal, //- (skewSigner * VisionHelper.skewCorrection()),
            (turnSigner * (VisionHelper.throttleCorrection())) + turnSignal  //+ (skewSigner * VisionHelper.skewCorrection())
        };
    }

    public static void doDriveIn(){
        grabVisionData();
        // Drivetrain.getInstance().setMotionMagicDrive(
        //     distanceLookup.getInterpolated(new InterpolatingDouble(mActiveCam.tY())).value,
        //     Drivetrain.getInstance().getGyroAngle() + mActiveCam.tX());

        // Drivetrain.getInstance().setMotionMagicDrive(2, 0);
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

    public static boolean hasTarget(){
        hasTargetRoller.add( (mActiveCam.hasTarget()) ? 1 : 0);
        return hasTargetRoller.getAverage() >= kHasTargetRollerThreshold;
    }

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static boolean hasLock(){ return hasLock; }
}