package frc.robot;

import java.util.ArrayList;


import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.Drivetrain;
import frc.robot.vision.TargetEntry;
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

  private ArrayList<TargetEntry> mVisionLookupTable;
  private final String kVisionTableLocation = "visionTable";
  //------------------------------------


  @Override
  public void robotInit() {
    drive = new CheesyDriveHelper();
    mVisionLookupTable = new ArrayList<>();

    for (String data: CSVLogger.fromCSV(kVisionTableLocation)){
      mVisionLookupTable.add(new TargetEntry(data));
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

  @Override
  public void teleopPeriodic() {
    Drivetrain.getInstance().setRawSpeed(
      drive.cheesyDrive(
        OI.getInstance().getForward(), 
        OI.getInstance().getLeft(),
        OI.getInstance().getQuickTurn(), 
        false
      )
    );
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
