package frc.robot;

public class RobotMap{
    public static class DrivetrainMap{
        public static final int kLeftMaster = 1;
        public static final int kLeftSlaveA = 2;
        public static final int kLeftSlaveB = 3;

        public static final int kRightMaster = 16;
        public static final int kRightSlaveA = 15;
        public static final int kRightSlaveB = 14;
    }

    public static class ElevatorMap{
        public static final int kMaster = 0;
        public static final int kSlave = 0;

        public static final int kSwitch = 0;
    }

    public static class ClimberMap{
        public static final int kClimber = 0;

        public static final int kSwitchUp = 0;
        public static final int kSwitchDown = 0;
    }

    public static class IntakeMap{
        public static final int kIntake = 0;
        public static final int kPiston = 1;
    }

    public static class DustpanMap{
        public static final int kIntake = 0;
        public static final int kPiston = 2;
    }

    public static class FingerMap{
        public static final int kFinger = 3;
        public static final int kArm = 4;

        public static final int kSwitch = 0;
    }
}