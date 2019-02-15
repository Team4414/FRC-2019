package frc.robot.subsystems;

import frc.robot.subsystems.Hand;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.DustPan.DustpanBoomState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Finger.FingerArmState;
import frc.robot.subsystems.Finger.FingerClapperState;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake.IntakeBoomState;
import frc.robot.subsystems.Intake.IntakeWheelState;

public class Superstructure extends Command{

    private static final double kIntakeMoveTime = 1;
    private static final double kArmMoveTime = 1;
    private static final double kElevatorDeadzone = 100; //ticks
    private static final double kHandThreshold = Elevator.getSetpoint(Elevator.Setpoint.HAND_CLR);

    public static class State{
        public DustPan.DustpanBoomState dustPanBoomState;
        public DustPan.DustpanIntakeState dustPanIntakeState;
        public Elevator.Setpoint elevatorSetpoint;
        public Finger.FingerClapperState fingerClapperState;
        public Finger.FingerArmState fingerArmState;
        public Hand.HandState handState;
        public Intake.IntakeBoomState intakeBoomState;
        public Intake.IntakeWheelState intakeWheelState;

        public State(
            DustPan.DustpanBoomState dustPanBoomState,
            DustPan.DustpanIntakeState dustPanIntakeState,
            Elevator.Setpoint elevatorSetpoint,
            Finger.FingerClapperState fingerState,
            Finger.FingerArmState fingerArmState,
            Hand.HandState handState,
            Intake.IntakeBoomState intakeBoomState,
            Intake.IntakeWheelState intakeState
        )
        {
            this.dustPanBoomState = dustPanBoomState;
            this.dustPanIntakeState = dustPanIntakeState;
            this.elevatorSetpoint = elevatorSetpoint;
            this.fingerArmState = fingerArmState;
            this.fingerClapperState = fingerState;
            this.handState = handState;
            this.intakeBoomState = intakeBoomState;
            this.intakeWheelState = intakeState;
        }

        public State(){
            this(null, null, null, null, null, null, null, null);
        }
    }

    public static State intakeBall = new State(
        DustpanBoomState.RETRACTED,
        DustpanIntakeState.OFF,
        Setpoint.BOTTOM,
        FingerClapperState.HOLDING,
        FingerArmState.RETRACTED,
        HandState.INTAKING,
        IntakeBoomState.EXTENDED,
        IntakeWheelState.ON
    );

    public static State intakePanel = new State(
        DustpanBoomState.EXTENDED,
        DustpanIntakeState.ON,
        Setpoint.STOW,
        FingerClapperState.OPEN,
        FingerArmState.RETRACTED,
        HandState.OFF,
        IntakeBoomState.RETRACTED,
        IntakeWheelState.OFF
    );

    public static State grabStationPanel = new State(
        DustpanBoomState.RETRACTED,
        DustpanIntakeState.OFF,
        Setpoint.STOW,
        FingerClapperState.OPEN,
        FingerArmState.EXTENDED,
        HandState.OFF,
        IntakeBoomState.RETRACTED,
        IntakeWheelState.OFF
    );

    public static State mNullState = new State();

    private static State mTargetState = mNullState;

    private static boolean mExtend = true;

    public Superstructure(State state){

        
        // set all states to a valid state regardless of whether they need to be changed or not
        if (state.elevatorSetpoint == null){
            state.elevatorSetpoint = Elevator.currentState;
        }

        if (state.fingerArmState == null){
            state.fingerArmState = Finger.armState;
        }

        if (state.fingerClapperState == null){
            state.fingerClapperState = Finger.clapperState;
        }

        if (state.handState == null){
            state.handState = Hand.handState;
        }

        if (state.dustPanBoomState == null){
            state.dustPanBoomState = DustPan.boomState;
        }

        if (state.dustPanIntakeState == null){
            state.dustPanIntakeState = DustPan.intakeState;
        }

        if (state.intakeBoomState == null){
            state.intakeBoomState = Intake.boomState;
        }

        if (state.intakeWheelState == null){
            state.intakeWheelState = Intake.wheelState;
        }

        mTargetState = state;
    }

    private boolean mDustpanBoomDone;
    private boolean mDustpanIntakeDone;
    private boolean mIntakeBoomDone;
    private boolean mIntakeWheelsDone;
    private boolean mArmDone;
    private boolean mHandDone;
    private boolean mClapperDone;
    private boolean mElevatorDone;

    private boolean mElevatorNeedsIntakeMove;

    private double mIntakeBoomTime = -1;
    private double mArmTime = -1;

    @Override
    protected void initialize() {
        mDustpanBoomDone = false;
        mDustpanIntakeDone = false;
        mIntakeBoomDone = false;
        mIntakeWheelsDone = false;
        mArmDone = false;
        mHandDone = false;
        mClapperDone = false;
        mElevatorDone = false;

        mIntakeBoomTime = -1;
        mArmTime = -1;
    }

