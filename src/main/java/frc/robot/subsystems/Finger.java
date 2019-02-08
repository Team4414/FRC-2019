package frc.robot.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;

public class Finger extends Subsystem{

    public static enum ArmState{
        RETRACTED,
        EXTENDED
    }

    public static enum FingerState{
        HOLDING,
        OPEN
    }

    private Solenoid mFinger;
    private Solenoid mArm;
    private DigitalInput mSwitch;
    
    private ArmState mArmState;
    private FingerState mFingerState;

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

        mArmState = ArmState.RETRACTED;
        mFingerState = FingerState.OPEN;
    }

    public void setArm(boolean extended){
        mArm.set(extended);
        mArmState = (extended) ? ArmState.EXTENDED : ArmState.RETRACTED;
    }

    public void setFinger(boolean holding){
        mFinger.set(holding);
        mFingerState = (holding) ? FingerState.HOLDING : FingerState.OPEN;
    }

    public ArmState getArmState(){
        return mArmState;
    }

    public FingerState getFingerState(){
        return mFingerState;
    }

    public boolean getSwitch(){
        return mSwitch.get();
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}