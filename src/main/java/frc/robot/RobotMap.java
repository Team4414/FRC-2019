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
        public static final int kMaster = 4;
        public static final int kSlave = 5;

        public static final int kSwitch = 0;
    }

    public static class ClimberMap{
        public static final int kClimber = 13;
        public static final int kPuller = 12;

        public static final int kPullerPiston = 4;

        public static final int kSwitchUp = 3;
        public static final int kSwitchDown = 4;
    }

    public static class HandMap{
        public static final int kHand = 6;

        public static final int kSensorPort = 0;
    }

    public static class IntakeMap{
        public static final int kIntake = 11;
        public static final int kPiston = 1;
    }

    public static class DustpanMap{
        public static final int kIntake = 7;
        public static final int kPiston = 0;
    }

    public static class PPintakeMap{
        public static final int kPP = 20;
        public static final int kArm = 2;
    }
}