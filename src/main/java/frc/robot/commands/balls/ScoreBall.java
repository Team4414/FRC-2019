package frc.robot.commands.balls;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.commands.elevator.JogElevator;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Elevator.Position;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Hand.HandArmState;
import frc.robot.subsystems.Hand.HandState;

public class ScoreBall extends CommandGroup{

    public ScoreBall(){
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                if (Robot.wantsToCargoOnScore){
                    Elevator.getInstance().setPosition(Setpoint.CARGO_SHIP);
                }
                return Elevator.getInstance().isAtSetpoint();
            }
        });
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                Robot.wantsToCargoOnScore = false;
                return true;
            }
        });
        addSequential(Hand.getInstance().setArmCommand(HandArmState.EXTENDED));
        addSequential(new WaitCommand(0.5));
        addSequential(Hand.getInstance().setHandCommand(HandState.DROP));
    }

    // @SuppressWarnings("resource")
    @Override
    public synchronized void cancel() {
        Hand.getInstance().set(HandState.HOLDING);
        Hand.getInstance().setArm(HandArmState.RETRACTED);
        // new ReGrabBall().start();
        super.cancel();
    }

    private class ReGrabBall extends GrabBall{

        private double initTime;

        @Override
        protected void initialize() {
            initTime = Timer.getFPGATimestamp();
            super.initialize();
        }

        @Override
        protected boolean isFinished() {
            return super.isFinished() || (Timer.getFPGATimestamp() > 1 + initTime);
        }

        @Override
        protected void end() {
            super.end();
        }
    }

}