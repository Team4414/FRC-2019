package frc.robot.commands.climbing;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;

public class Climb extends Command{

    //---------- Climb Konstants ----------
    private static final double kMaxClimbSpeed = 1;
    private static final double kPullerSpeed = -0.1;
    private static final double kDrivePullSpeed = 0;

    private static final double kHoldClimbSpeed = 0.4;

    private static final double kTimeToMaxSpeed = 2;
    private static final double kPullDelay = 1;
    //-------------------------------------

    private double mInitTime;

    @Override
    protected void initialize() {
        mInitTime = Timer.getFPGATimestamp();
        Robot.isClimbing = true;

        Climber.getInstance().deployPiston(true);
        Climber.getInstance().setPullRaw(kPullerSpeed);
        Climber.getInstance().setBrakeMode(true);
    }

    @Override
    protected void execute() {
        Climber.getInstance().setClimbRaw(getClimbSpeed());

        if (getTime() > kPullDelay){
            Climber.getInstance().setPullRaw(kPullerSpeed);
            Drivetrain.getInstance().setRawSpeed(kDrivePullSpeed, kDrivePullSpeed);
        }
    }

    @Override
    protected boolean isFinished() {
        return Climber.getInstance().getBotSwitch();
    }

    @Override
    protected void interrupted() {
        Climber.getInstance().setClimbRaw(kHoldClimbSpeed);
        Drivetrain.getInstance().setRawSpeed(0, 0);
        Robot.isClimbing = false;
    }

    private double getTime(){
        return Timer.getFPGATimestamp() - mInitTime;
    }

    private double getClimbSpeed(){
        // return Math.min(kMaxClimbSpeed, kMaxClimbSpeed * (getTime() / kTimeToMaxSpeed));
        return kMaxClimbSpeed;
    }   


}