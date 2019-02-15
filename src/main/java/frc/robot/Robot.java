package frc.robot;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Intake;
import frc.robot.vision.TargetEntry;
import frc.robot.vision.VisionTune;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.logging.CSVLogger;

public class Robot extends TimedRobot {

  private CheesyDriveHelper drive;

  //---------- Vision Items ------------
  public static Limelight limePanel = new Limelight(CAM.PANEL_SIDE);
  public static Limelight limeBall  = new Limelight(CAM.BALL_SIDE);

  public static ArrayList<TargetEntry> visionTable;
  private final String kVisionTableLocation = "visionlookup";
  //------------------------------------

  @Override
  public void robotInit() {


    drive = new CheesyDriveHelper();
    visionTable = new ArrayList<>();

    for (String data: CSVLogger.fromCSV(kVisionTableLocation)){
      visionTable.add(new TargetEntry(data));
      System.out.println(data);
    }
    
    Drivetrain.getInstance().zeroSensor();
    
    SmartDashboard.putNumber("ERROR", Elevator.getInstance().getError());
    
    Elevator.getInstance();
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
    limePanel.setLED(LED_STATE.ON);
    limePanel.setCamMode(CAM_MODE.VISION);
    Elevator.getInstance().zero();
    Elevator.getInstance().setPosition(0);
    // Finger.getInstance();
  }

  private double turnSignal = 0;

  DigitalInput dIO = new DigitalInput(1);
  AnalogInput aIO = new AnalogInput(2);

  @Override
  public void teleopPeriodic() {

    System.out.println(dIO + "\t\t\t\t" + aIO);

    // turnSignal = VisionHelper.turnCorrection() + OI.getInstance().getLeft();

    // System.out.println(turnSignal);
    // VisionHelper.turnCorrection();

    // limePanel.tHeight();
    // System.out.printf("%.3f",limePanel.getSkew());
    // System.out.println(limePanel.getSkew());
    // SmartDashboard.putNumber("SKEW", limePanel.getSkew());
    // SmartDashboard.putNumber("THEIGHT", limePanel.tHeight());
    // SmartDashboard.putNumber("DIST", VisionHelper.turnCorrection());
    // SmartDashboard.putNumber("WHRation", limePanel.getWHratio());
    // limePanel.tHeight();
    
    Drivetrain.getInstance().setRawSpeed(
      drive.cheesyDrive(
        OI.getInstance().getForward(), 
        OI.getInstance().getLeft(),
        false, 
        false
      )
    );

    // if (Timer.getFPGATimestamp() % 20 > 10){
    //   Finger.getInstance().setFinger(true);
    // }else{
    //   Finger.getInstance().setFinger(false);
    // }
    // limePanel.setLED(LED_STATE.ON);

    // limitTest.set(ControlMode.PercentOutput, -0.5);
    // System.out.println(limitSwitch.get());

    // Drivetrain.getInstance().setRawSpeed(0.5, 0.5);
    // System.out.println(Elevator.getInstance().getPosition());
    // Elevator.getInstance().setPosition(15000);
    // System.out.println(Elevator.getInstance().getPosition());
    // Elevator.getInstance().setPosition(25000);
    // SmartDashboard.putNumber("ERROR", Elevator.getInstance().getError());

    if(OI.getInstance().getXbox().getBumper(Hand.kLeft)){
      DustPan.getInstance().intake(true);
      DustPan.getInstance().deploy(true);
    }else{
      DustPan.getInstance().intake(false);
      DustPan.getInstance().deploy(false);
    }

    if(OI.getInstance().getXbox().getBumper(Hand.kRight)){
      Intake.getInstance().deploy(true);
      Intake.getInstance().intake(true);
    }else{
      Intake.getInstance().deploy(false);
      Intake.getInstance().intake(false);
    }

    if (OI.getInstance().getXbox().getAButton()){
      Finger.getInstance().setArm(true);
    }else{
      Finger.getInstance().setArm(false);
    }

    if (OI.getInstance().getXbox().getBButton()){
      Finger.getInstance().setFinger(true);
    }else{
      Finger.getInstance().setFinger(false);
    }

  }

  boolean mCollected = false;

  @Override
  public void testInit() {
    mCollected = false;
    Drivetrain.getInstance().zeroSensor();
    Elevator.getInstance().zero();
  }

  @Override
  public void testPeriodic() {
    if (!mCollected){
      mCollected = VisionTune.getInstance().areaAutoTune(0.1, 4);
    }
  }

  @Override
  public void disabledInit() {
    limePanel.setLED(LED_STATE.OFF);
    limeBall.setLED(LED_STATE.OFF);
    limePanel.setUSBCam(false); 
  }

  @Override
  public void disabledPeriodic(){
  }
}
