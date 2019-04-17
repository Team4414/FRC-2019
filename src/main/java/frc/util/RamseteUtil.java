package frc.util;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.commands.auton.MoveCommand.FieldSide;
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
    private static final double kZeta = 0.05;    //Damper (0.96)
    private static final double kBeta = 6.00;    //Agressiveness (5.65)

    private static final double kDistanceKill = 0.5;

    public enum Status{
        STANDBY,    //Robot is finished following a path and waiting for a new one.
        TRACKING    //Robot is currently busy tracking a path.
    }

    public Trajectory path;
    public int mSegCount;
    private static Status status = Status.STANDBY;
    private static boolean invertPath = false;
    private static boolean isLeftSide = false;

    private double mConstant, mAngleError, ramv, ramw, mInitMod;

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

        forceStateUpdate();

        //otherwise you are tracking so update your values.
        status = Status.TRACKING;

        // if( (path.get(mSegCount).heading + mInitMod + (invertPath ? Math.PI : 0)) - gTheta_Last >= Math.PI) {
        //     mInitMod -= (path.get(mSegCount).heading + mInitMod + (invertPath ? Math.PI : 0)) - gTheta_Last;
        // }
        

        //Ramsete Math:
        gX = path.get(mSegCount).x * Constants.kFeet2Meters; //(invertPath ? -1 : 1) * 
        gY = (isLeftSide? -1 : 1) * path.get(mSegCount).y * Constants.kFeet2Meters;//((invertPath) ? 1 : -1) * 
        gTheta = (isLeftSide? -1 : 1) * (( (invertPath ? 180 : 0) + mInitMod + ((path.get(mSegCount).heading))));

        // if(Math.abs(gTheta - gTheta_Last) > 5.5){
        //     mInitMod -= gTheta_Last-gTheta;
        //     gTheta = -((mInitMod + ((path.get(mSegCount).heading))));
        //     // gTheta += mInitMod;
        //     System.out.println("RUNNUNG FLIPPUY");
        // }
        mInitMod = 0;

        rX = getPose2d().getX() * Constants.kFeet2Meters;
        rY = getPose2d().getY() * Constants.kFeet2Meters;
        rTheta = (invertPath ? 180 : 0) + Pathfinder.d2r(getPose2d().getHeading());

        gW = (gTheta - gTheta_Last) / kTimestep;
        gV = (invertPath ? -1 : 1) * path.get(mSegCount).velocity * Constants.kFeet2Meters;

        mAngleError = Pathfinder.d2r(Pathfinder.boundHalfDegrees(Pathfinder.r2d(gTheta - rTheta)));

        double sinThetaErrOverThetaErr;
        if (Math.abs(mAngleError) < 0.00001)
            sinThetaErrOverThetaErr = 1; //this is the limit as sin(x)/x approaches zero
        else
            sinThetaErrOverThetaErr = Math.sin(mAngleError) / (mAngleError);

        // System.out.println("R " + "\t\t\t\t" + rTheta + "\t\t\t\tG " + gTheta + "\t\t\t\tThetaError: " + mAngleError);
        SmartDashboard.putNumber("GTheta", gTheta);
        SmartDashboard.putNumber("RTheta", rTheta);

        //Constant Equation from the paper.
        mConstant = 2.0 * kZeta *
                Math.sqrt(Math.pow(gW, 2.0) +
                kBeta * Math.pow(gV, 2.0));

        gTheta_Last = gTheta;

        //Eq. 5.12!
        ramv =  gV * Math.cos(mAngleError) +
                mConstant * (Math.cos(rTheta) * 
                (gX - rX) +
                Math.sin(rTheta) * (gY - rY));

        ramw =  gW + kBeta * gV *
                (sinThetaErrOverThetaErr) * (Math.cos(rTheta) *
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

        // mInitMod = (double) Math.round((rTheta - (path.get(0).heading + (invertPath ? Math.PI : 0))) / ((2*Math.PI))*2*Math.PI);
    }

    /**
     * Track Path Method.
     *
     * @param path The desired trajectory for the robot to follow.
     */
    public void trackPath(Trajectory path, boolean invert){
        this.path = path;
        mSegCount = 0;
        forceStateUpdate();
        invertPath = invert;
        gTheta_Last = gTheta;

        // mInitMod = (double) Math.round((rTheta - (path.get(0).heading + (invertPath ? Math.PI : 0))) / ((2*Math.PI))*2*Math.PI);
    }

    /**
     * Track Path Method.
     *
     * @param path The desired trajectory for the robot to follow.
     */
    public void trackPath(Trajectory path, boolean invert, boolean isLeftField){
        this.path = path;
        mSegCount = 0;
        forceStateUpdate();
        invertPath = invert;
        gTheta_Last = gTheta;
        this.isLeftSide = isLeftField;

        // mInitMod = (double) Math.round((rTheta - (path.get(0).heading + (invertPath ? Math.PI : 0))) / ((2*Math.PI))*2*Math.PI);
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
            return;
        }else{
            status = Status.TRACKING;
        }

        if(
        Math.sqrt((path.get(path.length() - 1).x - rX)*((path.get(path.length() - 1).x - rX)) 
        + ((path.get(path.length() - 1).y - rY)*((path.get(path.length() - 1).y - rY)))) < kDistanceKill){
            System.out.println("KILLED");
            prepareForStandby();    
            status = Status.STANDBY;
        }

        // System.out.println(Math.sqrt((path.get(path.length() - 1).x - rX)*((path.get(path.length() - 1).x - rX)) 
        //     + ((path.get(path.length() - 1).y - rY)*((path.get(path.length() - 1).y - rY)))));
    }

    /**
     * Get Velocities Method.
     *
     * @return A Velocity DriveSignal to apply to the drivetrain.
     */
    public DriveSignal getVels(){
        
        System.out.println((invertPath ? -1 : 1) + "\t\t\t" + ((invertPath ? -1 : 1) * ramv - ramw * (kWheelBase * Constants.kFeet2Meters) / 2));
        
        return new DriveSignal(
                Constants.kMeters2Feet * ((invertPath ? -1 : 1) * ramv - ramw * (kWheelBase * Constants.kFeet2Meters) / 2),
                Constants.kMeters2Feet * ((invertPath ? -1 : 1) * ramv + ramw * (kWheelBase * Constants.kFeet2Meters) / 2)
        );

        // return new DriveSignal(-3, -3);
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
        gTheta_Last = 0;
        gTheta = 0;
    }

    public double getMaxDist(){
        if (status == Status.TRACKING){
            return path.get(path.length() - 1).position;
        }else{
            return 0;
        }
    }

    public double getCurrentDist(){
        if (status == Status.TRACKING){
            return path.get(mSegCount).position;
        }else{
            return 0;
        }
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