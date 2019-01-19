package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import frc.robot.subsystems.Drivetrain;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;

public class Robot extends TimedRobot {

  @Override
  public void robotInit() {

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
  @Override
  public void autonomousPeriodic() {
    Drivetrain.getInstance().setRawSpeed(
      -Limelight.tX() * 0.05,
      Limelight.tX() * 0.05
    );
  }

  @Override
  public void teleopPeriodic() {
  }

  @Override
  public void testPeriodic() {
    
  }

  @Override
  public void disabledInit() {
    Limelight.setLED(LED_STATE.OFF);
  }
}
