package frc.robot.vision;

import frc.robot.Robot;
import frc.util.Limelight;

/**
 * 
 */
public class VisionHelper{

    //---------- Constants ----------
    private static final double kInPoint = 6;
    private static final double kOutPoint = 1;

    private static final double kTrackingGain = 0.05;
    private static final double kSkewGain = 0.5;
    //-------------------------------

    private static double mTracking = 0;
    private static double mSkew = 0;
    private static boolean hasLock = false;

    private static Limelight mActiveCam = Robot.limePanel;
    private static TargetEntry mEntry = TargetEntry.BLANK_DATA;

    public static double turnCorrection(){
        if (!mActiveCam.hasTarget()){
            hasLock = false;
            return 0;
        }
        hasLock = true;
        
        updateEntry();

        mTracking = (mActiveCam.tX() - mEntry.theta()) * (kTrackingGain * getScaler(mEntry.dist()));

        // System.out.println(getScaler(mEntry.dist()));
        // System.out.println(mEntry.dist());

        mSkew = mActiveCam.getSkew() * kSkewGain;

        return (mTracking + mSkew);

        // return mActiveCam.tX();
    }

    private static double getScaler(double dist){
        //(out - in) - (distance - in)
        //----------------------------
        //        (out - in)

        // return 
        //     Math.max(0, 
        //     Math.min(1, 
        //         (((kOutPoint - kInPoint) - (dist - kInPoint)) / (kOutPoint - kInPoint))
        //     )
        //     );
        return 1;
    }
    private static void updateEntry(){
        mEntry = TargetEntry.interpolate(Robot.visionTable, mActiveCam.tHeight(), mActiveCam.getCamSide());
    }

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static boolean hasLock(){ return hasLock; }
}