package frc.util;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.util.talon.LimitableSRX;

public class LimitSwitch extends Trigger{

    private final DigitalInput mSwitch;
    private final LimitableSRX mController;
    private final Travel mTravelType;
    private final boolean mInverted;

    public static enum Travel{
        FORWARD,
        BACKWARD,
    };

    public LimitSwitch(int port, Travel type, boolean inverted, LimitableSRX controller){
        mSwitch = new DigitalInput(port);
        mController = controller;
        mTravelType = type;
        mInverted = inverted;
    }

    public LimitSwitch(int port, Travel type, LimitableSRX controller){
        mSwitch = new DigitalInput(port);
        mController = controller;
        mTravelType = type;
        mInverted = false;
    }

    @Override
    public boolean get(){
        if (getSwitch()){
            if (mTravelType == Travel.FORWARD){
                mController.limitForwardTravel(true);
            }

            if (mTravelType == Travel.BACKWARD){
                mController.limitReverseTravel(true);
            }
        } else {
            mController.limitForwardTravel(false);
            mController.limitReverseTravel(false);
        }

        return getSwitch();
    }

    private boolean getSwitch(){
        if (mInverted){
            return !mSwitch.get();
        }else{
            return mSwitch.get();
        }
    }

}