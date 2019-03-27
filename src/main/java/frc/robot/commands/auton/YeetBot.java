package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Drivetrain;

public class YeetBot extends Command{

    double mPower, mTime, mStartTime;

    public YeetBot(double power, double time){
        mPower = power;
        mTime = time;
        mStartTime = 0;
    }

    @Override
    protected void initialize() {
        Ramsete.getInstance().stop();
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void execute() {
        Drivetrain.getInstance().setRawSpeed(mPower, mPower);
    }

    @Override
    protected boolean isFinished() {
        return (Timer.getFPGATimestamp() > (mTime + mStartTime));
    }

    @Override
    protected void end() {
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

}