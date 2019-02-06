package frc.robot.subsystems;

import java.util.LinkedHashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Hand extends Subsystem{

    public static enum HandState{
        OFF,
        HOLDING,
        INTAKING,
        DROP
    }

    private static final double kHoldPwr = 0.5;
    private static final double kIntakePwr = 1;
    private static final double kDropPwr = -1;

    private VictorSPX mHolder;
    private HandState mCurrentState;

    private static Hand instance;
    public static Hand getInstance(){
        if (instance == null)
            instance = new Hand();
        return instance;
    }

    private Hand(){
        mHolder = CTREFactory.createVictor(RobotMap.DustpanMap.kIntake);
        mCurrentState = HandState.OFF;
    }

    public void set(HandState state){
        switch (state){
            case OFF:
                setRaw(0);
                break;
            case HOLDING:
                setRaw(kHoldPwr);
                break;
            case INTAKING:
                setRaw(kIntakePwr);
                break;
            case DROP:
                setRaw(kDropPwr);
                break;
        }
        mCurrentState = state;
    }

    public void setRaw(double pwr){
        mHolder.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}