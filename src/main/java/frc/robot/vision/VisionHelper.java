package frc.robot.vision;

import frc.robot.Robot;
import frc.util.Limelight;

/**
 * 
 */
public class VisionHelper{
    private static Limelight mActiveCam = Robot.limePanel;

    public static void setActiveCam(Limelight cam){ mActiveCam = cam; }
    public static double calcDistance
}