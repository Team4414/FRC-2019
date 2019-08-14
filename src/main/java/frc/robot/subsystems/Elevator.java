package frc.robot.subsystems;

import java.util.LinkedHashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Timer;
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

    
    // private static final double mConversionFactor = 17419d / 37797d;
    private static final double mConversionFactor = 1d/1.38d;

    private static final double kP = 0.5 * mConversionFactor; //0.5
    private static final double kI = 0; 
    private static final double kD = 0; //1.5
    private static final double kF = 0.07 * mConversionFactor; //0.06
    private static final double kArbFF = 0.1238; //0.1

    private static final int kMMacceleration = (int) (45000); //45000
    private static final int kMMvelocity = (int) (9000); //17000

    private static final int kTopLimit = (int) (73000); //72387

    private static final int kElevatorTolerance = (int) (1000);
    private static final int kDropForPanelClearDistance = (int) (700);

    private static int mZeroOffset = 0;
    private static boolean mNeedsZero; 
    
    private LimitableSRX mMaster;
    private VictorSPX mSlave;

    private LimitSwitch mLowLimit;

    public static Setpoint currentState;

    private static boolean mLockElevator = false;

    public static enum Position{
        INTAKE,
        LOW,
        SECOND,
        MIDDLE,
        HIGH,
    }

    public static enum Setpoint{
        FINGER_CLR,

        STOW,
        PANEL_GRAB,
        BOTTOM,
        FLOOR_INTAKE,
        CARGO_SHIP,
        FUEL_STATION,
        FUEL_LOW,
        HATCH_MID,
        FUEL_MID,
        HATCH_HIGH,
        FUEL_HIGH,
    };

    private static final LinkedHashMap<Setpoint, Integer> heightSetpoints = new LinkedHashMap<>();

    
    public static int kHandThreshold;


    private Elevator(){
          
        heightSetpoints.put(Setpoint.BOTTOM,     -400);
        heightSetpoints.put(Setpoint.STOW,       1200); 
        heightSetpoints.put(Setpoint.PANEL_GRAB,  850); //19 79
        heightSetpoints.put(Setpoint.FLOOR_INTAKE, 0);
        heightSetpoints.put(Setpoint.CARGO_SHIP, 33000);
        heightSetpoints.put(Setpoint.FUEL_STATION, 33000);
        heightSetpoints.put(Setpoint.FUEL_LOW,   19475);
        heightSetpoints.put(Setpoint.HATCH_MID,  29932);
        heightSetpoints.put(Setpoint.FUEL_MID,   47573); //41300
        heightSetpoints.put(Setpoint.HATCH_HIGH, 57182);
        heightSetpoints.put(Setpoint.FUEL_HIGH,   72900); //69500
        heightSetpoints.put(Setpoint.FINGER_CLR, 5695);

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

        mMaster.setSensorPhase(true);
        mMaster.overrideLimitSwitchesEnable(false);

        mMaster.configPeakOutputForward(1);
        mMaster.configPeakOutputReverse(-1);

        mMaster.setInverted(true);
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

    public LimitableSRX getMaster(){
        return mMaster;
    }

    public boolean setPosition(int position){
        if (mNeedsZero)
            return false;
        if (mLockElevator)
            return false;
        mMaster.set(ControlMode.MotionMagic, (position) + mZeroOffset, DemandType.ArbitraryFeedForward, kArbFF);
        return true;
    }

    public boolean setPosition(int position, double arbFF){
        // if (mNeedsZero)
        //     return false;
        // if (mLockElevator)
        //     return false;
        mMaster.set(ControlMode.MotionMagic, (position) + mZeroOffset, DemandType.ArbitraryFeedForward, arbFF);
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
        };
    }

    public Command lockElevatorCommand(boolean lock){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                mLockElevator = lock;
                return true;
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
        return (mMaster.getClosedLoopTarget() - mMaster.getSelectedSensorPosition());
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

    public void lockElevator(boolean lock){
        mLockElevator = lock;
    }

    public Command dropElevatorABit(boolean drop){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                setPosition((int)(getSetpoint(currentState)) - ((drop) ? kDropForPanelClearDistance : -kDropForPanelClearDistance));
                return true;
            }
        };
    }

    public Command slowElevator(boolean slow){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                if (slow){
                    mMaster.configMotionAcceleration(kMMacceleration / 2);
                }else{
                    mMaster.configMotionAcceleration(kMMacceleration);
                }
                return true;
            }
        };
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

    private double mLastVel = 0;

    public double getAcceleration(){
        double returnMe;
        returnMe =  (mMaster.getSelectedSensorVelocity() - mLastVel) * 0.02;
        mLastVel = mMaster.getSelectedSensorVelocity();
        return returnMe;
    }  

    @Override
    public Loggable setupLogger() {
        return new Loggable("ElevatorLog"){
            @Override
            protected LogObject[] collectData() {
                return new LogObject[]{
                    new LogObject("Time", Timer.getFPGATimestamp()),
                    new LogObject("Target",  mMaster.getClosedLoopTarget()),
                    new LogObject("Position", mMaster.getSelectedSensorPosition()),
                    new LogObject("Error", mMaster.getClosedLoopError(0))
                };
            }
        };
	}

    @Override
    protected void initDefaultCommand() { /* no op */ }

}