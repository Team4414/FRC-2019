package frc.util;

import java.util.ArrayList;

import edu.wpi.first.networktables.NetworkTableInstance;

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

    /**
     * Which Limelight to use
     */
    public static enum CAM{
        BALL_SIDE, PANEL_SIDE;
    };

    private static final String kBallCamName = "limelight-ball";
    private static final String kPanelCamName = "limelight-panel";
    
    private static final int kRollingSkewFilter = 50;
    private static final int kRollingHeightFilter = 5;

    private final String mTableName;
    
    private static CAM side;
    private RollingAverage skewRoller;
    private RollingAverage heightRoller;

    public Limelight(CAM type){
        if (type == CAM.BALL_SIDE){
           mTableName = kBallCamName;
        } else {
            mTableName = kPanelCamName;
        }
        side = type;
        skewRoller = new RollingAverage(kRollingSkewFilter);
        heightRoller = new RollingAverage(kRollingHeightFilter);
    }
    public Limelight(String tableName){
        mTableName = tableName;
        skewRoller = new RollingAverage(kRollingSkewFilter);
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
             skewRoller.add((
                Math.abs(yData[rightPair.get(0)] - yData[rightPair.get(1)]) + 
                Math.abs(yData[leftPair.get(0)] - yData[leftPair.get(1)]))
                * 0.5);
        }catch (Exception e){ return 0; }

        return skewRoller.getAverage();
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


        try{
            heightRoller.add((Math.abs(
                        yData[leftPair.get(0)] - 
                        yData[leftPair.get(1)]
                    ) - 
                    Math.abs(
                        yData[rightPair.get(0)] - 
                        yData[rightPair.get(1)]
                    )) / tHeight());
        }catch (Exception e){
            return 0;
        }

        return heightRoller.getAverage();
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

    public CAM getCamSide(){
        return side;
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