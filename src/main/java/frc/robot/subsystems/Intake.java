package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Intake extends Subsystem{

    public static enum IntakeBoomState{
        RETRACTED,
        EXTENDED
    }

    public static enum IntakeWheelState{
        ON,
        OFF,
        UNJAM,
        CLEAR
    }

    private static final double kIntakePwr = 0.75;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    public static IntakeWheelState wheelState;
    public static IntakeBoomState boomState;

    private static boolean mLocked;

    private static Intake instance;
    public static Intake getInstance(){
        if (instance == null)
            instance = new Intake();
        return instance;
    }

    private Intake(){
        mPiston = new Solenoid(RobotMap.IntakeMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.IntakeMap.kIntake);

        mIntake.configOpenloopRamp(0.3);

        mIntake.setInverted(true);

        wheelState = IntakeWheelState.OFF;
        boomState = IntakeBoomState.RETRACTED;

        mLocked = false;
    }

    public void deploy (boolean deploy){
        if (mLocked){
            return;
        }
        mPiston.set(deploy);
        boomState = (deploy) ? IntakeBoomState.EXTENDED : IntakeBoomState.RETRACTED;
    }

    public void deploy(IntakeBoomState state){
        if (state == IntakeBoomState.EXTENDED){
            deploy(true);
        }else{
            deploy(false);
        }
    }

    public Command deployCommand(boolean deploy){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                deploy(deploy);
                return true;
            }
        };
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        wheelState = (run) ? IntakeWheelState.ON : IntakeWheelState.OFF; 
    }

    public void intake(IntakeWheelState state){
        if (state == IntakeWheelState.ON){
            intake(true);
        }else if (state == IntakeWheelState.CLEAR){
            setRaw(-0.25);
        }else if (state == IntakeWheelState.UNJAM){
            setRaw(-1);
        }else{
            intake(false);
        }
    }

    public Command intakeCommand(IntakeWheelState state){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                intake(state);
                return true;
            }
        };
    }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    public boolean isLocked(){
        return mLocked;
    }

    public void lock(boolean lock){
        mLocked = lock;
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}