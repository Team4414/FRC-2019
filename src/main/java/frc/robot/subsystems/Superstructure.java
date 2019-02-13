package frc.robot.subsystems;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.DustPan.IntakeState;
import frc.robot.subsystems.DustPan.PanelBoomState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Finger.FingerState;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake.BallBoomState;
import frc.robot.subsystems.Intake.WheelState;

public class Superstructure extends Command{

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

    public static State mGrabStationPanel = new State(
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

    private static State mTargetState = mNullState;

    private static boolean mExtend = true;

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

    @Override
    protected boolean isFinished() {
        return false;
    }


}