    @Override
    protected void execute() {

        if (!mDustpanBoomDone){
            //if you need to move the dustpan up or down

            if (mExtend){
                //and you aren't perimeter locked
                DustPan.getInstance().deploy(mTargetState.dustPanBoomState);
            }
            
            mDustpanBoomDone = true;
        }

        if (!mDustpanIntakeDone){
            //if you need to run the dustpan intake, do it
            DustPan.getInstance().intake(mTargetState.dustPanIntakeState);
            mDustpanBoomDone = true;
        }

        if (!mHandDone){
            //if you need to set the hand, do it
            Hand.getInstance().set(mTargetState.handState);
            mHandDone = true;
        }

        if (!mIntakeWheelsDone){
            //if you need to run the intake, do it
            Intake.getInstance().intake(mTargetState.intakeWheelState);
            mIntakeWheelsDone = true;
        }

        if (!mArmDone){
            //if you need to move the arm, do it
            Finger.getInstance().setArm(mTargetState.fingerArmState);

            if (mArmTime == -1){
                //if the timer is not started, start it
                mArmTime = Timer.getFPGATimestamp();
            }

            if (timer(mArmTime, kArmMoveTime)){
                //if the timer has finished
                mArmTime = -1;
                mArmDone = true;
            }
        }

        if(!mClapperDone){
            //if the clapper needs to be set

            if (mArmDone){
                //and the arm has finished moving, set the arm
                Finger.getInstance().setFinger(mTargetState.fingerClapperState);
            }
        }

        if (!mElevatorDone){
            mElevatorNeedsIntakeMove = false;
            if (Hand.getInstance().hasBall()){
                //if the hand has a ball

                if (Elevator.getInstance().getPosition() > Elevator.getSetpoint(mTargetState.elevatorSetpoint)){
                    //and you are above the target

                    if (Elevator.getSetpoint(mTargetState.elevatorSetpoint) < kHandThreshold){
                        //and you want to go below the threshold, you need to move the elevator.
                        mElevatorNeedsIntakeMove = true;
                    }

                }

                if (Elevator.getInstance().getPosition() < Elevator.getSetpoint(mTargetState.elevatorSetpoint)){
                    //and you are below the target

                    if (Elevator.getSetpoint(mTargetState.elevatorSetpoint) > kHandThreshold){
                        //and you want to go above the threshold, you need to move the elevator.
                        mElevatorNeedsIntakeMove = true;
                    }
                }
            }

            if (mElevatorNeedsIntakeMove){

                if (!mExtend){
                    //if you can't extend, don't move the elevator.
                    mElevatorDone = true;
                }else{
                    //if the elevator needs the intake to move, deploy it
                    Intake.getInstance().deploy(IntakeBoomState.EXTENDED);

                    if (mIntakeBoomTime == -1){
                        //if the timer is not started, start it
                        mIntakeBoomTime = Timer.getFPGATimestamp();
                    }

                    if (timer(mIntakeBoomTime, kIntakeMoveTime)){
                        //if the timer has finished, signal it
                        mIntakeBoomTime = -1;
                        mElevatorNeedsIntakeMove = false;
                        //if the intake had to extend anyway, than you are done
                        if (mTargetState.intakeBoomState == IntakeBoomState.EXTENDED){
                            mIntakeBoomDone = true;
                        }
                    }
                }
            }

            if (!mElevatorNeedsIntakeMove){
                //if the elevator does not need the intake to move, set it
                Elevator.getInstance().setPosition(mTargetState.elevatorSetpoint);
            }

            if (Math.abs(Elevator.getInstance().getPosition() - Elevator.getSetpoint(mTargetState.elevatorSetpoint)) < kElevatorDeadzone){
                //if the elevator is close to its target, the elevator is done moving
                mElevatorDone = true;
            }
        }

        if (!mIntakeBoomDone){
            //if you still need to set your intake

            if (mExtend){
                //and you can extend

                if (mElevatorDone){
                    //and your elevator is done moving, set the intake
                    Intake.getInstance().deploy(mTargetState.intakeBoomState);
        
                    if (mIntakeBoomTime == -1){
                        //if the timer is not started, start it
                        mIntakeBoomTime = Timer.getFPGATimestamp();
                    }
        
                    if (timer(mIntakeBoomTime, kIntakeMoveTime)){
                        //if the timer has finished
                        mIntakeBoomTime = -1;
                        mIntakeBoomDone = true;
                    }
                }
            }else{
                mIntakeBoomDone = true;
            }
        }
    }

    @Override
    protected boolean isFinished() {
        //if literally everything is done, finish the command.
        return (
            mDustpanBoomDone && mDustpanIntakeDone &&
            mIntakeBoomDone  && mIntakeWheelsDone  &&
            mArmDone         && mHandDone          &&
            mClapperDone     && mElevatorDone
        );
    };



    private boolean timer(double initTime, double delay){
        return (initTime + delay >= Timer.getFPGATimestamp());
    }


}