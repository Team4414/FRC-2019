package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.LimitSwitch;
import frc.util.LimitSwitch.Travel;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.LimitableSRX;
import frc.util.talon.CTREFactory;

public class Climber extends Subsystem implements ILoggable {

    private static final double kClimbPower = 0;
    private static final double kRetractPower = 0;

    private final LimitableSRX mClimber;

    @SuppressWarnings("unused")
    private final LimitSwitch mTopSwitch, mLowSwitch;
    

    public static Climber instance;
    public static Climber getInstance(){
        if (instance == null)
            instance = new Climber();
        return instance;
    }
    private Climber(){
        mClimber = new LimitableSRX(CTREFactory.createDefaultTalon(RobotMap.ClimberMap.kClimber));

        mTopSwitch = new LimitSwitch(RobotMap.ClimberMap.kSwitchUp, Travel.FORWARD, mClimber);
        mLowSwitch = new LimitSwitch(RobotMap.ClimberMap.kSwitchDown, Travel.BACKWARD, mClimber);
    }

    @Override
    protected void initDefaultCommand() {}

    public void climb (boolean climb){
        mClimber.set(ControlMode.PercentOutput, (climb) ? Math.abs(kClimbPower) : 0);
    }

    public void retract (boolean retract){
        mClimber.set(ControlMode.PercentOutput, (retract) ? -Math.abs(kRetractPower) : 0);
    }
    
    @Override
    public Loggable setupLogger() {
        return null;
    }

}