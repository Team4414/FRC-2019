package frc.util;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

public class RamseteUtil{
    public static final double kFeet2Meters = 3.28084;
    public static final double kMeters2Feet = 1 / kFeet2Meters;

    private static double gX, gY, gTheta, gTheta_Last,
                   rX, rY, rTheta,
                   gW, gV;


    public static DriveSignal getSignal(Trajectory path, int pathIndex, double timestep, double zeta, double beta, double wheelbase){
        gX = path.get(pathIndex).x * kFeet2Meters;
        gY = path.get(pathIndex).y * kFeet2Meters;
        gTheta_Last = gTheta;
        gTheta = path.get(pathIndex).heading;

        rX = path.get(pathIndex).x * kFeet2Meters;
        rY = path.get(pathIndex).y * kFeet2Meters;
        rTheta = Pathfinder.d2r(path.get(pathIndex).heading);

        gW = (gTheta - gTheta_Last) / timestep;
        gV = path.get(pathIndex).velocity * kFeet2Meters;

        double mAngleError = Pathfinder.d2r(Pathfinder.boundHalfDegrees(Pathfinder.r2d(gTheta - rTheta)));

        double sinThetaErrOverThetaErr;
        if (Math.abs(mAngleError) < 0.00001)
            sinThetaErrOverThetaErr = 1; //this is the limit as sin(x)/x approaches zero
        else
            sinThetaErrOverThetaErr = Math.sin(mAngleError) / (mAngleError);

        double mConstant = 2.0 * zeta *
                Math.sqrt(Math.pow(gW, 2.0) +
                beta * Math.pow(gV, 2.0));

        gTheta_Last = gTheta;

        //Eq. 5.12!
        double ramv =  gV * Math.cos(mAngleError) +
                mConstant * (Math.cos(rTheta) * 
                (gX - rX) +
                Math.sin(rTheta) * (gY - rY));

        double ramw =  gW + beta * gV *
                (sinThetaErrOverThetaErr) * (Math.cos(rTheta) *
                (gY - rY) - Math.sin(rTheta) *
                (gX - rX)) + mConstant * (mAngleError);

        return new DriveSignal(
            kMeters2Feet * (ramv - ramw * (wheelbase * kFeet2Meters) / 2),
            kMeters2Feet * (ramv + ramw * (wheelbase * kFeet2Meters) / 2)
        );
    }

}