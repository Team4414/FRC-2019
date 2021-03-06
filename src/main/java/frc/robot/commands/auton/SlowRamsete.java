package frc.robot.commands.auton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import frc.robot.Constants;
import frc.robot.subsystems.Drivetrain;
import frc.util.DriveSignal;
import frc.util.kinematics.pos.RobotPos;
import jaci.pathfinder.Trajectory;
import jaci.pathfinder.Trajectory.Segment;

@SuppressWarnings("FieldCanBeLocal")
/**
 * Ramsete implementation by Brian for Team 321 based on Aaron's implementation
 * with help from Prateek and all on the FIRST programming discord server
 * yeeted by Kunal for Team 3647 (with permission of course)
 */
public class SlowRamsete 
{

    public enum MotionProfileDirection{
        FORWARD,
        BACKWARD
    }

    // Should be greater than zero and this increases correction
    private double kBeta = 6.00; //1.5;

    // Should be between zero and one and this increases dampening
    private double kZeta = 0.05; //0.7;

    // Holds what segment we are on
    private int segmentIndex;
    private Segment current;

    // The trajectory to follow
    private Trajectory trajectory;

    // The robot's x and y position and angle
    private RobotPos robotPos;

    // Variable used to calculate linear and angular velocity
    private double lastTheta, nextTheta;
    private double k, thetaError, sinThetaErrorOverThetaError;
    public double desiredAngularVelocity, linearVelocity, angularVelocity;
    private double odometryError;

    // Constants
    private static final double EPSILON = 0.00000001;
    private static final double TWO_PI = 2 * Math.PI;

    // Variable for holding velocity for robot to drive on
    private Velocity velocity;
    private DriveSignal driveSignal;
    private double left, right;


    public SlowRamsete(Trajectory trajectory, MotionProfileDirection direction) 
    {
        //ternary operator, if direction is forward return trajectory, else return the reversed path
        this.trajectory = direction == MotionProfileDirection.FORWARD ? trajectory : reversePath(trajectory);
        // this.trajectory = TrajectoryUtil.correctPath(trajectory);

        segmentIndex = 0;
        // odometry = Odometry.getInstance();

        driveSignal = new DriveSignal(0,0);
    }

    public SlowRamsete(Trajectory trajectory, double b, double zeta, MotionProfileDirection direction) 
    {
        this(trajectory, direction);

        this.kBeta = b;
        this.kZeta = zeta;
    }

    public Velocity getVelocity() 
    {
        if (isFinished()) 
        {
            return new Velocity(0, 0);
        }

        current = trajectory.get(segmentIndex);

        desiredAngularVelocity = calculateDesiredAngular();

        linearVelocity = calculateLinearVelocity(current.x, current.y, current.heading, current.velocity, desiredAngularVelocity);

        angularVelocity = calculateAngularVelocity(current.x, current.y, current.heading, current.velocity, desiredAngularVelocity);

        return new Velocity(linearVelocity, angularVelocity);
    }

    public DriveSignal getNextDriveSignal() 
    {
        velocity = getVelocity();

        left = (-(velocity.getAngular() * Constants.kWheelBase) + (2 * velocity.getLinear())) / 2;
        right = ((velocity.getAngular() * Constants.kWheelBase) + (2 * velocity.getLinear())) / 2;

        driveSignal.setLeft(left);
        driveSignal.setRight(right);

        segmentIndex++;

        return driveSignal;
    }

    private double calculateDesiredAngular() 
    {
        if (segmentIndex < trajectory.length() - 1) 
        {
            lastTheta = trajectory.get(segmentIndex).heading;
            nextTheta = trajectory.get(segmentIndex + 1).heading;
            return boundHalfRadians(nextTheta - lastTheta) / current.dt;
        } 
        else 
        {
            return 0;
        }
    }

