package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.TalonSRXFactory;

public class Elevator extends Subsystem implements ILoggable {

    
    private TalonSRX mMaster;

    @SuppressWarnings("unused")
    private VictorSPX mSlave;

    private Elevator(){
        mMaster = TalonSRXFactory.createDefaultTalon(RobotMap.ElevatorMap.kMaster);
        mSlave = TalonSRXFactory.createPermanentSlaveVictor(RobotMap.ElevatorMap.kSlave, mMaster);

        mMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);

        mMaster.config_kP(Constants.kCTREpidIDX, 1);
        mMaster.config_kI(Constants.kCTREpidIDX, 0);
        mMaster.config_kD(Constants.kCTREpidIDX, 0);
        mMaster.config_kF(Constants.kCTREpidIDX, 0);

        mMaster.setSensorPhase(false);

        mMaster.setInverted(false);
        mSlave.setInverted(false);

        setupLogger();
    }

    @Override
    public Loggable setupLogger() {
        return null;
	}

    @Override
    protected void initDefaultCommand() { /* no op */ }

}