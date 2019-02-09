package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Intake extends Subsystem{

    public static enum BallBoomState{
        RETRACTED,
        EXTENDED
    }

    public static enum WheelState{
        ON,
        OFF
    }

    private static final double kIntakePwr = 1;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    private WheelState mIntakeState;
    private BallBoomState mBoomState;

    private static Intake instance;
    public static Intake getInstance(){
        if (instance == null)
            instance = new Intake();
        return instance;
    }

    private Intake(){
        mPiston = new Solenoid(RobotMap.IntakeMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.IntakeMap.kIntake);

        mIntakeState = WheelState.OFF;
        mBoomState = BallBoomState.RETRACTED;
    }

    public void deploy (boolean deploy){
        mPiston.set(deploy);
        mBoomState = (deploy) ? BallBoomState.EXTENDED : BallBoomState.RETRACTED;
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        mIntakeState = (run) ? WheelState.ON : WheelState.OFF; 
    }

    public WheelState  getIntakeState(){  return mIntakeState; }
    public BallBoomState getBoomState()  { return mBoomState;   }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}