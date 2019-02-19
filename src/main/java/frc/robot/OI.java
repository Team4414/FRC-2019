package frc.robot;

import java.util.LinkedHashMap;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.commands.IntakeBallSequence;
import frc.robot.commands.IntakePanelSequence;
import frc.robot.commands.JogElevator;
import frc.robot.commands.Score;
import frc.robot.commands.actions.Climb;
import frc.robot.commands.actions.GrabPanel;
import frc.robot.commands.actions.ScorePanel;
import frc.robot.commands.actions.Unjam;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Elevator.Position;
import frc.robot.subsystems.Finger.FingerArmState;
import frc.robot.subsystems.Finger.FingerClapperState;
import frc.robot.subsystems.Hand.HandState;

public class OI{
    
    private static OI instance;
    public static OI getInstance(){
        if (instance == null)
            instance = new OI();
        return instance;
    }

    private static final int kThrottleNubID = 0;
    private static final int kTurnNubID = 1;
    private static final int kXboxID = 2;

    private static final int kTurnAxis = 0;
    private static final int kThrottleAxis = 1;

    private static final int kNubTopButton = 11;
    private static final int kNubBotButton = 12;

    private static final int kVisionButtonID = 0;

    private Joystick throttleNub;
    private Joystick turnNub;
    private XboxController xbox;

    private Trigger jogLow;
    private Trigger jogMid;
    private Trigger jogTop;
    private Trigger jogCrg;

    private Trigger intakeBall;
    private Trigger intakePanel;

    private Trigger unJam;
    private Trigger score;

    private Trigger climb;
     
    private OI(){

        throttleNub = new Joystick(kThrottleNubID);
        turnNub = new Joystick(kTurnNubID);
        xbox = new XboxController(kXboxID);

        jogLow = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getAButton();
            }

        };

        jogMid = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getBButton();
            }

        };

        jogTop = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getYButton();
            }

        };

        jogCrg = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getXButton();
            }

        };

        intakeBall = new Trigger(){

            @Override
            public boolean get() {
                return xbox.getBumper(Hand.kLeft);
            }

        };

        intakePanel = new Trigger(){

            @Override
            public boolean get() {
                return xbox.getBumper(Hand.kRight);
            }

        };

        climb = new Trigger(){
        
            @Override
            public boolean get() {
                xbox.getStartButton();
                return true;
            }
        };

        IntakePanelSequence intake = new IntakePanelSequence();
        IntakeBallSequence  ball = new IntakeBallSequence();

        intakePanel.whenActive(intake);

        intakePanel.whenInactive(new Command(){
            @Override
            protected boolean isFinished() {
                intake.cancel();
                return true;
            }
        });

        intakeBall.whenActive(ball);

        intakeBall.whenInactive(new Command(){
            @Override
            protected boolean isFinished() {
                ball.cancel();
                return true;
            }
        });

        unJam = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getStickButton(Hand.kLeft);
            }

        };

        score = new Trigger(){
        
            @Override
            public boolean get() {
                return turnNub.getRawButton(kNubTopButton);
            }

        };

        Command scoreCommand = new Score();

        score.whenActive(scoreCommand);

        score.whenInactive(new Command(){
        
            @Override
            protected boolean isFinished() {
                scoreCommand.cancel();
                // Finger.getInstance().setArm(FingerArmState.RETRACTED);
                // Finger.getInstance().setFinger(FingerClapperState.HOLDING);
                // frc.robot.subsystems.Hand.getInstance().set(HandState.OFF);
                return true;
            }
        });

        jogLow.whenActive(new JogElevator(Position.LOW));
        jogMid.whenActive(new JogElevator(Position.MIDDLE));
        jogTop.whenActive(new JogElevator(Position.HIGH));
        jogCrg.whenActive(new JogElevator(Position.SECOND));

        climb.whileActive(new Climb());

        unJam.whileActive(new Unjam());
    }

    public double getLeft(){
        return turnNub.getRawAxis(kTurnAxis);
    }

    public double getForward(){
        return -throttleNub.getRawAxis(kThrottleAxis);
    }

    public boolean getQuickTurn(){
        return throttleNub.getRawButton(kNubBotButton);
    }

    public XboxController getXbox(){
        return xbox;
    }

    public double getXboxAxis(int axis){
        return xbox.getRawAxis(axis);
    }

    public boolean getVision(){
        return throttleNub.getRawButton(kNubTopButton);
    }
}