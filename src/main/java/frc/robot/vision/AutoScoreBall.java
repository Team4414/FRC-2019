package frc.robot.vision;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Hand.HandArmState;
import frc.robot.subsystems.Hand.HandState;
import frc.util.Limelight.LED_STATE;

public class AutoScoreBall extends CommandGroup{

    @Override
    protected void initialize() {
    }

    public AutoScoreBall(){
        addSequential(new AutoDriveIn());
        addSequential(Hand.getInstance().setArmCommand(HandArmState.EXTENDED));
        addSequential(new WaitCommand(0.2));
        addSequential(Hand.getInstance().setHandCommand(HandState.DROP));
        addSequential(new WaitCommand(0.5));
        addSequential(Hand.getInstance().setArmCommand(HandArmState.RETRACTED));
        addSequential(Hand.getInstance().setHandCommand(HandState.OFF));
    }

    @Override
    protected void interrupted() {
        Hand.getInstance().setArm(HandArmState.RETRACTED);
        if (Hand.getInstance().hasBall()){
            Hand.getInstance().set(HandState.HOLDING);
        }else{
            Hand.getInstance().set(HandState.OFF);
        }
    }
    @Override
    public synchronized void cancel() {
        this.interrupted();
        super.cancel();
    }

    @Override
    protected void end() {
        VisionHelper.getActiveCam().setLED(LED_STATE.OFF);
        Drivetrain.getInstance().setBrakeMode(false);
    }
}

