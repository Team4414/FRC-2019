package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.Drivetrain;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;

public class Robot extends TimedRobot {

  private CheesyDriveHelper drive;

  @Override
  public void robotInit() {
    drive = new CheesyDriveHelper();
  }

  @Override
  public void robotPeriodic() {

  }

  @Override
  public void autonomousInit() {
    Limelight.setCamMode(CAM_MODE.VISION);
    Limelight.setLED(LED_STATE.ON);
  }

  double[] visionOffset;
  double throttleCommand;
  double targetArea = 0.4;

  @Override
  public void autonomousPeriodic() {

    throttleCommand = ((targetArea - Limelight.tArea()) * -3);


    if (Limelight.hasTarget()){
      Drivetrain.getInstance().setRawSpeed(
        throttleCommand + Limelight.tX() * 0.05,
        throttleCommand - Limelight.tX() * 0.05
      );
    }else{
      Drivetrain.getInstance().setRawSpeed(0, 0);
    }

  }

  @Override
  public void teleopInit() {
    Limelight.setUSBCam(true);
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

  @Override
  public void testInit() {
    Limelight.setLED(LED_STATE.ON);
    Limelight.setCamMode(CAM_MODE.VISION);
  }

  @Override
  public void disabledInit() {
    Limelight.setLED(LED_STATE.OFF);
    Limelight.setCamMode(CAM_MODE.DRIVER);
    Limelight.setUSBCam(false);
  }
}
