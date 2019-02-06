package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
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
    @SuppressWarnings("unused") 
    private VictorSPX mLeftSlaveA, mLeftSlaveB, mRightSlaveA, mRightSlaveB;

    private PigeonIMU mGyro;

    //Zero Offsets:
    private double mLeftZeroOffset = 0;
    private double mRightZeroOffset = 0;
    private double mGyroOffset = 0;

    //Drive Gains: 
    private double kP = 0.4;
    private double kI = 0;
    private double kD = 0;
    private double kF = 0.17;

    private double kFriction = 0; //0.1

    //Odometery variables:
    private double mLastPos, mCurrentPos, mDeltaPos;
    volatile double x, y, theta;
    private Notifier odometery;


    private Drivetrain(){
        mLeftMaster = CTREFactory.createDefaultTalon(RobotMap.DrivetrainMap.kLeftMaster);
        mLeftSlaveA = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kLeftSlaveA, mLeftMaster);
        mLeftSlaveB = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kLeftSlaveB, mLeftMaster);

        mRightMaster = CTREFactory.createDefaultTalon(RobotMap.DrivetrainMap.kRightMaster);
        mRightSlaveA = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kRightSlaveA, mRightMaster);
        mRightSlaveB = CTREFactory.createPermanentSlaveVictor(RobotMap.DrivetrainMap.kRightSlaveB, mRightMaster);

        mGyro = new PigeonIMU(0);

        mLeftMaster.configOpenloopRamp(0.01, Constants.kCTREtimeout);
        mRightMaster.configOpenloopRamp(0.01, Constants.kCTREtimeout);

        mLeftMaster.configVoltageCompSaturation(12, Constants.kCTREtimeout);
        mRightMaster.configVoltageCompSaturation(12, Constants.kCTREtimeout);

        mLeftMaster.enableVoltageCompensation(true);
        mRightMaster.enableVoltageCompensation(true);

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

        mLeftMaster.setSensorPhase(true);
        mRightMaster.setSensorPhase(true);

        mRightMaster.setInverted(true);
        mRightSlaveA.setInverted(true);
        mRightSlaveB.setInverted(true);

        //Zero Odometery:
        x = 0;
        y = 0;
        theta = 0;

        //Set up Odometery Notifier
        odometery = new Notifier(() ->{
            mCurrentPos = (getLeftSensorPosition() + getRightSensorPosition())/2.0;
            mDeltaPos = mCurrentPos - mLastPos;
            theta = getGyroAngle();
            x +=  Math.cos(d2r(theta)) * mDeltaPos;
            y +=  Math.sin(d2r(theta)) * mDeltaPos;
            mLastPos = mCurrentPos;
        });

        setupLogger();
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

    /**
     * Zero Sensor
     * 
     * <p> Zeroes all sensors (encoders + gyro) and odometery information </p>
     */
    public void zeroSensor(){
        mLeftZeroOffset = mLeftMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
        mRightZeroOffset = mRightMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
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
     * Sets the Position of the Robot
     */
    public void setPosition(RobotPos pos){
        this.x = pos.getX();
        this.y = pos.getY();
        this.theta = pos.getHeading();
    }

    @Override
    public Loggable setupLogger(){
        return new Loggable("DriveLog"){
            @Override
            protected LogObject[] collectData() {
                return new LogObject[]{
                    new LogObject("LeftVel",  getLeftSensorVelocity()),
                    new LogObject("RightVel", getRightSensorVelocity())
                };
            }
        };
    }

    private static double d2r(double degrees){
        return (degrees / 180) * Math.PI;
    }
}