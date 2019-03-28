package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.PIDCommand;
import frc.robot.subsystems.Drivetrain;
import jaci.pathfinder.Pathfinder;

public class PIDTurn extends PIDCommand{

	private static final double THRESHOLD_ERROR = 2;
	private double angle;
	private double zeroGyro;
    private double finalTime, startTime;
    private double mGyroOffset;
	
    public PIDTurn(double desiredAngle, double timeInMillis) {
    	super( 0.0225,0,-0.225);
    	
        angle = desiredAngle;
        finalTime = timeInMillis;
    }

    protected void initialize() {
    	mGyroOffset = Drivetrain.getInstance().getGyroAngle();
    	startTime = Timer.getFPGATimestamp();
        System.out.println("GyroTurnStarted");
        Ramsete.getInstance().stop();
    }
	
    protected void execute() {
    	super.setSetpoint(angle * getSmoothingFunction(Timer.getFPGATimestamp(), startTime + finalTime));
    	// System.out.println("Gyro: " + getGyroHeading() + "\tSetpoint = " + super.getSetpoint());
//    	System.out.println(Gyro.getInstance().getFusedHeading());
    }

    protected boolean isFinished() {
    	//Time >= AllottedTime && DriveTrain Speeds < Threshold
//    	return (startTime + finalTime >= Timer.getFPGATimestamp() && 
//    			(Math.abs(DriveTrain.getInstance().getRightSpeed()) + Math.abs(DriveTrain.getInstance().getLeftSpeed())
//    			< RIOConfigs.getInstance().getConfigOrAdd("DRIVETRAIN_DRIVE_COMMAND_THRESHOLD", 0.05)));
    	return (Math.abs(super.getPIDController().getError()) < THRESHOLD_ERROR);
//    	return false;
    }

    protected void end() {
    	Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    protected void interrupted() {
    	this.end();
    }
    
    @Override
	protected double returnPIDInput() {
		return (-getGyroHeading());
	}
	@Override
	protected void usePIDOutput(double output) {
		Drivetrain.getInstance().setRawSpeed(output, -output);
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
		return Pathfinder.boundHalfDegrees(Drivetrain.getInstance().getGyroAngle());
	}
}