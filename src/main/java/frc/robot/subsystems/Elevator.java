package frc.robot.subsystems;

import java.util.LinkedHashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.robot.Robot.Side;
import frc.util.LimitSwitch;
import frc.util.LimitSwitch.Travel;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.LimitableSRX;
import frc.util.talon.CTREFactory;

public class Elevator extends Subsystem implements ILoggable {

    private static Elevator instance;
    public static Elevator getInstance(){
        if (instance == null)
            instance = new Elevator();
        return instance;
    }

    private static final double kP = 0.7; //1
    private static final double kI = 0; //000.1
    private static final double kD = 0;
    private static final double kF = 0.15;

    private static final int kMMacceleration = 10000; //40000 | 20000
    private static final int kMMvelocity = 4000; //8000

    private static final int kTopLimit = 35663;

    private static final int kElevatorTolerance = 500;

    private static int mZeroOffset = 0;
    private static boolean mNeedsZero; 
    
    private LimitableSRX mMaster;
    private VictorSPX mSlave;

    @SuppressWarnings("unused")
    private LimitSwitch mLowLimit;

    public static Setpoint currentState;

    public static enum Position{
        INTAKE,
        LOW,
        SECOND,
        MIDDLE,
        HIGH,
    }

    public static enum Setpoint{
        FINGER_CLR, //4000

        STOW,   //1600
        BOTTOM, //0
        FLOOR_INTAKE, //400
        CARGO_SHIP, //14000
        FUEL_LOW, //11000
        HATCH_MID, //16000
        FUEL_MID, //24732
        HATCH_HIGH, //29000
        FUEL_HIGH, //36291
    };

    private static final LinkedHashMap<Setpoint, Integer> heightSetpoints = new LinkedHashMap<>();

    
    public static int kHandThreshold;


    private Elevator(){
        
        heightSetpoints.put(Setpoint.BOTTOM,     0);
        heightSetpoints.put(Setpoint.STOW,       1600);
        heightSetpoints.put(Setpoint.FLOOR_INTAKE,400);
        heightSetpoints.put(Setpoint.CARGO_SHIP, 16000);
        heightSetpoints.put(Setpoint.FUEL_LOW,   8600);
        heightSetpoints.put(Setpoint.HATCH_MID,  16000);
        heightSetpoints.put(Setpoint.FUEL_MID,   21532);
        heightSetpoints.put(Setpoint.HATCH_HIGH, 29000);
        heightSetpoints.put(Setpoint.FUEL_HIGH,  35443);
        heightSetpoints.put(Setpoint.FINGER_CLR, 4000);

        kHandThreshold = heightSetpoints.get(Setpoint.FUEL_LOW);


        mMaster = new LimitableSRX(CTREFactory.createDefaultTalon(RobotMap.ElevatorMap.kMaster));
        mSlave = CTREFactory.createPermanentSlaveVictor(RobotMap.ElevatorMap.kSlave, mMaster);

        mMaster.configForwardSoftLimitThreshold(kTopLimit + mZeroOffset);
        mMaster.configReverseSoftLimitThreshold(mZeroOffset);

        mLowLimit = new LimitSwitch(RobotMap.ElevatorMap.kSwitch, Travel.BACKWARD, true, mMaster);

        mMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        mMaster.config_kP(Constants.kCTREpidIDX, kP);
        mMaster.config_kI(Constants.kCTREpidIDX, kI);
        mMaster.config_kD(Constants.kCTREpidIDX, kD);
        mMaster.config_kF(Constants.kCTREpidIDX, kF);

        mMaster.configMotionAcceleration(kMMacceleration);
        mMaster.configMotionCruiseVelocity(kMMvelocity);

        mMaster.setSensorPhase(true); //good
        mMaster.overrideLimitSwitchesEnable(false);

        mMaster.configPeakOutputForward(1);
        mMaster.configPeakOutputReverse(-1);

        mMaster.setInverted(true); //good
        mSlave.setInverted(InvertType.FollowMaster);

        mMaster.configClosedLoopPeakOutput(0, 1);

        mMaster.configForwardSoftLimitEnable(true);

        mMaster.setNeutralMode(NeutralMode.Brake);
        mSlave.setNeutralMode(NeutralMode.Brake);

        mNeedsZero = true;

        checkNeedsZero();

        setupLogger();
    }

