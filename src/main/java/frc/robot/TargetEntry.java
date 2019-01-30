package frc.robot;

public class TargetEntry{
    private final double mDistance;
    private final double mTheta;
    private final double mHeight;

    public TargetEntry (double distance, double theta, double height){
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