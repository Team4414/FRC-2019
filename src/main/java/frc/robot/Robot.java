package frc.robot;

import edu.wpi.first.cameraserver.CameraServer;
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
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.vision.VisionHelper;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.Limelight.TARGET_MODE;

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

  public static Limelight.TARGET_MODE targetMode = TARGET_MODE.CENTER;
  public static boolean overrideVisionToBall = false;
  //------------------------------------

  private boolean mInitCalled;
  private double mTurnSignal;

  private double[] operatorSignal;

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
    Climber.getInstance();

    limePanel.setUSBCam(true);
    limePanel.setLED(LED_STATE.ON);
    limePanel.setCamMode(CAM_MODE.DRIVER);
    limeBall.setUSBCam(true);
    limeBall.setLED(LED_STATE.ON);
    limeBall.setCamMode(CAM_MODE.DRIVER);

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
    if(Hand.getInstance().hasBall() || overrideVisionToBall){
      activeSide = Side.BALL;
    }else{
      activeSide = Side.PANEL;
    }

    if (Elevator.getInstance().getSwitch()){
      Elevator.getInstance().zero();
    }
  }

  @Override
  public void autonomousInit() {
    teleopInit(); //Just start teleop in sandstorm
    
    Elevator.getInstance().setPosition(Setpoint.STOW);
  }

  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();
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

    Drivetrain.getInstance().setBrakeMode(false);

    
    Climber.getInstance().deployPiston(false);

    mInitCalled = true;
  }

  @Override
  public void teleopPeriodic(){

    targetMode = OI.getInstance().getVisionSwitcher();

    operatorSignal = drive.cheesyDrive(
      OI.getInstance().getForward(), 
      OI.getInstance().getLeft(),
      OI.getInstance().getQuickTurn(), 
      false
    );

    if (OI.getInstance().getVision()){

      if (activeSide == Side.BALL){
        VisionHelper.setActiveCam(limeBall);
      }else{
        VisionHelper.setActiveCam(limePanel);
      }

      mTurnSignal = VisionHelper.turnCorrection();
      operatorSignal[0] -= mTurnSignal;
      operatorSignal[1] += mTurnSignal;

    }else{
      VisionHelper.resetLock();
      mTurnSignal = 0;
    }



    if (!isClimbing){
      Drivetrain.getInstance().setRawSpeed(operatorSignal);
    }
    Scheduler.getInstance().run();
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
    limePanel.setUSBCam(true); 
    limeBall.setUSBCam(true);
    limePanel.setLED(LED_STATE.ON);
    limeBall.setLED(LED_STATE.ON);
    limePanel.setCamMode(CAM_MODE.VISION);
    limePanel.setCamMode(CAM_MODE.VISION);

    Climber.getInstance().setBrakeMode(false);
    Elevator.getInstance().setPosition(0);
    Drivetrain.getInstance().setBrakeMode(false);
  }

  @Override
  public void disabledPeriodic(){
    //no-op
  }
}
