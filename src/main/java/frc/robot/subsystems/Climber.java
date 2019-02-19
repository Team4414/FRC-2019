package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.LimitableSRX;
import frc.util.talon.CTREFactory;

public class Climber extends Subsystem implements ILoggable {

    private static final double kClimbPower = 0.25;
    private static final double kInitClimbPower = 0.1;
    private static final double kRetractPower = 0.25;

    private static final double kPullPower = 0.1;

    private static final int kCurrentLimit = 20;

    private final TalonSRX mClimber;
    private final VictorSPX mPuller;

    // private final Solenoid mPiston;

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
        // mPiston = new Solenoid(RobotMap.ClimberMap.kPullerPiston);

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

    public boolean getTopSwitch(){
        return !topSwitch.get();
    }

    public boolean getBotSwitch(){
        return !botSwitch.get();
    }

    public Command climbCommand(boolean waitOnSwitch){
        return new Command(){

            @Override
            protected void initialize() {
                setClimbRaw((waitOnSwitch) ? kClimbPower : kInitClimbPower);
            }
        
            @Override
            protected boolean isFinished() {
                if (!waitOnSwitch){
                    return true;
                }else{
                    return getTopSwitch();
                }
            }

            @Override
            protected void end() {
                setClimbRaw(0);
            }

            @Override
            protected void interrupted() {
                setClimbRaw(0);
            }
        };
    }

    public Command pullCommand(boolean pull){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                setPullRaw(kPullPower);
                return true;
            }
        };
    }

    public Command deployPistonCommand(boolean deploy){
        return new Command(){
        
            @Override
            protected boolean isFinished() {
                // mPiston.set(true);
                return true;
            }
        };
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