    public void setRaw(double percent){
        mMaster.set(ControlMode.PercentOutput, percent);
    }

    public void setF(double val){
        mMaster.config_kF(0, val);
    }

    public boolean setPosition(int position){
        if (mNeedsZero)
            return false;
        mMaster.set(ControlMode.MotionMagic, position + mZeroOffset);
        return true;
    }

    public boolean setPosition(Setpoint setpoint){

        if (setPosition(heightSetpoints.get(setpoint))){
            currentState = setpoint;
            return true;
        }

        return false;
    }

    public Command jogElevatorCommand(Position pos){
        return new Command(){

            @Override
            protected void initialize() {
                super.initialize();
                setPosition(getSignal(pos, Robot.activeSide));
            }
        
            @Override
            protected boolean isFinished() {
                return isAtSetpoint();
            }
        };
    }

    public Command jogElevatorCommand(Setpoint pos){
        return new Command(){

            @Override
            protected void initialize() {
                super.initialize();
                setPosition(pos);
            }
        
            @Override
            protected boolean isFinished() {
                return isAtSetpoint();
            }

            @Override
            protected void end() {
                super.end();
            }
        };
    }

    public double getZeroOffset(){ return mZeroOffset; }

    public static Setpoint getSignal(Position pos, Side side){
        Setpoint mSetpoint = currentState;
        if (side == Side.BALL){
            switch(pos){
                case INTAKE:
                    mSetpoint = Setpoint.BOTTOM;
                    break;
                case LOW:
                    mSetpoint = Setpoint.FUEL_LOW;
                    break;
                case SECOND:
                    mSetpoint = Setpoint.CARGO_SHIP;
                    break;
                case MIDDLE:
                    mSetpoint = Setpoint.FUEL_MID;
                    break;
                case HIGH:
                    mSetpoint = Setpoint.FUEL_HIGH;
                    break;
            }
        }else{
            switch(pos){
                case INTAKE:
                    mSetpoint = Setpoint.STOW;
                case LOW:
                    mSetpoint = Setpoint.STOW;
                    break;
                case SECOND:
                    mSetpoint = Setpoint.HATCH_MID;
                    break;
                case MIDDLE:
                    mSetpoint = Setpoint.HATCH_MID;
                    break;
                case HIGH:
                    mSetpoint = Setpoint.HATCH_HIGH;
                    break;
            }
        }
        return mSetpoint;
    }

    public double getError(){
        return mMaster.getClosedLoopTarget() - mMaster.getSelectedSensorPosition();
    }

    public boolean isAtSetpoint(){
        return (Math.abs(getError()) < kElevatorTolerance);
    }

    public static double getSetpoint(Setpoint point){
        return heightSetpoints.get(point);
    }

    public double getPosition(){
        return mMaster.getSelectedSensorPosition() - mZeroOffset;
    }

    public void zero(){
        mZeroOffset = mMaster.getSelectedSensorPosition();
        mMaster.configForwardSoftLimitThreshold(kTopLimit + mZeroOffset);
        mMaster.configReverseSoftLimitThreshold(mZeroOffset);
        currentState = Setpoint.BOTTOM;
        mNeedsZero = false;
    }

    public boolean getSwitch(){
        return mLowLimit.get();
    }

    public boolean checkNeedsZero(){
        if (getSwitch()){
            zero();
        }
        return mNeedsZero;
    }

    @Override
    public Loggable setupLogger() {
        return null;
	}

    @Override
    protected void initDefaultCommand() { /* no op */ }

}