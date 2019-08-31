package frc.robot;

import java.util.LinkedHashMap;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.commands.DriveManual;
import frc.robot.commands.auton.CargoShip;
import frc.robot.commands.auton.MoveCommand;
import frc.robot.commands.auton.PIDTurn;
import frc.robot.commands.auton.PathLoader;
import frc.robot.commands.auton.Ramsete;
import frc.robot.commands.auton.TestAutons;
import frc.robot.commands.auton.MoveCommand.FieldSide;
import frc.robot.commands.elevator.ZeroElevator;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Intake.IntakeWheelState;
import frc.robot.subsystems.PPintake.ArmState;
import frc.robot.subsystems.PPintake.PPState;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.SignalLEDS;
import frc.robot.subsystems.SignalLEDS.LightPattern;
import frc.robot.vision.AutoDriveIn;
import frc.robot.vision.AutoScoreCommand;
import frc.robot.vision.VisionHelper;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.Limelight.TARGET_MODE;
import jaci.pathfinder.Trajectory;


// !!!!!!!!!!! This is modified for 4414's offseason zippy !!!!!!!!!!
public class Robot extends TimedRobot {

  public static enum Side {
    BALL, PANEL,
  };

  // ---------- Commands/Controllers ---------
  private CheesyDriveHelper drive;
  private static ZeroElevator mZeroElevatorCommand;

  private static Command mAutoScoreCommand;
  private static Command mAutoDriveInCommand;
  private static DriveManual mDriveCommand;
  // -----------------------------------------

  // --------- System Wide Variables ----------
  public static Side activeSide;
  public static boolean respectPerimeter;
  public static boolean isClimbing;
  public static boolean isStationGrab;
  public static boolean autoPlace;
  public static boolean wantsToCargoOnScore;
  
  public static PowerDistributionPanel pdp;
  //------------------------------------------

  //---------- Vision Items ------------
  public static Limelight limePanel = new Limelight(Side.PANEL);
  public static Limelight limeBall  = new Limelight(Side.BALL );

  public static Limelight.TARGET_MODE targetMode = TARGET_MODE.CENTER;
  public static boolean overrideVisionToBall = false;
  //------------------------------------

  private boolean mInitCalled;
  private boolean mAutoCancelled;

  //Map of all autonomous paths
  public static LinkedHashMap<String, Trajectory> autonPaths;
  Command autonCommand;

  private SendableChooser<FieldSide> mFieldSideChooser;
  
  private SendableChooser<FieldSide> autoChooser = new SendableChooser<FieldSide>();

  @Override
  public void robotInit(){

    UsbCamera driverCam = CameraServer.getInstance().startAutomaticCapture(0);
    driverCam.setResolution(160,120);
    driverCam.setFPS(10);

    pdp = new PowerDistributionPanel();

    //Instantiate all subsystems
    Elevator.getInstance();
    Hand.getInstance();
    PPintake.getInstance();
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

    Elevator.getInstance().zero();

    OI.getInstance(); //OI goes last, needs subsystems to be instantiated first.


    //Create Commands & Controllers
    drive = new CheesyDriveHelper();
    mZeroElevatorCommand = new ZeroElevator();
    mDriveCommand = new DriveManual();
    mAutoScoreCommand = new AutoScoreCommand();
    mAutoDriveInCommand = new AutoDriveIn();

    //Set all system wide variables
    activeSide = (Hand.getInstance().hasBall()) ? Side.BALL : Side.PANEL;

    respectPerimeter = false;
    isClimbing = false;
    autoPlace = false;
    isStationGrab = false;
    wantsToCargoOnScore = false;

    mInitCalled = false;
  }

  @Override
  public void robotPeriodic() {

    //Override active side to ball always
    activeSide = Side.BALL;
  }

  @Override
  public void autonomousInit() {

    teleopInit(); //Just start teleop in sandstorm
    

    Scheduler.getInstance().run();
  }

  @Override
  public void autonomousPeriodic() {
    teleopPeriodic();

    Scheduler.getInstance().run();
  }

  @Override
  public void teleopInit() {

    limePanel.setUSBCam(true);
    limePanel.setLED(LED_STATE.OFF);
    limePanel.setCamMode(CAM_MODE.VISION);
    limeBall.setUSBCam(true);
    limeBall.setLED(LED_STATE.OFF);
    limeBall.setCamMode(CAM_MODE.VISION);

    if (mInitCalled)
      return;

      
    PPintake.getInstance().setPP(PPState.HOLDING);
    Intake.getInstance().intake(IntakeWheelState.CLEAR);
    
    // autonCommand.cancel();
    mZeroElevatorCommand.start();

    Drivetrain.getInstance().setBrakeMode(false);
    Drivetrain.getInstance().zeroSensor();

    Climber.getInstance().deployPiston(false);

    

    Elevator.getInstance().checkNeedsZero();

    mInitCalled = true;
  }

  
  boolean alreadyScored = false;

  double adder = 0;
  
