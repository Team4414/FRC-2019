package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import frc.robot.Constants;
import frc.robot.subsystems.Drivetrain;
import frc.util.RamseteUtil;
import frc.util.kinematics.pos.RobotPos;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;

public class Ramsete extends RamseteUtil implements Runnable, ILoggable{

    private static Ramsete instance;
    private static boolean isRunning;

    private static final double kRamseteTimestep = 0.02;

    public static Ramsete getInstance(){
        if (instance == null)
            instance = new Ramsete(kRamseteTimestep);
        return instance;
    }

    private Notifier mNotifier;

    private final double kTimestep;

    public Ramsete(double timestep){
        super(Constants.kWheelBase, kRamseteTimestep);
        mNotifier = new Notifier(this);
        setupLogger();
        kTimestep = timestep;
        isRunning = false;
    }

    public void start(){
        isRunning = true;
        mNotifier.startPeriodic(kTimestep);
        forceStateUpdate();
    }

    public void stop(){
        isRunning = false;
        mNotifier.stop();
        Drivetrain.getInstance().setRawSpeed(0, 0); //stop the robot.
    }

    public static boolean isRunning(){
        return isRunning;
    }

    @Override
    public RobotPos getPose2d(){
        return Drivetrain.getInstance().getRobotPos();
    }

    @Override
    public void run() {
        this.update();

        Drivetrain.getInstance().setVelocity(this.getVels().getLeft(), this.getVels().getRight());
    }

    @Override
    public Loggable setupLogger(){
        return new Loggable("PathLog"){
            @Override
            protected LogObject[] collectData() {
                return new LogObject[]{
                    new LogObject("Time", Timer.getFPGATimestamp()),
                    new LogObject("Type", "P"),
                    new LogObject("XPos", getGoalX()),
                    new LogObject("YPos", getGoalY()),
                    new LogObject("Heading", getGoalTheta()),
                };
            }
        };
    }
}