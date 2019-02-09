package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class DustPan extends Subsystem{

    private static boolean mExtend = true;

    public static enum PanelBoomState{
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

    private PanelBoomState mBoomState;
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

        mBoomState = PanelBoomState.RETRACTED;
        mIntakeState = IntakeState.OFF;
    }

    public void deploy (boolean deploy){
        if (!mExtend){
            deploy = false;
        }
        mBoomState = (deploy) ? PanelBoomState.EXTENDED : PanelBoomState.RETRACTED;
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        mIntakeState = (run) ? IntakeState.ON: IntakeState.OFF;
    }

    public IntakeState getIntakeState(){ return mIntakeState; }
    public PanelBoomState getBoomState(){ return mBoomState; }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}