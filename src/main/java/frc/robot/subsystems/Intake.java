package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.RobotMap;
import frc.util.talon.CTREFactory;

public class Intake extends Subsystem{

    private static final double kIntakePwr = 1;

    private Solenoid piston;
    private VictorSPX intake;

    private static Intake instance;
    public static Intake getInstance(){
        if (instance == null)
            instance = new Intake();
        return instance;
    }

    private Intake(){
        piston = new Solenoid(RobotMap.IntakeMap.kPiston);
        intake = CTREFactory.createVictor(RobotMap.IntakeMap.kIntake);
    }

    public void deploy (boolean deploy){
        piston.set(deploy);
    }

    public void intake (boolean run){
        setRaw( (run) ? kIntakePwr : 0 );
    }

    public void setRaw(double pwr){
        intake.set(ControlMode.PercentOutput, pwr);
    }

    @Override
    protected void initDefaultCommand() { /* no op */ }

}