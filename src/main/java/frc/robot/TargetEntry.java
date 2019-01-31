package frc.robot;

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
}