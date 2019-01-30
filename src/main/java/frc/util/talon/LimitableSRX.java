package frc.util.talon;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;

public class LimitableSRX extends BaseMotorController{
    
    public LimitableSRX(int port){
        super(port);
    }

    public LimitableSRX(BaseMotorController srx){
        super(srx.getBaseID());
    }

    public void limitForwardTravel(boolean limit){
        configPeakOutputForward((limit) ? 0 : 1);
    }

    public void limitReverseTravel(boolean limit){
        configPeakOutputReverse(limit ? 0: -1);
    }

}