  @Override
  public void teleopPeriodic(){

    System.out.println(pdp.getCurrent(5));


    if (Timer.getMatchTime() < 30){
      SignalLEDS.getInstance().set(LightPattern.RED_STROBE);
    }else{
      SignalLEDS.getInstance().set(LightPattern.TEAL_SOLID);
    }

    targetMode = OI.getInstance().getVisionSwitcher();

    if (!isClimbing){
      if (activeSide == Side.PANEL){
        //side is panel
        if (OI.getInstance().getVision()){
          //vision button is pressed

          VisionHelper.getActiveCam().setLED(LED_STATE.ON);
          VisionHelper.getActiveCam().setCamMode(CAM_MODE.VISION);

          if (VisionHelper.hasTarget()){
            //drive command is running and vision sees target: auto score
            if (mDriveCommand.isRunning()){
              mDriveCommand.cancel();
            }

            if (OI.getInstance().getStationButton()){
              if (mAutoScoreCommand.isRunning()){
                mAutoScoreCommand.cancel();
              }
              if (!mAutoDriveInCommand.isRunning()){
                mAutoDriveInCommand.start();
              }
            }else{
              if (mAutoDriveInCommand.isRunning()){
                mAutoDriveInCommand.cancel();
              }
              if (!mAutoScoreCommand.isRunning() && !alreadyScored){
                mAutoScoreCommand.start();
                alreadyScored = true;
              }
            }
          } else {
            //vision enabled but cannot see target
            if (!mDriveCommand.isRunning()){
              mDriveCommand.start();
            }
          }

        }else{
          //vision button is released: cancel autoscore and start manual drive

          VisionHelper.getActiveCam().setLED(LED_STATE.OFF);
          VisionHelper.getActiveCam().setCamMode(CAM_MODE.DRIVER);

          if (mAutoDriveInCommand.isRunning()){
            mAutoDriveInCommand.cancel();
          }

          if (mAutoScoreCommand.isRunning()){
            mAutoScoreCommand.cancel();
          }

          if (!mDriveCommand.isRunning()){
            mDriveCommand.start();
          }

          alreadyScored = false;
        }
      } else {
        //side is ball: cancel autoscore and manual drive

        if (mAutoScoreCommand.isRunning()){
          mAutoScoreCommand.cancel();
        }

        if (mAutoDriveInCommand.isRunning()){
          mAutoDriveInCommand.cancel();
        }

        if (!mDriveCommand.isRunning()){
          mDriveCommand.start();
        }

        if (OI.getInstance().getVision()){
          VisionHelper.getActiveCam().setLED(LED_STATE.ON);
          VisionHelper.getActiveCam().setCamMode(CAM_MODE.VISION);
          mDriveCommand.enableVisionCorrection(true);
        }else{
          VisionHelper.getActiveCam().setLED(LED_STATE.OFF);
          VisionHelper.getActiveCam().setCamMode(CAM_MODE.VISION);
          mDriveCommand.enableVisionCorrection(false);
        }
        
      }
    } else {
      if (mDriveCommand.isRunning()){
        mDriveCommand.cancel();
      }

      if (mAutoScoreCommand.isRunning()){
        mAutoScoreCommand.cancel();
      }

      if (mAutoDriveInCommand.isRunning()){
        mAutoDriveInCommand.cancel();
      }
    }

    Scheduler.getInstance().run();
  }
  
  @Override
  public void testInit() {
    // mAutoDriveInCommand.start();
    //no-op
    Drivetrain.getInstance().startOdometery(0.005);
  }

  @Override
  public void testPeriodic() {
    // if (!mAutoDriveInCommand.isRunning()){
    //   mAutoDriveInCommand.start();
    // }

      // Drivetrain.getInstance().setRawSpeed(0.1, 0.1);
      // Drivetrain.getInstance().setVelocity(5, 5);
      // System.out.println(Drivetrain.getInstance().getLeftSensorVelocity() + "\t\t\t\t" + Drivetrain.getInstance().getRightSensorVelocity());
      // System.out.println(Drivetrain.getInstance().getLeftMaster().getSelectedSensorVelocity() + "\t\t\t" + Drivetrain.getInstance().getRightMaster().getSelectedSensorVelocity());
      // System.out.println("X: " + Drivetrain.getInstance().getRobotPos().getX() + "\t\t Y:" + Drivetrain.getInstance().getRobotPos().getY() +"\t\t LEFTPOS:" +
      // Drivetrain.getInstance().getLeftSensorPosition() + "\t\t Right: " + Drivetrain.getInstance().getRightSensorPosition());



    // Elevator.getInstance().setRaw(1);
    // Drivetrain.getInstance().setRawSpeed(VisionHelper.getDriveSignal());
    // Drivetrain.getInstance().setRawSpeed(VisionHelper.throttleCorrection(), VisionHelper.throttleCorrection());
    // System.out.println(VisionHelper.getActiveCam().tS());
    //no-op
  }

  @Override
  public void disabledInit() {

    mAutoCancelled = false;

    limePanel.setLED(LED_STATE.OFF);
    limeBall.setLED(LED_STATE.OFF);
    limePanel.setUSBCam(true); 
    limeBall.setUSBCam(true);
    limePanel.setLED(LED_STATE.ON);
    limeBall.setLED(LED_STATE.ON);
    limePanel.setCamMode(CAM_MODE.VISION);
    limeBall.setCamMode(CAM_MODE.VISION);

    Climber.getInstance().setBrakeMode(false);
    Elevator.getInstance().setPosition(0);
    Drivetrain.getInstance().setBrakeMode(false);
    Drivetrain.getInstance().stopOdometery();

    Ramsete.getInstance().stop();
  }

  @Override
  public void disabledPeriodic(){
    //no-op
  }
}
