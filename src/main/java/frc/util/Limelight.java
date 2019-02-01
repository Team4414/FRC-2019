package frc.util;

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

    private static final String kBallCamName = "limeball";
    private static final String kPanelCamName = "limepanel";

    private final String mTableName;

    public Limelight(CAM type){
        if (type == CAM.BALL_SIDE){
           mTableName = kBallCamName;
        } else {
            mTableName = kPanelCamName;
        }
    }
    public Limelight(String tableName){
        mTableName = tableName;
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
        Double[] y = getArray("tcorny");
        return ((y[0] - y[2]) + (y[1] - y[3])) / 2d;
    }

    public double getSkew(){
        Double[] xData = getArray("tcornx");
        Double[] yData = getArray("tcorny");

        Double[] topLeft  = new Double[]{xData[0], yData[0]};
        Double[] topRight = new Double[]{xData[1], yData[1]};
        Double[] botLeft  = new Double[]{xData[2], yData[2]};
        Double[] botRight = new Double[]{xData[3], yData[3]};

        double height = ((topLeft[1] - botLeft[1]) + (topRight[1] - botRight[1])) / 2d;

        return ((topLeft[1] - botLeft[1]) - (topRight[1] - botRight[1])) / height;       
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