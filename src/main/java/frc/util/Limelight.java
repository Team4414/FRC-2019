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
     * @return if the camera has any valid targets
     */
    public static boolean hasTarget(){
        if ( get("tv") == 1 )
            return true;
        return false;
    }
    
    public static double tX(){
        return get("tx");
    }

    public static double tY(){
        return get("ty");
    }

    public static double tArea(){
        return get("ta");
    }

    public static double tHor(){
        return get("thor");
    }

    public static void setLED(LED_STATE state){
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

    public static void setCamMode(CAM_MODE mode){
        switch (mode){
            case VISION:
                set("camMode", 0);
                break;
            case DRIVER:
                set("camMode", 1);
                break;
        }
    }

    public static void setUSBCam(boolean isPrimary){
        set ("stream", (isPrimary) ? 2 : 0);
    }

    private static double get(String varName){
        return NetworkTableInstance.getDefault().getTable("limelight").getEntry(varName).getDouble(0);
    }
    
    private static void set(String varName, double value){
        NetworkTableInstance.getDefault().getTable("limelight").getEntry(varName).setNumber(value);
    }
}