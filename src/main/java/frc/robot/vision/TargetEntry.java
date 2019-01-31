package frc.robot.vision;

import java.util.ArrayList;
import java.util.Scanner;

public class TargetEntry{
    private final double mDistance;
    private final double mTheta;
    private final double mHeight;

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
            
        }catch(NullPointerException e){
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


    public static TargetEntry interpolate(ArrayList<TargetEntry> data, double dist){
        TargetEntry lowBound = data.get(0);
        TargetEntry highBound = data.get(0);

        double distLow = Double.MAX_VALUE;
        double distHigh = Double.MAX_VALUE;
        double err;

        for (TargetEntry curr: data){

            err = dist - curr.dist();

            if ((err > 0) & (err < distLow)){
                //if the error is positive (sample is less than target)
                lowBound = curr;
                distLow = err;
            }

            if ((err < 0) & (err < distHigh)){
                //if the error us negative (sample is above the target)
                highBound = curr;
                distHigh = err;
            }
        }


        return interpolate(highBound, lowBound, dist);
    }

    private static TargetEntry interpolate(TargetEntry high, TargetEntry low, double dist){
        //HighDist - LowDist
        //------------------ * (HighValue + LowValue)
        //  dist - LowDist

        double pos = ((high.dist() - low.dist()) / (dist - low.dist()));

        return new TargetEntry(
            dist,
            pos * (high.theta() + low.theta()),
            pos * (high.height() + low.height())
        );
    }
}