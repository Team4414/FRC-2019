package frc.robot.subsystems;

import edu.wpi.first.wpilibj.PWMSpeedController;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.command.Subsystem;

public class SignalLEDS extends Subsystem{
    public static enum LightPattern{
        TEAL_SOLID, //0.79
        RED_STROBE, //-0.11
        WAVES, //-0.41
        GREEN_SOLID, //0.73
    }

    private PWMSpeedController ledDriver;

    @Override
    protected void initDefaultCommand() {
    }

    private static SignalLEDS instance;
    public static SignalLEDS getInstance(){
        if (instance == null)
            instance = new SignalLEDS();
        return instance;
    }
    private SignalLEDS(){
        ledDriver = new Talon(0);
    }

    public void set(LightPattern set){
        switch(set){
            case TEAL_SOLID:
                ledDriver.set(0.73);
                break;
            case RED_STROBE:
                ledDriver.set(-0.13);
                break;
            case WAVES:
                ledDriver.set(-0.41);
                break;
            case GREEN_SOLID:
                ledDriver.set(0.73);
                break;
        }
    }

}