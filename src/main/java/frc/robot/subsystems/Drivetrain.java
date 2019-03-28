package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.RobotMap;
import frc.util.kinematics.pos.RobotPos;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;
import frc.util.talon.CTREFactory;
import jaci.pathfinder.Pathfinder;

public class Drivetrain extends Subsystem implements ILoggable{

    //Singleton:
    private static Drivetrain instance;
    public static Drivetrain getInstance(){
        if(instance == null)
            instance = new Drivetrain();
        return instance;
    }

    //Hardware Controllers:
    private TalonSRX mLeftMaster, mRightMaster;
    private VictorSPX mLeftSlaveA, mLeftSlaveB, mRightSlaveA, mRightSlaveB;

    int[] leftPDPs = {
        RobotMap.DrivetrainMap.kLeftMaster - 1,
        RobotMap.DrivetrainMap.kLeftSlaveA - 1,
        RobotMap.DrivetrainMap.kLeftSlaveB - 1
    };

    private PigeonIMU mGyro;

    //Zero Offsets:
    private double mLeftZeroOffset = 0;
    private double mRightZeroOffset = 0;
    private double mGyroOffset = 0;

    //Odometery variables:
    private double mLastPos, mCurrentPos, mDeltaPos;
    double x, y, theta;
    private Notifier odometery;
    private static final double kOdometeryFix = 0.973;

    //Drive Gains: 
    private double kP = 0.1; //0.4
    private double kI = 0;
    private double kD = 1;
    private double kF = 0.225;//0.17

    private double kFriction = 0; //0.1


