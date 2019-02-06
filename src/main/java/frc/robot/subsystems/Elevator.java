package frc.robot.subsystems;

import java.util.LinkedHashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.util.LimitSwitch;
import frc.util.LimitSwitch.Travel;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.LimitableSRX;
import frc.util.talon.CTREFactory;

public class Elevator extends Subsystem implements ILoggable {

    private static final double kP = 1;
    private static final double kI = 0;
    private static final double kD = 0;
    private static final double kF = 0;

    private static final int kMMacceleration = 0;
    private static final int kMMvelocity = 0;
    
    private LimitableSRX mMaster;
    private VictorSPX mSlave;

    @SuppressWarnings("unused")
    private LimitSwitch mLowLimit;

    public static enum Setpoint{
        HAND_CLR,
        FINGER_CLR,

        BOTTOM,
        CARGO_SHIP,
        FUEL_LOW,
        HATCH_MID,
        FUEL_MID,
        HATCH_HIGH,
        FUEL_HIGH,
    };

    private static final LinkedHashMap<Setpoint, Integer> heightSetpoints = new LinkedHashMap<>();


    private Elevator(){
        
        heightSetpoints.put(Setpoint.BOTTOM,     0);
        heightSetpoints.put(Setpoint.CARGO_SHIP, 0);
        heightSetpoints.put(Setpoint.FUEL_LOW,   0);
        heightSetpoints.put(Setpoint.HATCH_MID,  0);
        heightSetpoints.put(Setpoint.FUEL_MID,   0);
        heightSetpoints.put(Setpoint.HATCH_HIGH, 0);
        heightSetpoints.put(Setpoint.FUEL_HIGH,  0);
        heightSetpoints.put(Setpoint.HAND_CLR,   0);
        heightSetpoints.put(Setpoint.FINGER_CLR, 0);


        mMaster = new LimitableSRX(CTREFactory.createDefaultTalon(RobotMap.ElevatorMap.kMaster));
        mSlave = CTREFactory.createPermanentSlaveVictor(RobotMap.ElevatorMap.kSlave, mMaster);

        mLowLimit = new LimitSwitch(RobotMap.ElevatorMap.kSwitch, Travel.BACKWARD, mMaster);

        mMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        mMaster.config_kP(Constants.kCTREpidIDX, kP);
        mMaster.config_kI(Constants.kCTREpidIDX, kI);
        mMaster.config_kD(Constants.kCTREpidIDX, kD);
        mMaster.config_kF(Constants.kCTREpidIDX, kF);

        mMaster.configMotionAcceleration(kMMacceleration);
        mMaster.configMotionCruiseVelocity(kMMvelocity);

        mMaster.setSensorPhase(false);

        mMaster.setInverted(false);
        mSlave.setInverted(false);

        setupLogger();
    }

    public void setRaw(double percent){
        mMaster.set(ControlMode.PercentOutput, percent);
    }

    public void setPosition(int position){
        mMaster.set(ControlMode.MotionMagic, position);
    }

    public void setPosition(Setpoint setpoint){
        setPosition(heightSetpoints.get(setpoint));
    }

    @Override
    public Loggable setupLogger() {
        return null;
	}

    @Override
    protected void initDefaultCommand() { /* no op */ }

}