package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.VisionHelper;
import frc.util.RamseteUtil.Status;
import frc.util.kinematics.pos.RobotPos;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

public class MoveCommand extends Command{

    public enum FieldSide{
        LEFT,
        RIGHT
    }

    public enum VisionCancel{
        CANCEL_ON_VISION,
        RUN_FULL_PATH
    }

    public enum ZeroOdometeryMode{
        FIRST_PATH,
        NO_ZERO
    }

    private Trajectory mPath;
    private boolean mInvertPath;
    private VisionCancel mLookForVision;
    private boolean mIsFirstPath;
    private FieldSide mIsRightPath;

    private static Side lastPathInverted = Side.BALL;

    public MoveCommand(Trajectory path){
        this(path, Side.BALL, VisionCancel.RUN_FULL_PATH);
    }

    public MoveCommand(Trajectory path, Side invertPath, VisionCancel lookForVision){
        mPath = path;
        if(invertPath == Side.PANEL){
            mInvertPath = true;
        }else{
            mInvertPath = false;
        }
        mIsFirstPath = false;
        mIsRightPath = FieldSide.LEFT;
        lastPathInverted = invertPath;
        mLookForVision = lookForVision;
    }

    public MoveCommand(Trajectory path, Side invertPath, VisionCancel lookForVision, ZeroOdometeryMode isFirstPath){
        this(path, invertPath, lookForVision);
        mIsFirstPath = (isFirstPath == ZeroOdometeryMode.FIRST_PATH);
    }

    public MoveCommand(Trajectory path, Side invertPath, VisionCancel lookForVision, ZeroOdometeryMode isFirstPath, FieldSide fieldSide){
        this(path, invertPath, lookForVision);
        mIsRightPath = fieldSide;
        mIsFirstPath = (isFirstPath == ZeroOdometeryMode.FIRST_PATH);
    }

    @Override
    protected void initialize() {
        Ramsete.getInstance().start();
        // System.out.println("INITIAL POSIIION:\t\t" + Drivetrain.getInstance().getRobotPos().getX() + "\t\t\t" + Drivetrain.getInstance().getRobotPos().getY());
        // System.out.println(((mInvertPath) ? -1 : 1) * mPath.get(0).x +"\t\t\t\t" + ((mInvertPath) ? 1 : 1) * mPath.get(0).y + "\t\t\t\t" + (mInvertPath ? -1 : 1) *  Pathfinder.r2d(mPath.get(0).heading));
        // Drivetrain.getInstance().zeroSensor();
        if(mIsFirstPath){
            Drivetrain.getInstance().setOdometery(new RobotPos(mPath.get(0).x, ((mIsRightPath == FieldSide.LEFT) ? -1 : 1) * mPath.get(0).y, ((mInvertPath) ? 180 : 0) + (((mIsRightPath == FieldSide.LEFT) ? -1 : 1) * Pathfinder.r2d(mPath.get(0).heading))));
        }else{
            // Drivetrain.getInstance().setOdometery(new RobotPos(Drivetrain.getInstance().getRobotPos(), mInvertPath));
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
        return Ramsete.getStatus() == Status.STANDBY || ((mLookForVision == VisionCancel.CANCEL_ON_VISION) && Robot.limePanel.hasTarget());
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