    private double calculateLinearVelocity(double desiredX, double desiredY, double desiredTheta,
            double desiredLinearVelocity, double desiredAngularVelocity) 
    {
        robotPos = Drivetrain.getInstance().getRobotPos();
        // System.out.println("DesiredLinearVelocity: " + desiredLinearVelocity);
        // System.out.println("Desired X: " + desiredX);
        // System.out.println("Actual X : " + odometry.getX());
        // System.out.println("thetaError: " + thetaError);
        k = calculateK(desiredLinearVelocity, desiredAngularVelocity);

        thetaError = boundHalfRadians(desiredTheta - robotPos.getHeading());

        odometryError = (Math.cos(robotPos.getHeading()) * (desiredX - robotPos.getHeading()))
                + (Math.sin(robotPos.getHeading()) * (desiredY - robotPos.getHeading()));

        // System.out.println("odometryError: " + (odometryError));

        return (desiredLinearVelocity * Math.cos(thetaError)) + (k * odometryError);
    }

    private double calculateAngularVelocity(double desiredX, double desiredY, double desiredTheta,
            double desiredLinearVelocity, double desiredAngularVelocity) 
    {
        k = calculateK(desiredLinearVelocity, desiredAngularVelocity);

        thetaError = boundHalfRadians(desiredTheta - robotPos.getHeading());

        if (Math.abs(thetaError) < EPSILON) 
        {
            // This is for the limit as sin(x)/x approaches zero
            sinThetaErrorOverThetaError = 1;
        } 
        else 
        {
            sinThetaErrorOverThetaError = Math.sin(thetaError) / thetaError;
        }

        odometryError = (Math.cos(robotPos.getHeading()) * (desiredY - robotPos.getY()))
                - (Math.sin(robotPos.getHeading()) * (desiredX - robotPos.getX()));

        return desiredAngularVelocity + (kBeta * desiredLinearVelocity * sinThetaErrorOverThetaError * odometryError)
                + (k * thetaError);
    }

    private double calculateK(double desiredLinearVelocity, double desiredAngularVelocity) 
    {
        return 2 * kZeta * Math.sqrt(Math.pow(desiredAngularVelocity, 2) + (kBeta * Math.pow(desiredLinearVelocity, 2)));
    }

    private double boundHalfRadians(double radians) 
    {
        while (radians >= Math.PI)
            radians -= TWO_PI;
        while (radians < -Math.PI)
            radians += TWO_PI;
        return radians;
    }

    public Segment currentSegment() 
    {
        return current;
    }

    public boolean isFinished() 
    {
        return segmentIndex >= trajectory.length();
    }

    public void printOdometry()
    {
        System.out.println("Segment index: " + segmentIndex);
        // // System.out.println("Trajectory.length: " + trajectory.length());
        // if(odometry.getX() < 3)
        //     System.out.println(odometry.getX());
        // else
        //     System.out.println("PASSED 3");
    }

    public int getSegmentIndex()
    {
        return this.segmentIndex;
    }

    public boolean pathFractionSegment(double fraction)
    {
        return (this.segmentIndex > trajectory.length()*fraction) && (this.segmentIndex < trajectory.length());
    }

    public void printDeltaDist()
    {
        // System.out.println("Delta position in meters: " + odometry.getDeltaPosition());
    }

    public void printCurrentEncoders()
    {
        // System.out.println("Odometry encoder: " + odometry.getCurrentEncoderPosition());
        // System.out.println("Actual encoder left: " + Drivetrain.leftSRX.getSelectedSensorPosition(0));
        // System.out.println("Actual encoder right: " + Drivetrain.rightSRX.getSelectedSensorPosition(0));
    }

    public static Trajectory reversePath(Trajectory originalTrajectory)
    {
        ArrayList<Segment> segments = new ArrayList<>(Arrays.asList(originalTrajectory.segments));
        Collections.reverse(segments);

        double distance = segments.get(0).position;

        return new Trajectory(segments.stream()
                .map(segment -> new Segment(segment.dt, segment.x, segment.y, distance - segment.position, -segment.velocity, -segment.acceleration, -segment.jerk, segment.heading))
                .toArray(Segment[]::new));
    }
}