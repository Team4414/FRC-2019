package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.VisionHelper;
import frc.util.RamseteUtil.Status;
import frc.util.kinematics.pos.RobotPos;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

public class MoveCommand extends Command{

    private Trajectory mPath;
    private boolean mInvertPath;
    private boolean mLookForVision;
    private boolean mIsFirstPath;

    private static boolean lastPathInverted = false;

    public MoveCommand(Trajectory path){
        this(path, false, false);
    }

    public MoveCommand(Trajectory path, boolean invertPath, boolean lookForVision){
        mPath = path;
        if(lastPathInverted != invertPath){
            mInvertPath = true;
            System.out.println("Pathnasdf");
        }else{
            mInvertPath = false;
        }
        mIsFirstPath = false;
        lastPathInverted = invertPath;
        mLookForVision = lookForVision;
    }

    public MoveCommand(Trajectory path, boolean invertPath, boolean lookForVision, boolean isFirstPath){
        this(path, invertPath, lookForVision);
        mIsFirstPath = isFirstPath;
    }

    @Override
    protected void initialize() {
        Ramsete.getInstance().start();
        // System.out.println("INITIAL POSIIION:\t\t" + Drivetrain.getInstance().getRobotPos().getX() + "\t\t\t" + Drivetrain.getInstance().getRobotPos().getY());
        // System.out.println(((mInvertPath) ? -1 : 1) * mPath.get(0).x +"\t\t\t\t" + ((mInvertPath) ? 1 : 1) * mPath.get(0).y + "\t\t\t\t" + (mInvertPath ? -1 : 1) *  Pathfinder.r2d(mPath.get(0).heading));
        // Drivetrain.getInstance().zeroSensor();
        if(mIsFirstPath){
            Drivetrain.getInstance().setOdometery(new RobotPos(((mInvertPath) ? -1 : 1) * mPath.get(0).x, ((mInvertPath) ? 1 : -1) * mPath.get(0).y, (mInvertPath ? -1 : -1) *  Pathfinder.r2d(mPath.get(0).heading)));
        }else{
            Drivetrain.getInstance().setOdometery(new RobotPos(Drivetrain.getInstance().getRobotPos(), mInvertPath));
        }

        System.out.println("INITIAL POSIIION:\t\t" + Drivetrain.getInstance().getRobotPos().getX() + "\t\t\t" + Drivetrain.getInstance().getRobotPos().getY() + "\t\t\t\t" + Drivetrain.getInstance().getRobotPos().getHeading());
        System.out.println(((mInvertPath) ? -1 : 1) * mPath.get(0).x +"\t\t\t\t" + ((mInvertPath) ? 1 : 1) * mPath.get(0).y + "\t\t\t\t" + (mInvertPath ? -1 : 1) *  Pathfinder.r2d(mPath.get(0).heading));

        if (!Ramsete.isRunning()){
            System.out.println("!!!!!!!!!! Attempted to start movement without starting Ramsete Controller !!!!!!!!!!");
        }else{
            Ramsete.getInstance().trackPath(mPath, mInvertPath);
        }
        System.out.println("Starting Move");
        Ramsete.getInstance().forceStateUpdate();
    }

    @Override
    protected void execute() {
        // System.out.println("RObotX:" + Drivetrain.getInstance().getRobotPos().getX() + "\t\t\tROBOT Y:" + Drivetrain.getInstance().getRobotPos().getY());
    }

    @Override
    protected boolean isFinished() {
        return Ramsete.getStatus() == Status.STANDBY || (mLookForVision && Robot.limePanel.hasTarget());
    }

    @Override
    protected void end() {
        Ramsete.getInstance().stop();
        System.out.println("Move Finished");
        Drivetrain.getInstance().setRawSpeed(0, 0);
    }

    @Override
    protected void interrupted() {
        Ramsete.getInstance().stop();
        System.out.println("Move Interrupted");
    }

}