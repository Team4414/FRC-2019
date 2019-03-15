package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class PPintake extends Subsystem{

    private static final double kHoldPwr = 2.5d/12d;
    private static final double kIntakePwr = 1;
    private static final double kScorePower = -1;
    private static final double kUnJamPower = -0.2;

    private static final double kPanelCurrentThreshold = 15;

    public static enum ArmState{
        RETRACTED,
        EXTENDED
    }

    public static enum PPState{
        HOLDING,
        SCORE,
        INTAKE,
        UNJAM,
        OFF
    }

    private VictorSPX mPP;
    private Solenoid mArm;
    
    public static ArmState armState;
    public static PPState ppState;

    private static PPintake instance;
    public static PPintake getInstance(){
        if (instance == null)
            instance = new PPintake();
        return instance;
    }

    private PPintake(){
        mPP = CTREFactory.createVictor(RobotMap.PPintakeMap.kPP);
        mArm = new Solenoid(RobotMap.PPintakeMap.kArm);

        mPP.setInverted(true);

        armState = ArmState.RETRACTED;
        ppState = PPState.OFF;
    }

    public void setArm(boolean extended){
        mArm.set(extended);
        armState = (extended) ? ArmState.EXTENDED : ArmState.RETRACTED;
    }

    public void setArm(ArmState state){
        if (state == ArmState.EXTENDED){
            setArm(true);
        }else{
            setArm(false);
        }
    }

    public Command setArmCommand(boolean extended){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                if (Robot.respectPerimeter){
                    return true;
                }
                setArm(extended);
                return true;
            }
        };
    }

    public void setPP(PPState state){
        switch(state){
            case HOLDING:
                mPP.set(ControlMode.PercentOutput, kHoldPwr);
                break;
            case SCORE:
                mPP.set(ControlMode.PercentOutput, kScorePower);
                break;
            case INTAKE:
                mPP.set(ControlMode.PercentOutput, kIntakePwr);
                break;
            case UNJAM:
                mPP.set(ControlMode.PercentOutput, kUnJamPower);
                break;
            case OFF:
                mPP.set(ControlMode.PercentOutput, 0);
                break;
        }
        ppState = state;
    }

    public Command setPPCommand(PPState state){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                setPP(state);
                return true;
            }
        };
    }

    public boolean hasPanel(){
        return 
            (Robot.pdp.getCurrent(RobotMap.PPintakeMap.kPP - 1) > kPanelCurrentThreshold);
    }

    public Command waitForPPCommand(){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                return hasPanel();
            }
        };
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}