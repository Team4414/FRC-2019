package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Robot;
import frc.robot.RobotMap;

public class Finger extends Subsystem{

    public static enum FingerArmState{
        RETRACTED,
        EXTENDED
    }

    public static enum FingerClapperState{
        HOLDING,
        OPEN
    }

    private Solenoid mFinger;
    private Solenoid mArm;
    private DigitalInput mSwitch;
    
    public static FingerArmState armState;
    public static FingerClapperState clapperState;

    private static Finger instance;
    public static Finger getInstance(){
        if (instance == null)
            instance = new Finger();
        return instance;
    }

    private Finger(){
        mFinger = new Solenoid(RobotMap.FingerMap.kFinger);
        mArm = new Solenoid(RobotMap.FingerMap.kArm);
        mSwitch = new DigitalInput(RobotMap.FingerMap.kSwitch);

        armState = FingerArmState.RETRACTED;
        clapperState = FingerClapperState.HOLDING;
    }

    public void setArm(boolean extended){
        mArm.set(extended);
        armState = (extended) ? FingerArmState.EXTENDED : FingerArmState.RETRACTED;
    }

    public void setArm(FingerArmState state){
        if (state == FingerArmState.EXTENDED){
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

    public void setFinger(boolean holding){
        mFinger.set(!holding);
        clapperState = (holding) ? FingerClapperState.HOLDING : FingerClapperState.OPEN;
    }

    public void setFinger(FingerClapperState state){
        if (state == FingerClapperState.HOLDING){
            setFinger(true);
        }else{
            setFinger(false);
        }
    }

    public Command setFingerCommand(boolean holding){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                setFinger(holding);
                return true;
            }
        };
    }

    public boolean getSwitch(){
        return mSwitch.get();
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}