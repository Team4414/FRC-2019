package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.LimitableSRX;
import frc.util.talon.CTREFactory;

public class Climber extends Subsystem implements ILoggable {

    private static final double kClimbPower = 0;
    private static final double kRetractPower = 0;

    private static final int kCurrentLimit = 20;

    private final TalonSRX mClimber;
    private final VictorSPX mPuller;

    private final DigitalInput topSwitch;
    private final DigitalInput botSwitch;
    

    public static Climber instance;
    public static Climber getInstance(){
        if (instance == null)
            instance = new Climber();
        return instance;
    }
    private Climber(){
        mClimber = CTREFactory.createDefaultTalon(RobotMap.ClimberMap.kClimber);
        mPuller = CTREFactory.createVictor(RobotMap.ClimberMap.kPuller);

        mClimber.configPeakCurrentLimit(kCurrentLimit);
        mClimber.configPeakCurrentDuration(0);
        mClimber.configContinuousCurrentLimit(kCurrentLimit);

        topSwitch = new DigitalInput(RobotMap.ClimberMap.kSwitchUp);
        botSwitch = new DigitalInput(RobotMap.ClimberMap.kSwitchDown);

        mClimber.setInverted(true);

        setBrakeMode(true);

        mPuller.overrideSoftLimitsEnable(false);
        mPuller.overrideLimitSwitchesEnable(false);
    }

    @Override
    protected void initDefaultCommand() {}

    public void climb (boolean climb){
        mClimber.set(ControlMode.PercentOutput, (climb) ? Math.abs(kClimbPower) : 0);
    }

    public void retract (boolean retract){
        mClimber.set(ControlMode.PercentOutput, (retract) ? -Math.abs(kRetractPower) : 0);
    }

    public void setClimbRaw(double pwr){
        if (!topSwitch.get() && pwr > 0){
            pwr = 0;
        }
        if (!botSwitch.get() && pwr < 0){
            pwr = 0;
        }

        mClimber.set(ControlMode.PercentOutput, pwr);
        
    }

    public void setPullRaw(double pwr){
        mPuller.set(ControlMode.PercentOutput, pwr);
        System.out.println(pwr);
    }

    public void setBrakeMode(boolean brake){
        mClimber.setNeutralMode((brake) ? NeutralMode.Brake : NeutralMode.Coast);
    }
    
    @Override
    public Loggable setupLogger() {
        return null;
    }

}