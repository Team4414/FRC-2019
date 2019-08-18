package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.Robot;
import frc.robot.subsystems.Hand;

public class WaitForBall extends Command{

    @Override
    protected boolean isFinished() {
        return Hand.getInstance().hasBall();
        // return Robot.pdp.getCurrent(5) > 6;
        
    }

    @Override
    protected void end() {
    }

}