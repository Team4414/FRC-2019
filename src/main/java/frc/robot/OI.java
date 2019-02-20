package frc.robot;

import edu.wpi.first.wpilibj.GenericHID.Hand;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.buttons.Trigger;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.commands.Score;
import frc.robot.commands.Unjam;
import frc.robot.commands.balls.IntakeBallSequence;
import frc.robot.commands.climbing.ClimbingGroup;
import frc.robot.commands.climbing.RetractClimber;
import frc.robot.commands.elevator.JogElevator;
import frc.robot.commands.panels.IntakePanelSequence;
import frc.robot.commands.panels.StationGrab;
import frc.robot.subsystems.Climber;
import frc.robot.subsystems.Elevator.Position;

public class OI {

    private static OI instance;

    public static OI getInstance() {
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
    private Trigger stationPanel;

    private Trigger climb;
    private Trigger releaseClimber;
    private Trigger retractClimb;
     
    private OI(){

        throttleNub = new Joystick(kThrottleNubID);
        turnNub = new Joystick(kTurnNubID);
        xbox = new XboxController(kXboxID);

        //-------------------- Trigger Setup ------------------

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
                return xbox.getPOV() == 0;
            }
        };

        releaseClimber = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getPOV() == 270;
            }
        };

        retractClimb = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getPOV() == 180;
            }
        };

        stationPanel = new Trigger(){
        
            @Override
            public boolean get() {
                return xbox.getStickButton(Hand.kRight);
            }
        };

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
        //-------------------------------------------------------

        //---------- Binding ----------
        Command intake = new IntakePanelSequence();
        Command ball = new IntakeBallSequence();
        Command scoreCommand = new Score();
        Command climbCommand = new ClimbingGroup();

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



        score.whenActive(scoreCommand);
        score.whenInactive(new Command(){
        
            @Override
            protected boolean isFinished() {
                scoreCommand.cancel();
                return true;
            }
        });



        jogLow.whenActive(new JogElevator(Position.LOW));
        jogMid.whenActive(new JogElevator(Position.MIDDLE));
        jogTop.whenActive(new JogElevator(Position.HIGH));
        jogCrg.whenActive(new JogElevator(Position.SECOND));


        climb.whenActive(climbCommand);
        climb.whenInactive(new Command(){
        
            @Override
            protected boolean isFinished() {
                climbCommand.cancel();
                return true;
            }
        });


        retractClimb.whileActive(new RetractClimber());
        releaseClimber.whenActive(Climber.getInstance().deployPistonCommand(true));


        StationGrab stationGrab = new StationGrab();


        stationPanel.whenActive(stationGrab);
        stationPanel.whenInactive(new Command(){
        
            @Override
            protected boolean isFinished() {
                stationGrab.cancel();
                return true;
            }
        });


        unJam.whileActive(new Unjam());
        //--------- ------------------
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