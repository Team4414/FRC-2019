package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.PPintake.PPState;
import frc.util.Limelight.LED_STATE;

public class StationGrab extends CommandGroup {

    @Override
    protected void initialize() {
        Robot.isStationGrab = true;
    }

    public StationGrab(){
        // addSequential(new Command(){
        
        //     @Override
        //     protected boolean isFinished() {
        //         Robot.isStationGrab = true;
        //         return true;
        //     }
        // });
        
        addSequential(new SafeElevatorMove(Setpoint.PANEL_GRAB));
        addSequential(PPintake.getInstance().setArmCommand(true));
        addSequential(PPintake.getInstance().setPPCommand(PPState.INTAKE));
        addSequential(new WaitCommand(0.5));
        addSequential(PPintake.getInstance().waitForPPCommand());
    }

    @Override
    protected void interrupted() {
        // this.end();
        PPintake.getInstance().setPP(PPState.OFF);
        PPintake.getInstance().setArm(false);
        Robot.isStationGrab = false;
    }

    @Override
    @SuppressWarnings("resource")
    protected void end() {
        new RetractPanel().start();
    }

    private class RetractPanel extends CommandGroup{
        public RetractPanel(){
            addSequential(new WaitCommand(0.25));
            addSequential(PPintake.getInstance().setPPCommand(PPState.INTAKE));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.BLINK));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.BLINK));
            addSequential(PPintake.getInstance().setPPCommand(PPState.HOLDING));
            addSequential(PPintake.getInstance().setArmCommand(false));
            addSequential(new WaitCommand(0.5));
            addSequential(Elevator.getInstance().jogElevatorCommand(Setpoint.STOW));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.OFF));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
        }

        @Override
        protected void end() {
            Robot.isStationGrab = false;
        }

        @Override
        protected void interrupted() {
            this.end();
        }
    }

}