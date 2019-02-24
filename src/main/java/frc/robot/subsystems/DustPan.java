package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class DustPan extends Subsystem{

    private static final int kPDPport = RobotMap.DustpanMap.kIntake - 1;
    private static final double kCurrentThreshold = 10;

    public static enum DustpanBoomState{
        EXTENDED,
        RETRACTED
    }

    public static enum DustpanIntakeState{
        ON,
        OFF,
        HOLD,
        UNJAM
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

    public Command deployCommand(boolean extend){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                if (Robot.respectPerimeter){
                    return true;
                }
                deploy( (extend) ? DustpanBoomState.EXTENDED : DustpanBoomState.RETRACTED);
                return true;
            }
        };
    }

    public Command intakeCommand(DustpanIntakeState state){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                intake(state);
                return true;
            }
        };
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
        }else if (state == DustpanIntakeState.HOLD){
            setRaw(0.25);
        }else if (state == DustpanIntakeState.UNJAM){
            setRaw(-0.25);
        }else{
            intake(false);
        }
    }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    public boolean hasPanel(){
        return (getRawCurrent() >= kCurrentThreshold);
    }

    public double getRawCurrent(){
        return Robot.pdp.getCurrent(kPDPport);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}