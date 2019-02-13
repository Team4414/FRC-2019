package frc.robot;

import java.util.LinkedHashMap;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;

public class OI{
    
    private static OI instance;
    public static OI getInstance(){
        if (instance == null)
            instance = new OI();
        return instance;
    }

    private static final int kThrottleNubID = 0;
    private static final int kTurnNubID = 1;
    private static final int kXboxID = 2;

    private double kThrottleStickOffset = 0;
    private double kTurnStickOffset = 0;

    private static final double kThrottleScaler = 1;
    private static final double kTurnScalar = 1;

    private static final int kTurnAxis = 0;
    private static final int kThrottleAxis = 1;

    private static final int[] kQuickTurnButtonIDs = new int[]{11,12};
    private static final int kVisionButtonID = 0;

    private Joystick throttleNub;
    private Joystick turnNub;
    private XboxController xbox;
     
    private OI(){
        
        throttleNub = new Joystick(kThrottleNubID);
        turnNub = new Joystick(kTurnNubID);
        xbox = new XboxController(kXboxID);

        kThrottleStickOffset = 0;
        kTurnStickOffset = 0;

        kThrottleStickOffset = getForward();
        kTurnStickOffset = getLeft();
    }

    public double getLeft(){
        return kTurnScalar * turnNub.getRawAxis(kTurnAxis) - kTurnStickOffset;
    }

    public double getForward(){
        return kThrottleScaler * throttleNub.getRawAxis(kThrottleAxis) - kThrottleStickOffset;
    }

    public boolean getQuickTurn(){
        for(int id: kQuickTurnButtonIDs){
            if(throttleNub.getRawButton(id))
                return true;
         }
        return false;
    }

    public XboxController getXbox(){
        return xbox;
    }

    public boolean getVision(){
        return turnNub.getRawButton(kVisionButtonID);
    }
}