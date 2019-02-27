package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Intake.IntakeWheelState;
import frc.util.Limelight.LED_STATE;

public class IntakeBallSequence extends CommandGroup {

    public IntakeBallSequence() {
        addSequential(new SafeElevatorMove(Setpoint.BOTTOM));
        addSequential(Elevator.getInstance().lockElevatorCommand(true));
        addSequential(Intake.getInstance().intakeCommand(IntakeWheelState.ON));
        addSequential(Intake.getInstance().deployCommand(true));
        addSequential(Hand.getInstance().setHandCommand(HandState.INTAKING));
        addSequential(new WaitForBall());
    }

    @Override
    @SuppressWarnings("resource")
    protected void end() {
        new StowSequence().start();
    }
    
    @Override
    protected void interrupted() {
        Intake.getInstance().deploy(false);
        Intake.getInstance().intake(false);
        Hand.getInstance().set(HandState.OFF);
        Intake.getInstance().intake(IntakeWheelState.OFF);
        Elevator.getInstance().lockElevator(false);
        Elevator.getInstance().setPosition(Setpoint.STOW);
    }

    private class StowSequence extends CommandGroup{

        public StowSequence(){
            addSequential(new Command(){
        
                @Override
                protected boolean isFinished() {
                    Robot.activeSide = Side.BALL;
                    return true;
                }
            });

            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.BLINK));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.BLINK));
            addSequential(Hand.getInstance().setHandCommand(HandState.HOLDING)); 
            addSequential(Elevator.getInstance().lockElevatorCommand(false));
            addSequential(new SafeElevatorMove(Setpoint.FUEL_LOW));
            addSequential(Intake.getInstance().deployCommand(false));
            addSequential(Intake.getInstance().intakeCommand(IntakeWheelState.OFF));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.OFF));
        }

    }

}