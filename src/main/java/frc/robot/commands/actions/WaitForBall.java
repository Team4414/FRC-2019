package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import frc.robot.subsystems.Hand;

public class WaitForBall extends Command{

    @Override
    protected boolean isFinished() {
        System.out.println("RUnning");
        return Hand.getInstance().hasBall();
        
    }

    @Override
    protected void end() {
        System.out.println("!!!!!!!");
    }

}