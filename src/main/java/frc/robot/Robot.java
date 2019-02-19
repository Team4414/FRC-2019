package frc.robot;

import java.util.ArrayList;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import frc.robot.commands.IntakePanelSequence;
import frc.robot.commands.ZeroElevator;
import frc.robot.commands.actions.SafeElevatorMove;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.DustPan.DustpanBoomState;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Finger.FingerArmState;
import frc.robot.subsystems.Finger.FingerClapperState;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake.IntakeBoomState;
import frc.robot.subsystems.Intake.IntakeWheelState;
import frc.robot.subsystems.Superstructure.State;
import frc.robot.vision.TargetEntry;
import frc.robot.vision.VisionTune;
import frc.util.CheesyDriveHelper;
import frc.util.Limelight;
import frc.util.Limelight.CAM_MODE;
import frc.util.Limelight.LED_STATE;
import frc.util.logging.CSVLogger;
import frc.util.talon.CTREFactory;

public class Robot extends TimedRobot {

  public static enum Side{
    BALL,
    PANEL,
  };

  private CheesyDriveHelper drive;
  public static Side activeSide;
  public static boolean respectPerimeter;

  private static ZeroElevator mZeroElevatorCommand;

  public static PowerDistributionPanel pdp;

  //---------- Vision Items ------------
  public static Limelight limePanel = new Limelight(Side.PANEL);
  public static Limelight limeBall  = new Limelight(Side.BALL );

  public static ArrayList<TargetEntry> visionTable;
  private final String kVisionTableLocation = "visionlookup";
  //------------------------------------

  @Override
  public void robotInit(){

    pdp = new PowerDistributionPanel();

    Elevator.getInstance();
    Hand.getInstance();
    Finger.getInstance();
    DustPan.getInstance();
    Intake.getInstance();
    Drivetrain.getInstance().zeroSensor();

    OI.getInstance();


    drive = new CheesyDriveHelper();
    mZeroElevatorCommand = new ZeroElevator();

    visionTable = new ArrayList<>();

    for (String data: CSVLogger.fromCSV(kVisionTableLocation)){
      visionTable.add(new TargetEntry(data));
      System.out.println(data);
    }
    
    activeSide = (Hand.getInstance().hasBall()) ? Side.BALL : Side.PANEL;

    // test = new Superstructure(new State(
    //   DustpanBoomState.RETRACTED,
    //   DustpanIntakeState.OFF,
    //   Setpoint.FUEL_MID, 
    //   FingerClapperState.HOLDING, 
    //   FingerArmState.RETRACTED, 
    //   HandState.OFF, 
    //   IntakeBoomState.RETRACTED, 
    //   IntakeWheelState.OFF));

    // test = new IntakePanelSequence();
    // test = new SafeElevatorMove(Setpoint.HATCH_MID);
    // test = new Superstructure(Superstructure.intakePanel);
    // test = Elevator.getInstance().jogElevatorCommand(4000);

    respectPerimeter = false;
  }

  @Override
  public void robotPeriodic() {
    // System.out.println(Elevator.getInstance().getPosition());
    System.out.println("Acceleration: " + Elevator.getInstance().getAcceleration());
    // System.out.println(Elevator.getInstance().getSwitch());
    // System.out.println(aio.getVoltage());
    // System.out.println(Hand.getInstance().getSensorVoltage());
    // System.out.println(Hand.getInstance().hasBall());
    // System.out.println(DustPan.getInstance().getRawCurrent());

    // if (Elevator.getInstance().getSwitch()){
    //   Elevator.getInstance().zero();
    // }

    if(Hand.getInstance().hasBall()){
      activeSide = Side.BALL;
    }else{
      activeSide = Side.PANEL;
    }
  }

  @Override
  public void autonomousInit() {
    mZeroElevatorCommand.start();
  }

  @Override
  public void autonomousPeriodic() {
  }

  @Override
  public void teleopInit() {
    mZeroElevatorCommand.start();

    limePanel.setUSBCam(true);
    limePanel.setLED(LED_STATE.OFF);
    limePanel.setCamMode(CAM_MODE.DRIVER);
    limeBall.setUSBCam(true);
    limeBall.setLED(LED_STATE.OFF);
    limeBall.setCamMode(CAM_MODE.DRIVER);

    Elevator.getInstance().checkNeedsZero();
    Elevator.getInstance().setRaw(0);
    // Finger.getInstance();

    // test.start();
    Drivetrain.getInstance().setBrakeMode(false);
    
    Elevator.getInstance().setPosition(4000);

  }

  private double turnSignal = 0;

  Command test;

  AnalogInput aio;

  @Override
  public void teleopPeriodic(){

    if (OI.getInstance().getVision()){
      turnSignal = limePanel.getTheta() * 0.2;
    }else{
      turnSignal = OI.getInstance().getLeft();
    }
    // System.out.println(Elevator.getInstance().getError());

    // Drivetrain.getInstance().setRawSpeed(0.5, 0.5);

    // DustPan.getInstance().deploy(DustpanBoomState.);
    
    // System.out.println(aio.getVoltage());
    Scheduler.getInstance().run();
    
    Drivetrain.getInstance().setRawSpeed(
      drive.cheesyDrive(
        OI.getInstance().getForward(), 
        turnSignal,
        OI.getInstance().getQuickTurn(), 
        false
      )
    );

    // Climber.getInstance().setClimbRaw(OI.getInstance().getXboxAxis(1));
    // Climber.getInstance().setPullRaw(OI.getInstance().getXboxAxis(5));

    // Elevator.getInstance().setupLogger().log();
    
  }

  boolean mCollected = false;

  @Override
  public void testInit() {
    mCollected = false;
    Drivetrain.getInstance().zeroSensor();
    // Elevator.getInstance().zero();
    Elevator.getInstance().checkNeedsZero();
    // Elevator.getInstance().setPosition(20000);
    // test.start();
  }

  @Override
  public void testPeriodic() {
    // if (!mCollected){
    //   mCollected = VisionTune.getInstance().areaAutoTune(0.1, 4);
    // }
    
  }

  @Override
  public void disabledInit() {
    limePanel.setLED(LED_STATE.OFF);
    limeBall.setLED(LED_STATE.OFF);
    limePanel.setUSBCam(false); 
    Climber.getInstance().setBrakeMode(false);
    Elevator.getInstance().setPosition(0);
    Drivetrain.getInstance().setBrakeMode(false);

    CSVLogger.logCSV("ELEVATORLOG", Elevator.getInstance().setupLogger().get());
  }

  @Override
  public void disabledPeriodic(){
  }
}
