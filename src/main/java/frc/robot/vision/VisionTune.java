package frc.robot.vision;

import java.util.ArrayList;

import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.logging.CSVLogger;

public class VisionTune{

    private double mLastDistance;
    private boolean mStarted;
    private ArrayList<Object> mEntries;

    // Which camera to use for the tuning procedure
    private static final Limelight mTuneCam = Robot.limePanel;

    private static VisionTune instance;
    public static VisionTune getInstance(){
        if (instance == null)
            instance = new VisionTune();
        return instance;
    }

    private VisionTune(){
        mEntries = new ArrayList<>();

        mLastDistance = 0;
        mStarted = false;
    }



    public boolean areaAutoTune(double samplingIntervalFeet, double maxDistance){

        if (!mStarted){
            mTuneCam.setCamMode(CAM_MODE.VISION);
            mTuneCam.setLED(LED_STATE.ON);
            System.out.println("########## Starting Vision Tune ##########");
            mStarted = true;
            Drivetrain.getInstance().zeroSensor();
        }

        double mDist = dist();

        if (mDist >= maxDistance){
            mStarted = false;
            System.out.println("########## Vision Tune Finished ##########");
            mTuneCam.setLED(LED_STATE.OFF);
            mTuneCam.setCamMode(CAM_MODE.DRIVER);
            CSVLogger.logCSV("VisionLookup", mEntries);
            return true;
        }

        if (mDist >= (mLastDistance + samplingIntervalFeet)){
            mLastDistance += samplingIntervalFeet;
            System.out.println("##### Point Added: " + mLastDistance + "\t" + mTuneCam.tHeight() + " #####");
            mEntries.add(new TargetEntry(mDist, mTuneCam.tX(), mTuneCam.tHeight()));
        }

        return false;
    }

    private double dist(){
        //distance given by encoders
        return (0.5 * (Drivetrain.getInstance().getRightSensorPosition() + Drivetrain.getInstance().getLeftSensorPosition()));
    }
    
}