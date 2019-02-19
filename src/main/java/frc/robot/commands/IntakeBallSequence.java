package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.actions.SafeElevatorMove;
import frc.robot.commands.actions.WaitForBall;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake.IntakeWheelState;

public class IntakeBallSequence extends CommandGroup{

    // private boolean mInterrupted = false;

    public IntakeBallSequence(){

        // addSequential(new Command(){
        
        //     @Override
        //     protected boolean isFinished() {
        //         mInterrupted = false;
        //         return true;
        //     }
        // });

        addSequential(Intake.getInstance().intakeCommand(IntakeWheelState.ON));
        addSequential(Intake.getInstance().deployCommand(true));
        addSequential(new SafeElevatorMove(Setpoint.BOTTOM));
        addSequential(Elevator.getInstance().lockElevatorCommand(true));
        addSequential(Hand.getInstance().setHandCommand(HandState.INTAKING));
        addSequential(new WaitForBall());
    }

    @Override
    protected void end() {
        // if (!mInterrupted){
        //     new StowSequence().start();
        // }
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

            addSequential(Hand.getInstance().setHandCommand(HandState.HOLDING)); 
            addSequential(Elevator.getInstance().lockElevatorCommand(false));
            addSequential(new SafeElevatorMove(Setpoint.FUEL_LOW));
            addSequential(Intake.getInstance().deployCommand(false));
            addSequential(Intake.getInstance().intakeCommand(IntakeWheelState.OFF));
        }

    }

    public class DebugMessage extends Command{

        String mMessage;

        public DebugMessage(String message){
            mMessage = message;
        }

        @Override
        protected boolean isFinished() {
            System.out.println(mMessage);
            return true;
        }

    }

}