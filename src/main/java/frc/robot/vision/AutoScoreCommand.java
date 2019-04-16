package frc.robot.vision;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.PPintake.PPState;
import frc.util.Limelight.LED_STATE;

public class AutoScoreCommand extends CommandGroup{

    @Override
    protected void initialize() {
    }

    public AutoScoreCommand(){
        addSequential(new AutoDriveIn());
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(0.35));
        addSequential(PPintake.getInstance().setPPCommand(PPState.SCORE));
        addSequential(new WaitCommand(0.2));
        addSequential(PPintake.getInstance().setArmCommand(false));
        addSequential(new WaitCommand(0.2));
        addSequential(PPintake.getInstance().setPPCommand(PPState.OFF));
    }

    @Override
    protected void interrupted() {
        PPintake.getInstance().setPP(PPState.HOLDING);
        PPintake.getInstance().setArm(false);
    }
    @Override
    public synchronized void cancel() {
        this.interrupted();
        super.cancel();
    }

    @Override
    protected void end() {
        VisionHelper.getActiveCam().setLED(LED_STATE.OFF);
        // Drivetrain.getInstance().setBrakeMode(false);
    }
}

