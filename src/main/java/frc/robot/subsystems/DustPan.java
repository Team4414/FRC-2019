package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class DustPan extends Subsystem{

    public static enum BoomState{
        EXTENDED,
        RETRACTED
    }

    public static enum IntakeState{
        ON,
        OFF
    }

    private static final double kIntakePwr = 1;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    private BoomState mBoomState;
    private IntakeState mIntakeState;

    private static DustPan instance;
    public static DustPan getInstance(){
        if (instance == null)
            instance = new DustPan();
        return instance;
    }

    private DustPan(){
        mPiston = new Solenoid(RobotMap.DustpanMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.DustpanMap.kIntake);

        mBoomState = BoomState.RETRACTED;
        mIntakeState = IntakeState.OFF;
    }

    public void deploy (boolean deploy){
        mPiston.set(deploy);
        mBoomState = (deploy) ? BoomState.EXTENDED : BoomState.RETRACTED;
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        mIntakeState = (run) ? IntakeState.ON: IntakeState.OFF;
    }

    public IntakeState getIntakeState(){ return mIntakeState; }
    public BoomState getBoomState(){ return mBoomState; }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}