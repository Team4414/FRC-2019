package frc.robot.subsystems;

public class Superstructure{

    public static class State{
        public static Elevator.Setpoint elevatorSetpoint;
        public static Hand.State handState;
        public static boolean fingerState;
    }

    private static boolean mWantsBall = false;
    private static boolean mWantsPanel = false;

    private static boolean mHasBall = false;
    private static boolean mHasPanel = false;


    private static boolean mExtend = true;
    private static boolean mIntakeLock = false;
    private static boolean mDustpanLock = false;
    private static boolean mElevatorLock = true;

    private static boolean mSystemStable = true;
    
    private static boolean mElevatorStable = true;


    private static Superstructure instance;
    public static Superstructure getInstance(){
        if (instance == null)
            instance = new Superstructure();
        return instance;
    }

    private static boolean checkStates(){
        if (mWantsBall){
            if (mWantsPanel){
                return false;
            }
            if (mHasPanel){
                return false;
            }
            if (mHasBall){
                mElevatorLock = false;
                return true;
            }
        }
    }


}