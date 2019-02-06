package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Finger extends Subsystem{


    private Solenoid mFinger;
    private Solenoid mArm;
    private DigitalInput mSwitch;

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
    }

    public void setArm(boolean extended){
        mArm.set(extended);
    }

    public void setFinger(boolean holding){
        mFinger.set(holding);
    }

    public boolean getSwitch(){
        return mSwitch.get()
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}