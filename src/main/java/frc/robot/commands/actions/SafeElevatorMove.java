package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Intake.IntakeBoomState;

public class SafeElevatorMove extends CommandGroup{

    IntakeBoomState mInitState; //state to return intake to after elevator move

    private static final double kIntakeMoveTime = 0.5;

    public SafeElevatorMove(Setpoint setpoint){
        //store initial state
        addSequential(new DebugMessage("STARTED"));
        // addSequential(new Command(){
        
        //     @Override
        //     protected boolean isFinished() {
        //         mInitState = Intake.boomState;
        //         return true;
        //     }
        // });

        addSequential(new DebugMessage("1"));
        //handle safety by moving/waiting on the intake
        // addSequential(new DoMoveIntakeAndWait(setpoint));
        addSequential(new DebugMessage("2"));

        //jog the elevator
        addSequential(Elevator.getInstance().jogElevatorCommand(setpoint));
        addSequential(new DebugMessage("3"));

        //restore the intake boom back to initial state
        // addSequential(new Command(){
        
        //     @Override
        //     protected boolean isFinished() {
        //         Intake.getInstance().lock(false);
        //         Intake.getInstance().deploy(mInitState);
        //         return true;
        //     }
        // });
    }

    private class DoMoveIntakeAndWait extends CommandGroup{

        private DoMoveIntake mMove;

        public DoMoveIntakeAndWait(Setpoint setpoint){
            mMove = new DoMoveIntake(setpoint);

            addSequential(mMove);
            addSequential(new Command(){
            
                @Override
                protected boolean isFinished() {
                    return !mMove.needsWait();
                }

            }, kIntakeMoveTime);
        }

        private class DoMoveIntake extends Command{

            private Setpoint mSetpoint;
            private boolean mNeedsWait;
    
            public DoMoveIntake(Setpoint setpoint){
                mSetpoint = setpoint;
                mNeedsWait = false;
            }
    
            @Override
            protected void initialize() {
                if (Hand.getInstance().hasBall()){
                    //if the hand has a ball
    
                    if (Elevator.getInstance().getPosition() < Elevator.kHandThreshold){
                        //and you are below the target

                        if (Elevator.getSetpoint(mSetpoint) > Elevator.kHandThreshold){
                            //and you want to go above the threshold, you need to move the elevator.
                            Intake.getInstance().deploy(true);
                            Intake.getInstance().lock(true);
                            mNeedsWait = true;
                        }
    
                    }
    
                    if (Elevator.getInstance().getPosition() > Elevator.kHandThreshold){
                        //and you are aboce the target
    
                        if (Elevator.getSetpoint(mSetpoint) < Elevator.kHandThreshold){
                            //and you want to go below the threshold, you need to move the elevator.
                            Intake.getInstance().deploy(true);
                            Intake.getInstance().lock(true);
                            mNeedsWait = true;
                        }
                    }
                }
            }
    
            @Override
            protected boolean isFinished() {
                return true;
            }
    
            public boolean needsWait(){
                return mNeedsWait;
            }
    
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