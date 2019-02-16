package frc.util;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.buttons.Trigger;
import frc.util.talon.LimitableSRX;

public class DoubleLimitSwitch extends Trigger{

    private final DigitalInput mSwitchF;
    private final DigitalInput mSwitchB;
    private final LimitableSRX mController;

    public DoubleLimitSwitch(int portF, int portB, LimitableSRX controller){
        mSwitchF = new DigitalInput(portF);
        mSwitchB = new DigitalInput(portB);
        mController = controller;
    }

    @Override
    public boolean get(){

        if (!mSwitchF.get()){
            mController.limitForwardTravel(true);
        }else{
            mController.limitForwardTravel(false);
        }

        if (!mSwitchB.get()){
            mController.limitReverseTravel(true);
        }else{
            mController.limitReverseTravel(false);
        }

        return !mSwitchF.get() || !mSwitchB.get();
    }

}