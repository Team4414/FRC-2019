package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
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
        OFF
    }

    private static final double kIntakePwr = 1;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    public static IntakeWheelState wheelState;
    public static IntakeBoomState boomState;

    private static Intake instance;
    public static Intake getInstance(){
        if (instance == null)
            instance = new Intake();
        return instance;
    }

    private Intake(){
        mPiston = new Solenoid(RobotMap.IntakeMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.IntakeMap.kIntake);

        mIntake.setInverted(true);

        wheelState = IntakeWheelState.OFF;
        boomState = IntakeBoomState.RETRACTED;
    }

    public void deploy (boolean deploy){
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

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        wheelState = (run) ? IntakeWheelState.ON : IntakeWheelState.OFF; 
    }

    public void intake(IntakeWheelState state){
        if (state == IntakeWheelState.ON){
            intake(true);
        }else{
            intake(false);
        }
    }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}