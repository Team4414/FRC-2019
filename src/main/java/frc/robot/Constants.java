package frc.robot;

public class Constants{
    
    public static final int kCTREpidIDX = 0;
    public static final int kCTREtimeout = 0;

    public static final double kFeet2Ticks= 3833;
    public static final double kTicks2Feet= 1/kFeet2Ticks;
    
    public static final double kWheelBase = 2.325;
    public static final double kMagEncoderPerRev = 4096;
    
    public static final double kWheelRadius = (kMagEncoderPerRev * kTicks2Feet) / (2*Math.PI);

    public static final double kNativeU2FPS = kTicks2Feet * 10;
    public static final double kFPS2NativeU = 1/kNativeU2FPS;

    public static final double kMeters2Feet = 3.28084;
    public static final double kFeet2Meters = 1/kMeters2Feet;
}