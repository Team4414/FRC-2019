package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;

public class Climber extends Subsystem implements ILoggable {

    private static final double kClimbPower = 0;
    private static final double kRetractPower = 0;

    private final TalonSRX climber;
    

    private Climber(){
        climber = new TalonSRX(RobotMap.ClimberMap.kClimber);
    }

    @Override
    protected void initDefaultCommand() {}

    public static Climber instance;
    public static Climber getInstance(){
        if (instance == null)
            instance = new Climber();
        return instance;
    }

    public void climb (boolean climb){
        climber.set(ControlMode.PercentOutput, (climb) ? Math.abs(kClimbPower) : 0);
    }

    public void retract (boolean retract){
        climber.set(ControlMode.PercentOutput, (retract) ? -Math.abs(kRetractPower) : 0);
    }
    
    @Override
    public Loggable setupLogger() {
        return null;
    }

}