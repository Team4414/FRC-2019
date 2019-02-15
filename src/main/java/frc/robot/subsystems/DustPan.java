package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class DustPan extends Subsystem{

    public static enum DustpanBoomState{
        EXTENDED,
        RETRACTED
    }

    public static enum DustpanIntakeState{
        ON,
        OFF
    }

    private static final double kIntakePwr = 1;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    public static DustpanBoomState boomState;
    public static DustpanIntakeState intakeState;

    private static DustPan instance;
    public static DustPan getInstance(){
        if (instance == null)
            instance = new DustPan();
        return instance;
    }

    private DustPan(){
        mPiston = new Solenoid(RobotMap.DustpanMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.DustpanMap.kIntake);

        mIntake.setInverted(true);

        boomState = DustpanBoomState.RETRACTED;
        intakeState = DustpanIntakeState.OFF;
    }

    public void deploy (boolean deploy){
        mPiston.set(deploy);
        boomState = (deploy) ? DustpanBoomState.EXTENDED : DustpanBoomState.RETRACTED;
    }

    public void deploy(DustpanBoomState state){
        if (state == DustpanBoomState.EXTENDED){
            deploy(true);
        }else{
            deploy(false);
        }
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
        intakeState = (run) ? DustpanIntakeState.ON: DustpanIntakeState.OFF;
    }

    public void intake(DustpanIntakeState state){
        if (state == DustpanIntakeState.ON){
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