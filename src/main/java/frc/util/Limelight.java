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

    private static Limelight ballCam;
    private static Limelight panelCam;
    
    public static Limelight getInstance(CAM side){
        if (side == CAM.BALL_SIDE){
            if (ballCam == null)
                ballCam = new Limelight(kBallCamName);
            return ballCam;
        }

        if (panelCam == null)
            panelCam = new Limelight(kPanelCamName);
        return panelCam;
    }

    private final String mTableName;

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

    public double tHor(){
        return get("thor");
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
    
    private void set(String varName, double value){
        NetworkTableInstance.getDefault().getTable(mTableName).getEntry(varName).setNumber(value);
    }
}