package frc.robot;

import java.util.ArrayList;


import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.TargetEntry;
import frc.robot.vision.VisionHelper;
import frc.robot.vision.VisionTune;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM;
import frc.util.Limelight.LED_STATE;
import frc.util.logging.CSVLogger;

public class Robot extends TimedRobot {

  private CheesyDriveHelper drive;

  //---------- Vision Items ------------
  public static Limelight limePanel = new Limelight(CAM.PANEL_SIDE);
  public static Limelight limeBall  = new Limelight(CAM.BALL_SIDE);

  public static ArrayList<TargetEntry> visionTable;
  private final String kVisionTableLocation = "/U/visionlookup.csv";
  //------------------------------------


  @Override
  public void robotInit() {
    drive = new CheesyDriveHelper();
    visionTable = new ArrayList<>();

    for (String data: CSVLogger.fromCSV(kVisionTableLocation)){
      visionTable.add(new TargetEntry(data));
    }
  }

  @Override
  public void robotPeriodic() {
  }

  @Override
  public void autonomousInit() {
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    limePanel.setUSBCam(true);
  }

  private double turnSignal = 0;

  @Override
  public void teleopPeriodic() {

    turnSignal = VisionHelper.turnCorrection() + OI.getInstance().getLeft();

    System.out.println(turnSignal);
    
    // Drivetrain.getInstance().setRawSpeed(
    //   drive.cheesyDrive(
    //     OI.getInstance().getForward(), 
    //     turnSignal,
    //     OI.getInstance().getQuickTurn(), 
    //     false
    //   )
    // );
  }

  boolean mCollected = false;

  @Override
  public void testInit() {
    mCollected = false;
    Drivetrain.getInstance().zeroSensor();
  }

  @Override
  public void testPeriodic() {
    if (!mCollected){
      mCollected = VisionTune.getInstance().areaAutoTune(0.5, 6);
    }
  }

  @Override
  public void disabledInit() {
    limePanel.setLED(LED_STATE.OFF);
    limeBall.setLED(LED_STATE.OFF);
    limePanel.setUSBCam(false); 
  }
}
