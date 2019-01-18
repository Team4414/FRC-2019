package frc.util.auton;

import frc.robot.Constants;
import frc.util.DriveSignal;
import frc.util.kinematics.pos.RobotPos;
import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

/**
 * Ramsete Class.
 *
 * <p>A Utility that, when extended, creates a Ramsete Controller</p>
 * <p>It is important to note that this Controller is meant to be reused with multiple paths. I.E. Do not create
 * a new Controller for every Trajectory</p>
 *
 * The functionality of this Controller is based on this paper:
 * https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
 *
 * @author Avidh Bavkar [avidh@team4414.com]
 * @author JJ Sessa [jonathan@team4414.com]
 */
public abstract class RamseteUtil {

    private final double kTimestep;
    private static final double kZeta = 0.96;    //Damper (0.9)
    private static final double kBeta = 5.65;    //Agressiveness (0.1)

    public enum Status{
        STANDBY,    //Robot is finished following a path and waiting for a new one.
        TRACKING    //Robot is currently busy tracking a path.
    }

    public Trajectory path;
    public int mSegCount;
    private static Status status = Status.STANDBY;

    private double mConstant, mAngleError, ramv, ramw;

    private double gX, gY, gTheta, gTheta_Last,
                   rX, rY, rTheta,
                   gW, gV;

    private final double kWheelBase;

    public RamseteUtil(double wheelBase, double timeStep){
        mSegCount = -1; //-1 used as an invalid number
        kWheelBase = wheelBase;
        kTimestep = timeStep;
    }

    /**
     * Update Method.
     *
     * <p>Expected to be called once per specified timestep.</p>
     */
    public void update(){

        if (path == null || mSegCount >= path.length()){
            //if the path is null or you are done tracking one, reset the controller and do not continue.
            prepareForStandby();
            status = Status.STANDBY;
            return;
        }

        //otherwise you are tracking so update your values.
        status = Status.TRACKING;

        //Ramsete Math:
        gX = path.get(mSegCount).x * Constants.kFeet2Meters;
        gY = path.get(mSegCount).y * Constants.kFeet2Meters;
        gTheta = path.get(mSegCount).heading;

        rX = getPose2d().getX() * Constants.kFeet2Meters;
        rY = getPose2d().getY() * Constants.kFeet2Meters;
        rTheta = Pathfinder.d2r(getPose2d().getHeading());

        gW = (gTheta - gTheta_Last) / kTimestep;
        gV = path.get(mSegCount).velocity * Constants.kFeet2Meters;

        mAngleError = Pathfinder.d2r(Pathfinder.boundHalfDegrees(Pathfinder.r2d(gTheta - rTheta)));

        //Constant Equation from the paper.
        mConstant = 2.0 * kZeta *
                Math.sqrt(Math.pow(gW, 2.0) +
                kBeta * Math.pow(gV, 2.0));

        gTheta_Last = gTheta;

        //Eq. 5.12!
        ramv =  gV * Math.cos(mAngleError) +
                mConstant * (Math.cos(rTheta) * 
                (gX - rX)) +
                Math.sin(rTheta) * (gY - rY);

        ramw =  gW + kBeta * gV *
                (Math.sin(mAngleError) / (mAngleError)) * (Math.cos(rTheta) *
                (gY - rY) - Math.sin(rTheta) *
                (gX - rX)) + mConstant * (mAngleError);

        mSegCount ++;
    }

    /**
     * Track Path Method.
     *
     * @param path The desired trajectory for the robot to follow.
     */
    public void trackPath(Trajectory path){
        this.path = path;
        mSegCount = 0;
        forceStateUpdate();
    }

    /**
     * Force Update State Method.
     *
     * <p>Forces an update of state</p>
     */
    public void forceStateUpdate(){
        status = (path == null || mSegCount >= path.length()) ? Status.STANDBY : Status.TRACKING;
        if (path == null || mSegCount >= path.length()){
            prepareForStandby();    
            status = Status.STANDBY;
        }else{
            status = Status.TRACKING;
        }
    }

    /**
     * Get Velocities Method.
     *
     * @return A Velocity DriveSignal to apply to the drivetrain.
     */
    public DriveSignal getVels(){
        return new DriveSignal(
                Constants.kMeters2Feet * (ramv - ramw * (kWheelBase * Constants.kFeet2Meters) / 2),
                Constants.kMeters2Feet * (ramv + ramw * (kWheelBase * Constants.kFeet2Meters) / 2)
        );
    }

    /**
     * Prepare For Standby Method
     * 
     * <p> Handles zeroing values when transitioning into {@Link Status.STANDBY} </p>
     */
    private void prepareForStandby(){
        path = null;
        mConstant = 0;
        mAngleError = 0;
        ramv = 0;
        ramw = 0;
        mSegCount = -1;
    }

    /**
     * Get Status Method.
     *
     * @return The {@link Status} of the controller.
     */
    public static Status getStatus(){
        return status;
    }

    public double getGoalX(){ return gX; }

    public double getGoalY(){ return gY; }

    public double getGoalTheta(){ return gTheta; }

    /**
     * Get Robot Position Method.
     *
     * @return The current position of the robot.
     */
    public abstract RobotPos getPose2d();
}
