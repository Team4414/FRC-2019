package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.commands.elevator.ZeroElevator;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.logging.CSVLogger;

public class Robot extends TimedRobot {

  public static enum Side{
    BALL,
    PANEL,
  };

  //---------- Commands/Controllers ---------
  private CheesyDriveHelper drive;
  private static ZeroElevator mZeroElevatorCommand;
  //-----------------------------------------

  //--------- System Wide Variables ----------
  public static Side activeSide;
  public static boolean respectPerimeter;
  public static boolean isClimbing;
  
  public static PowerDistributionPanel pdp;
  //------------------------------------------

  //---------- Vision Items ------------
  public static Limelight limePanel = new Limelight(Side.PANEL);
  public static Limelight limeBall  = new Limelight(Side.BALL );
  //------------------------------------

  private boolean mInitCalled;
  private double mTurnSignal;

  @Override
  public void robotInit(){

    pdp = new PowerDistributionPanel();

    //Instantiate all subsystems
    Elevator.getInstance();
    Hand.getInstance();
    Finger.getInstance();
    DustPan.getInstance();
    Intake.getInstance();
    Drivetrain.getInstance().zeroSensor();

    OI.getInstance(); //OI goes last, needs subsystems to be instantiated first.


    //Create Commands & Controllers
    drive = new CheesyDriveHelper();
    mZeroElevatorCommand = new ZeroElevator();

    //Set all system wide variables
    activeSide = (Hand.getInstance().hasBall()) ? Side.BALL : Side.PANEL;
    respectPerimeter = false;
    isClimbing = false;

    mInitCalled = false;
    mTurnSignal = 0;
  }

  @Override
  public void robotPeriodic() {
    if(Hand.getInstance().hasBall()){
      activeSide = Side.BALL;
    }else{
      activeSide = Side.PANEL;
    }
  }

  @Override
  public void autonomousInit() {
    this.teleopInit(); //Just start teleop in sandstorm
  }

  @Override
  public void teleopInit() {
    if (mInitCalled)
      return;

    mZeroElevatorCommand.start();

    limePanel.setUSBCam(true);
    limePanel.setLED(LED_STATE.OFF);
    limePanel.setCamMode(CAM_MODE.DRIVER);
    limeBall.setUSBCam(true);
    limeBall.setLED(LED_STATE.OFF);
    limeBall.setCamMode(CAM_MODE.DRIVER);

    Elevator.getInstance().checkNeedsZero();
    Elevator.getInstance().setRaw(0);
    Climber.getInstance().deployPiston(false);

    Drivetrain.getInstance().setBrakeMode(false);

    mInitCalled = true;

  }

  @Override
  public void teleopPeriodic(){

    if (OI.getInstance().getVision()){
      mTurnSignal = limePanel.getTheta() * 0.2;
    }else{
      mTurnSignal = OI.getInstance().getLeft();
    }

    Scheduler.getInstance().run();


    if (!isClimbing){
      Drivetrain.getInstance().setRawSpeed(
        drive.cheesyDrive(
          OI.getInstance().getForward(), 
          mTurnSignal,
          OI.getInstance().getQuickTurn(), 
          false
        )
      );
    }
  }

  @Override
  public void testInit() {
    //no-op
  }

  @Override
  public void testPeriodic() {
    //no-op
  }

  @Override
  public void disabledInit() {

    limePanel.setLED(LED_STATE.OFF);
    limeBall.setLED(LED_STATE.OFF);
    limePanel.setUSBCam(false); 
    limeBall.setUSBCam(false);

    Climber.getInstance().setBrakeMode(false);
    Elevator.getInstance().setPosition(0);
    Drivetrain.getInstance().setBrakeMode(false);
  }

  @Override
  public void disabledPeriodic(){
    //no-op
  }
}
