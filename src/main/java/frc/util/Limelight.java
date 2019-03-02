package frc.util;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot.Side;

/**
 * A Simple Driver for the Limelight
 */

public class Limelight{

    /**
     * An enum storing potential LED states.
     */
    public static enum LED_STATE{
        ON, OFF, BLINK, MANUAL;
    }

    /**
     * An enum storing potential Camera states.
     */
    public static enum CAM_MODE{
        VISION, DRIVER;
    }

    public static enum TARGET_MODE{
        LEFT, RIGHT, CENTER;
    }

    private static final int kFOVvert = 41;
    private static final int kFOVhor = 54;

    private static final int kResVert = 240;
    private static final int kResHor = 320;

    private static final double kCamOffset = 27d/2d;

    private static Side mSide;

    private static final int kHeightTarget = 6; //actual height of the target

    private static final String kBallCamName = "limelight-ball";
    private static final String kPanelCamName = "limelight-panel";
    
    private static final int kRollingHeightFilter = 5;

    private final String mTableName;
    private RollingAverage heightRoller;

    public Limelight(Side type){
        if (type == Side.BALL){
           mTableName = kBallCamName;
        } else {
            mTableName = kPanelCamName;
        }
        mSide = type;
        heightRoller = new RollingAverage(kRollingHeightFilter);
    }
    public Limelight(String tableName){
        mTableName = tableName;
        heightRoller = new RollingAverage(kRollingHeightFilter);
    }  

    /**
     * @return if the camera has any valid targets
     */
    public boolean hasTarget(){
        if ( get("tv") == 1 )
            return true;
        return false;
    }
    
    public double tX(){
        return get("tx");
    }

    public double tY(){
        return get("ty");
    }

    public double tArea(){
        return get("ta");
    }

    public double tHeight(){

        if (!hasTarget())
            return 0;
        Double[] xData = getArray("tcornx");
        Double[] yData = getArray("tcorny");
        
        if (xData.length < 4 || yData.length < 4){
            return 0;
        }

        ArrayList<Integer> rightPair = new ArrayList<>();
        ArrayList<Integer> leftPair = new ArrayList<>();

        int cnt;
        for (int i = 0; i < xData.length; i++){
            cnt = 0;
            for (int j = 0; j < xData.length; j++){
                if (xData[i] > xData[j]){
                    cnt++;
                }
            }
            if (cnt >= 2){
                rightPair.add(i);
            } else{
                leftPair.add(i);
            }
        }

        try{    
             heightRoller.add((
                Math.abs(yData[rightPair.get(0)] - yData[rightPair.get(1)]) + 
                Math.abs(yData[leftPair.get(0)] - yData[leftPair.get(1)]))
                * 0.5);
        }catch (Exception e){ return 0; }

        return heightRoller.getAverage();
    }

    public double getTheta(){
        return Math.acos(kCamOffset * getAbsDist());
    }

    public double getAbsDist(){
        //         h
        // d = -----------
        //      2tan( (h * vFOV) / ( 2 * resVert) )

        return (kHeightTarget / (2 * Math.tan((tHeight() * kFOVvert) / (2 *kResVert))));
    }

    public double getSkew(){
        if (!hasTarget())
            return 0;
        Double[] xData = getArray("tcornx");
        Double[] yData = getArray("tcorny");

        if (xData.length < 4 || yData.length < 4){
            return 0;
        }

        ArrayList<Integer> rightPair = new ArrayList<>();
        ArrayList<Integer> leftPair = new ArrayList<>();

        int cnt;
        for (int i = 0; i < xData.length; i++){
            cnt = 0;
            for (int j = 0; j < xData.length; j++){
                if (xData[i] > xData[j]){
                    cnt++;
                }
            }
            if (cnt >= 2){
                rightPair.add(i);
            } else{
                leftPair.add(i);
            }
        }

        int sign;

        if (Math.abs(yData[rightPair.get(0)] - yData[rightPair.get(1)]) > Math.abs(yData[leftPair.get(0)] - yData[leftPair.get(1)])){
            sign = 1;
        }else{
            sign = -1;
        }

        return sign * (get("thor") / get("tvert") - 2.2);
    }

    public void setLED(LED_STATE state){
        switch(state){
            case MANUAL:
                set("ledMode", 0);
                break;
            case ON:
                set("ledMode", 3);
                break;
            case OFF:
                set("ledMode", 1);
                break;
            case BLINK:
                set("ledMode", 2);
                break;
        }
    }

    public Command setLEDCommand(LED_STATE state){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                setLED(state);
                return true;
            }
        };
    }

    public void setCamMode(CAM_MODE mode){
        switch (mode){
            case VISION:
                set("camMode", 0);
                break;
            case DRIVER:
                set("camMode", 1);
                break;
        }
    }

    public void setUSBCam(boolean isPrimary){
        set ("stream", (isPrimary) ? 2 : 0);
    }

    public Side getCamSide(){
        return mSide;
    }

    public void setTargetMode(TARGET_MODE mode){
        if (mode == TARGET_MODE.CENTER){
            set("pipeline", 0);
        }
        if (mode == TARGET_MODE.LEFT){
            set("pipeline", 1);
        }
        if (mode == TARGET_MODE.RIGHT){
            set("pipeline", 2);
        }
    }

    private double get(String varName){
        return NetworkTableInstance.getDefault().getTable(mTableName).getEntry(varName).getDouble(0);
    }

    private Double[] getArray(String varName){
        return NetworkTableInstance.getDefault().getTable(mTableName).getEntry(varName).
        getDoubleArray(new Double[]{0d,0d});
    }
    
    private void set(String varName, double value){
        NetworkTableInstance.getDefault().getTable(mTableName).getEntry(varName).setNumber(value);
    }
}