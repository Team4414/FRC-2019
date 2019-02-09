package frc.robot.subsystems;

import frc.robot.subsystems.DustPan.IntakeState;
import frc.robot.subsystems.DustPan.PanelBoomState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Finger.FingerState;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake.BallBoomState;
import frc.robot.subsystems.Intake.WheelState;

public class Superstructure{

    public static class State{
        public DustPan.PanelBoomState dustPanBoomState;
        public DustPan.IntakeState dustPanIntakeState;
        public Elevator.Setpoint elevatorSetpoint;
        public Finger.FingerState fingerState;
        public Hand.HandState handState;
        public Intake.BallBoomState intakeBoomState;
        public Intake.WheelState intakeState;

        public State(
            DustPan.PanelBoomState dustPanBoomState,
            DustPan.IntakeState dustPanIntakeState,
            Elevator.Setpoint elevatorSetpoint,
            Finger.FingerState fingerState,
            Hand.HandState handState,
            Intake.BallBoomState intakeBoomState,
            Intake.WheelState intakeState
        )
        {
            this.dustPanBoomState = dustPanBoomState;
            this.dustPanIntakeState = dustPanIntakeState;
            this.elevatorSetpoint = elevatorSetpoint;
            this.fingerState = fingerState;
            this.handState = handState;
            this.intakeBoomState = intakeBoomState;
            this.intakeState = intakeState;
        }

        public State(){
            this(null, null, null, null, null, null, null);
        }
    }

    public static State mIntakeBall = new State(
        PanelBoomState.RETRACTED, 
        IntakeState.OFF, 
        Setpoint.BOTTOM, 
        FingerState.OPEN, 
        HandState.HOLDING, 
        BallBoomState.EXTENDED, 
        WheelState.ON
    );

    public static State mIntakePanel = new State(
        PanelBoomState.EXTENDED, 
        IntakeState.ON, 
        Setpoint.BOTTOM, 
        FingerState.OPEN, 
        HandState.HOLDING, 
        BallBoomState.EXTENDED, 
        WheelState.ON
    );

    public static State mNullState = new State();

    public static State mCurrState = new State();

    private static PanelBoomState mPanelPickupLocation = PanelBoomState.EXTENDED;

    private static boolean mWantsBall = false;
    private static boolean mWantsPanel = false;

    private static boolean mHasBall = false;
    private static boolean mHasPanel = false;

    private static State mTargetState = mNullState;

    private static boolean mExtend = true;
    private static boolean mIntakeLock = false;
    private static boolean mDustpanLock = false;

    private static boolean mSystemStable = true;
    
    private static boolean mElevatorStable = true;

    /**
     * The point where the elevator carrying a ball would impact the intake
     */
    private static final double mHandThreshold = Elevator.getSetpoint(Elevator.Setpoint.HAND_CLR);
    private static double mElevatorPos = 0;


    private static Superstructure instance;
    public static Superstructure getInstance(){
        if (instance == null)
            instance = new Superstructure();
        return instance;
    }

    private static boolean generateState(boolean ballWant, boolean panelWant, Elevator.Setpoint setpoint){
        State targState = new State();
        if (ballWant){
            //if you want the ball
            if (ballWant){
                //and you want the panel, you messed up
                mWantsBall = false;
                return false;
            }
            if (ballWant){
                //and you have a panel, you messed up
                mWantsBall = false;
                return false;
            }
            if (ballWant){
                //and you have a ball, unlock the elevator
                //setting to null signifies the elevator is "free" to later assume commanded setpoint
                targState.elevatorSetpoint = null;
                mWantsBall = true;
            }
            if (!mHasBall){
                //and you don't have a ball, intake one.
                targState = mIntakeBall;
            }
        }else{
            //if you don't want the ball

            if (mHasBall){
                //and you have a ball, drop it
                targState.handState = HandState.DROP;
            }

            //just make sure the elevator is free
            targState.elevatorSetpoint = null;

            mWantsBall = false;
        }

        if (panelWant){
            //if you want the panel

            if (mWantsBall){
                //and you want the ball, you messed up
                mWantsPanel = false;
                return false;
            }

            if (mHasBall){
                //and you have a ball, you messed up
                mWantsPanel = false;
                return false;
            }

            if (mHasPanel){
                //and you have a panel, unlock the elevator
                mWantsPanel = true;
                targState.elevatorSetpoint = null;
            }

            if (!mHasPanel){
                //and you don't have the panel, intake one
                targState = mIntakePanel;

                //make sure you intake correctly
                targState.dustPanBoomState = mPanelPickupLocation;
            }
        }else{
            //you don't want the panel
            targState.fingerState = FingerState.OPEN;
            mWantsPanel = false;

            //just make sure elevator is unlocked
            targState.elevatorSetpoint = null;
        }

        if (targState.elevatorSetpoint == null){
            //if the elevator is free

            if (mElevatorPos > mHandThreshold){
                //if you are above the threshold
                
                if (Elevator.getSetpoint(setpoint) < mHandThreshold){
                    //and the setpoint is below the threshold, you messed up
                    targState.elevatorSetpoint = mCurrState.elevatorSetpoint;
                    return false;
                }
            }
        }

        return false;
    }


}