    private Drivetrain(){
        mLeftMaster = CTREFactory.createDefaultTalon(RobotMap.DrivetrainMap.kLeftMaster);
        mLeftSlaveA = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kLeftSlaveA, mLeftMaster);
        mLeftSlaveB = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kLeftSlaveB, mLeftMaster);

        mRightMaster = CTREFactory.createDefaultTalon(RobotMap.DrivetrainMap.kRightMaster);
        mRightSlaveA = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kRightSlaveA, mRightMaster);
        mRightSlaveB = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kRightSlaveB, mRightMaster);

        mGyro = new PigeonIMU(Climber.getInstance().mClimber);

        mLeftMaster.configOpenloopRamp(0.08, Constants.kCTREtimeout);
        mRightMaster.configOpenloopRamp(0.08, Constants.kCTREtimeout);

        mLeftMaster.configVoltageCompSaturation(12, Constants.kCTREtimeout);
        mRightMaster.configVoltageCompSaturation(12, Constants.kCTREtimeout);

        mLeftMaster.enableVoltageCompensation(false);
        mRightMaster.enableVoltageCompensation(false);

        mLeftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kCTREpidIDX, Constants.kCTREtimeout);
        mRightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.kCTREpidIDX, Constants.kCTREtimeout);

        mLeftMaster.config_kP(0, kP, Constants.kCTREtimeout);
        mLeftMaster.config_kI(0, kI, Constants.kCTREtimeout);
        mLeftMaster.config_kD(0, kD, Constants.kCTREtimeout);
        mLeftMaster.config_kF(0, kF, Constants.kCTREtimeout);

        mRightMaster.config_kP(0, kP, Constants.kCTREtimeout);
        mRightMaster.config_kI(0, kI, Constants.kCTREtimeout);
        mRightMaster.config_kD(0, kP, Constants.kCTREtimeout);
        mRightMaster.config_kF(0, kF, Constants.kCTREtimeout);

        mLeftMaster.configPeakOutputForward(1);
        mLeftSlaveA.configPeakOutputForward(1);
        mLeftSlaveB.configPeakOutputForward(1);

        mLeftMaster.configPeakOutputReverse(-1);
        mLeftSlaveA.configPeakOutputReverse(-1);
        mLeftSlaveB.configPeakOutputReverse(-1);

        mRightMaster.configPeakOutputForward(1);
        mRightSlaveA.configPeakOutputForward(1);
        mRightSlaveB.configPeakOutputForward(1);

        mRightMaster.configPeakOutputReverse(-1);
        mRightSlaveA.configPeakOutputReverse(-1);
        mRightSlaveB.configPeakOutputReverse(-1);


        mLeftMaster.setSensorPhase(true);
        mRightMaster.setSensorPhase(true);

        mLeftMaster.setInverted(false);
        mLeftSlaveA.setInverted(InvertType.FollowMaster);
        mLeftSlaveB.setInverted(InvertType.FollowMaster);

        mRightMaster.setInverted(true);
        mRightSlaveA.setInverted(InvertType.FollowMaster);
        mRightSlaveB.setInverted(InvertType.FollowMaster);

        setupLogger();

        //Zero Odometery:
        x = 0;
        y = 0;
        theta = 0;

        odometery = new Notifier(() ->{
            mCurrentPos = (getLeftSensorPosition() + getRightSensorPosition())/2.0;
            mDeltaPos = mCurrentPos - mLastPos;
            theta = getGyroAngle();
            x +=  kOdometeryFix * Math.cos(Pathfinder.d2r((theta))) * mDeltaPos;
            y +=  kOdometeryFix * Math.sin(Pathfinder.d2r((theta))) * mDeltaPos;
            mLastPos = mCurrentPos;
        });
    }

    @Override
    protected void initDefaultCommand(){ /*no op*/ };

    /**
     * Set Raw Speed Method
     * 
     * @param left Percent Output
     * @param right Percent Output
     */
    public void setRawSpeed(double left, double right){
        mLeftMaster.set(ControlMode.PercentOutput, left);
        mRightMaster.set(ControlMode.PercentOutput, right); 
    }

    /**
     * Set Raw Speed Method
     * 
     * @param speeds Percent Outputs
     */
    public void setRawSpeed(double[] speeds){
        setRawSpeed(speeds[0], speeds[1]);
    }

    /**
     * Set Velocity Method
     * 
     * @param left  Feet per Second
     * @param right Feet per Second
     */
    public void setVelocity(double left, double right){
        mLeftMaster.set(ControlMode.Velocity, left * Constants.kFPS2NativeU, DemandType.ArbitraryFeedForward , kFriction);
        mRightMaster.set(ControlMode.Velocity, right * Constants.kFPS2NativeU, DemandType.ArbitraryFeedForward, kFriction);
    }

    public TalonSRX getLeftMaster(){
        return mLeftMaster;
    }
    public TalonSRX getRightMaster(){
        return mRightMaster;
    }

    /**
     * Zero Sensor
     * 
     * <p> Zeroes all sensors (encoders + gyro) and odometery information </p>
     */
    public void zeroSensor(){
        mLeftZeroOffset = mLeftMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
        mRightZeroOffset = mRightMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
        mGyroOffset = mGyro.getFusedHeading();

        x = 0;
        y = 0;
        theta = 0;

        mCurrentPos = 0;
        mDeltaPos = 0;
        mLastPos = 0;
    }

    public void setOdometery(RobotPos pos){
        x = pos.getX();
        y = pos.getY();
        theta = pos.getHeading();
        // mGyroOffset = (mGyro.getFusedHeading() + pos.getHeading());
        mGyroOffset = (mGyro.getFusedHeading() - pos.getHeading());
    }

    public void zeroGyro(){
        mGyroOffset = mGyro.getFusedHeading();
    }

    /**
     * Get Gyro Angle.
     * 
     * @return The fused heading from the sensors.
     */
    public double getGyroAngle(){
        return mGyro.getFusedHeading() - mGyroOffset;
    }

    public static double getAngleCorrected(double angle){
        while (angle >= 180) angle -= 360;
        while (angle < -180) angle += 360;
        return angle;
    }

    public void setBrakeMode(boolean brake){
        mLeftMaster.setNeutralMode((brake) ? NeutralMode.Brake : NeutralMode.Coast);
        mRightMaster.setNeutralMode((brake) ? NeutralMode.Brake : NeutralMode.Coast);
    }

    /**
     * Start Odometery Method
     * 
     * <p> Starts tracking the robot position </p>
     * 
     * @param period timestep to update at.
     */
    public void startOdometery(double period){
        odometery.startPeriodic(period);
    }

    /**
     * Stop Odometery Method
     * 
     * <p> Stops tracking the robot position </p>
     */
    public void stopOdometery(){
        odometery.stop();
    }

    /**
     * Get Robot Position Method.
     * 
     * @return The position of the robot.
     */
    public RobotPos getRobotPos(){
        return new RobotPos(x, y, theta);
    }

    /**
     * @return Left Sensor Position in Feet
     */
    public double getLeftSensorPosition(){
        return Constants.kTicks2Feet * (mLeftMaster.getSelectedSensorPosition(Constants.kCTREpidIDX) - mLeftZeroOffset);
    }

    /**
     * @return Right Sensor Position in Feet
     */
    public double getRightSensorPosition(){
        return Constants.kTicks2Feet * (mRightMaster.getSelectedSensorPosition(Constants.kCTREpidIDX) - mRightZeroOffset);
    }

    /**
     * @return Left Sensor Velocity in Feet per Second
     */
    public double getLeftSensorVelocity(){
        return Constants.kNativeU2FPS * mLeftMaster.getSelectedSensorVelocity(Constants.kCTREpidIDX);
    }

    /**
     * @return Left Sensor Velocity in Feet per Second
     */
    public double getRightSensorVelocity(){
        return Constants.kNativeU2FPS * mRightMaster.getSelectedSensorVelocity(Constants.kCTREpidIDX);
    }

    @Override
    public Loggable setupLogger(){
        return new Loggable("DriveLog"){
            @Override
            protected LogObject[] collectData() {
                return new LogObject[]{
                    new LogObject("LeftVel",  getLeftSensorVelocity()),
                    new LogObject("RightVel", getRightSensorVelocity()),

                    new LogObject("Type", "R"),
                    new LogObject("XPos", x  * Constants.kFeet2Meters),
                    new LogObject("YPos", y  * Constants.kFeet2Meters),
                    new LogObject("Heading", theta)
                };
            }
        };
    }
}