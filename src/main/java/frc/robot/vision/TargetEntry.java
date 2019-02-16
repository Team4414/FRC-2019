package frc.robot.vision;

import java.util.ArrayList;
import java.util.Scanner;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Robot.Side;

public class TargetEntry{
    private final double mDistance;
    private final double mTheta;
    private final double mHeight;

    public static final TargetEntry BLANK_DATA = new TargetEntry(0, 0, 0);

    public TargetEntry (double distance, double theta, double height){
        mDistance = distance;
        mTheta = theta;
        mHeight = height;
    }

    public TargetEntry (String csvData){
        Scanner rowScanner = new Scanner(csvData);
        rowScanner.useDelimiter(",");

        double distance, theta, height;

        try{
            distance = rowScanner.nextDouble();
            theta    = rowScanner.nextDouble();
            height   = rowScanner.nextDouble();
            
        }catch(Exception e){
            System.out.println("!!!!!!!!!! INCORRECT CSV DATA: "+ csvData +" FOR TARGET ENTRY !!!!!!!!!!");
            distance = 0;
            theta    = 0;
            height   = 0;
        }
        rowScanner.close();

        mDistance = distance;
        mTheta = theta;
        mHeight = height;
    }

    public double dist(){
        return mDistance;
    }

    public double theta(){
        return mTheta;
    }

    public double height(){
        return mHeight;
    }

    @Override
    public String toString(){
        return (mDistance + "," + mTheta + "," + mHeight+ ",\n");
    }


    public static TargetEntry interpolate(ArrayList<TargetEntry> data, double tHeight, Side camUsed){
        TargetEntry lowBound = data.get(0);
        TargetEntry highBound = data.get(0);

        double distLow = Double.MAX_VALUE;
        double distHigh = Double.MAX_VALUE;
        double err;

        for (TargetEntry curr: data){

            err = tHeight - curr.height();

            if ((err > 0) & (err < distLow)){
                //if the error is positive (sample is less than target)
                lowBound = curr;
                distLow = Math.abs(err);
            }

            if ((err < 0) & (err < distHigh)){
                //if the error is negative (sample is above the target)
                highBound = curr;
                distHigh = Math.abs(err);
            }
        }


        boolean isBallSide = (camUsed == Side.BALL) ? true : false;

        return interpolate(highBound, lowBound, tHeight, isBallSide);
    }

    private static TargetEntry interpolate(TargetEntry high, TargetEntry low, double tHeight, boolean isBallSide){
        //HighDist - LowDist
        //------------------ * (HighValue + LowValue)
        //  dist - LowDist


        double pos = ((high.height() - low.height()) / (tHeight - low.height()));
        double theta = ((isBallSide) ? -1 : 1) * (high.theta() + low.theta()) * pos;

        return new TargetEntry(
            pos * (high.dist() + low.dist()),
            theta,
            pos * (high.height() + low.height())
        );
    }
}