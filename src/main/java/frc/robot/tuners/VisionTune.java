package frc.robot.tuners;

import java.util.LinkedHashMap;

import frc.robot.subsystems.Drivetrain;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.kinematics.pos.RobotPos;

public class VisionTune{

    private double mLastDistance;
    private boolean mStarted;
    private LinkedHashMap<Double, Double> mTable;

    private static VisionTune instance;
    public static VisionTune getInstance(){
        if (instance == null)
            instance = new VisionTune();
        return instance;
    }

    private VisionTune(){
        mTable = new LinkedHashMap<>();

        mLastDistance = 0;
        mStarted = false;
    }



    public boolean areaAutoTune(LinkedHashMap<Double, Double> lookupTable, double samplingIntervalFeet, double maxDistance){

        if (!mStarted){
            Limelight.setCamMode(CAM_MODE.VISION);
            Limelight.setLED(LED_STATE.ON);
            System.out.println("########## Starting Vision Tune ##########");
            mStarted = true;
        }

        double mDist = dist();

        if (mDist >= maxDistance){
            lookupTable = mTable;
            mStarted = false;
            System.out.println("########## Vision Tune Finished ##########");
            Limelight.setLED(LED_STATE.OFF);
            Limelight.setCamMode(CAM_MODE.DRIVER);
            return true;
        }

        if (mDist >= (mLastDistance + samplingIntervalFeet)){
            mLastDistance += samplingIntervalFeet;
            System.out.println("##### Point Added: " + mLastDistance + "\t" + Limelight.tArea() + " #####");
            mTable.put(mLastDistance, Limelight.tArea());
        }

        return false;
    }

    private double dist(){
        return (0.5 * (Drivetrain.getInstance().getRightSensorPosition() + Drivetrain.getInstance().getLeftSensorPosition()));
    }
    
}