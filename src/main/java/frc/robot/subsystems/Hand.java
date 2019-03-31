package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Hand extends Subsystem{

    private static final double kDetectThreshold = 1.5;

    public static HandState handState;
    public static enum HandState{   
        OFF,
        HOLDING,
        INTAKING,
        DROP
    }

    private static final double kHoldPwr = 0.2;
    private static final double kIntakePwr = 1;
    private static final double kDropPwr = -0.8;

    private VictorSPX mHolder;
    private AnalogInput mSensor;

    private static Hand instance;
    public static Hand getInstance(){
        if (instance == null)
            instance = new Hand();
        return instance;
    }

    private Hand(){
        mHolder = CTREFactory.createVictor(RobotMap.HandMap.kHand);
        handState = HandState.OFF;
        mSensor = new AnalogInput(RobotMap.HandMap.kSensorPort);

        mHolder.setInverted(false);
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
        handState = state;
    }

    public Command setHandCommand(HandState state){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                set(state);
                return true;
            }
        };
    }

    public void setRaw(double pwr){
        mHolder.set(ControlMode.PercentOutput, pwr);
    }

    public boolean hasBall(){
        return (getSensorVoltage() >= kDetectThreshold);
    }

    public double getSensorVoltage(){
        return mSensor.getVoltage();
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}