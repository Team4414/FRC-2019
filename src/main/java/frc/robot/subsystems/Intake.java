package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Intake extends Subsystem{

    private static final double kIntakePwr = 1;

    private Solenoid mPiston;
    private VictorSPX mIntake;

    private static Intake instance;
    public static Intake getInstance(){
        if (instance == null)
            instance = new Intake();
        return instance;
    }

    private Intake(){
        mPiston = new Solenoid(RobotMap.IntakeMap.kPiston);
        mIntake = CTREFactory.createVictor(RobotMap.IntakeMap.kIntake);
    }

    public void deploy (boolean deploy){
        mPiston.set(deploy);
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
    }

    public void setRaw(double pwr){
        mIntake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}