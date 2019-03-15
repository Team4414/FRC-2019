package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.PPintake.PPState;
import frc.util.Limelight.LED_STATE;

public class StationGrab extends CommandGroup{

    public StationGrab(){
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                Robot.doAutoPlace = false;
                return true;
            }
        });
        addSequential(PPintake.getInstance().setPPCommand(PPState.INTAKE));
        addSequential(new SafeElevatorMove(Setpoint.STOW));
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(new WaitCommand(2));
        addSequential(PPintake.getInstance().waitForPPCommand());
    }

    @Override
    protected void interrupted() {
        this.end();
    }

    @Override
    @SuppressWarnings("resource")
    protected void end() {
        Robot.doAutoPlace = true;
        new RetractPanel().start();
    }

    private class RetractPanel extends CommandGroup{
        public RetractPanel(){
            addSequential(new WaitCommand(0.25));
            addSequential(PPintake.getInstance().setPPCommand(PPState.HOLDING));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.BLINK));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.BLINK));
            addSequential(PPintake.getInstance().setPPCommand(PPState.HOLDING));
            addSequential(PPintake.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(0.5));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.OFF));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
            addSequential(new Command(){
        
                @Override
                protected boolean isFinished() {
                    Robot.doAutoPlace = true;
                    return true;
                }
            });
        }
    }

}