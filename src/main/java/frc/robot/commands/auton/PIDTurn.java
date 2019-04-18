package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drivetrain;
import jaci.pathfinder.Pathfinder;

public class PIDTurn extends PIDCommand{

	private static final double THRESHOLD_ERROR = 2;
	private double angle;
	private double zeroGyro;
    private double finalTime, startTime;
	private double mGyroOffset;
	private double mOutput;
	
    public PIDTurn(double desiredAngle, double timeInMillis) {
		// super( 0.00125,0, 0.125);
		super(0.018, 0, 0.07);
		// super(1, 0, 0);
    	
        angle = desiredAngle;
		finalTime = timeInMillis;
		mOutput = 100;
    }

    protected void initialize() {
    	mGyroOffset = getGyroHeading();
    	// startTime = Timer.getFPGATimestamp();
        // System.out.println("GyroTurnStarted");
		// Ramsete.getInstance().stop();
		super.setSetpoint(angle);
    }
	
    protected void execute() {
    	// super.setSetpoint(angle);
    	// System.out.println("Gyro: " + getGyroHeading() + "\tSetpoint = " + super.getSetpoint());
   	// System.out.println(Gyro.getInstance().getFusedHeading());
		System.out.println(super.getPIDController().getError());
		// System.out.println();
    }

	double lastHdg;
	double hdg;
    protected boolean isFinished() {
		lastHdg = hdg;
		hdg = Drivetrain.getInstance().getGyroAngle();
		System.out.println(hdg-lastHdg);
    	//Time >= AllottedTime && DriveTrain Speeds < Threshold
//    	return (startTime + finalTime >= Timer.getFPGATimestamp() && 
//    			(Math.abs(DriveTrain.getInstance().getRightSpeed()) + Math.abs(DriveTrain.getInstance().getLeftSpeed())
//    			< RIOConfigs.getInstance().getConfigOrAdd("DRIVETRAIN_DRIVE_COMMAND_THRESHOLD", 0.05)));
    	// return (Math.abs(super.getPIDController().getError()) < THRESHOLD_ERROR);
		//    return false;
		return (Math.abs(super.getPIDController().getError()) < 3) && (Math.abs(hdg - lastHdg) < 0.5);
    }

    protected void end() {
		Drivetrain.getInstance().setRawSpeed(0, 0);
		System.out.println("DONE");
    }

    protected void interrupted() {
    	this.end();
    }
    
    @Override
	protected double returnPIDInput() {
		// System.out.println(getGyroHeading() - mGyroOffset);
		return (getGyroHeading() - mGyroOffset);
	}
	@Override
	protected void usePIDOutput(double output) {
		// System.out.println("OUTPUT:\t\t" + output);
		// SmartDashboard.putNumber("PIDTURNOUTPUT", output);
		// SmartDashboard.putNumber("PIDTURNERROR", super.getPIDController().getError());
		Drivetrain.getInstance().setRawSpeed(-output, output);
		mOutput = output;
	}
	
	private double getSmoothingFunction(double currentTime, double finalTime) {
		//Desmos Graph: https://www.desmos.com/calculator/3sls1cegnx
		// return (0.5 * (1 - (Math.cos(Math.PI  * (currentTime / finalTime)))));
		return 1;
	}
	
	private void zeroGyro() {
//		zeroGyro = Gyro.getInstance().getFusedHeading();
		// Drivetrain.getInstance().setAngleToZero();
	}
	private double getGyroHeading() {
		// double heading = Gyro.getInstance().getFusedHeading();
		// double finalAngle = heading;
		// if (heading > 180){
		// 	finalAngle = heading - 360;
		// }
		// return finalAngle;
		// return Pathfinder.boundHalfDegrees(Drivetrain.getInstance().getGyroAngle());
		return Drivetrain.getInstance().getGyroAngle();
	}
}