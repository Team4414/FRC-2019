package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.PPintake.PPState;
import frc.util.Limelight.LED_STATE;

public class IntakePanelSequence extends CommandGroup{

    private boolean mInterrupted;

    public IntakePanelSequence(){
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                mInterrupted = false;
                return true;
            }
        });
        addSequential(DustPan.getInstance().deployCommand(true));
        addSequential(DustPan.getInstance().intakeCommand(DustpanIntakeState.ON));
        addSequential(PPintake.getInstance().setArmCommand(false));
        addSequential(PPintake.getInstance().setPPCommand(PPState.OFF));
        addSequential(new SafeElevatorMove(Setpoint.BOTTOM));
        addSequential(Elevator.getInstance().lockElevatorCommand(true));
        addSequential(new WaitCommand(0.5));
        addSequential(new WaitForPanel());
    }

    
    @Override
    public synchronized void end() {
        if (!mInterrupted){
            new StowSequence().start();
        }
    }

    @Override
    protected void interrupted() {
        DustPan.getInstance().deploy(false);
        DustPan.getInstance().intake(false);
        PPintake.getInstance().setPP(PPState.HOLDING);
        PPintake.getInstance().setArm(false);
        Elevator.getInstance().lockElevator(false);
        Elevator.getInstance().setPosition(Setpoint.STOW);
        mInterrupted = true;
    }

    private class StowSequence extends CommandGroup{

        public StowSequence(){
            addSequential(new Command(){
        
                @Override
                protected boolean isFinished() {
                    Robot.activeSide = Side.PANEL;
                    return true;
                }
            });
    
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.BLINK));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.BLINK));
            addSequential(PPintake.getInstance().setPPCommand(PPState.INTAKE));
            addSequential(DustPan.getInstance().deployCommand(false));
            addSequential(new WaitCommand(1));
            addSequential(PPintake.getInstance().waitForPPCommand());
            addSequential(PPintake.getInstance().setPPCommand(PPState.HOLDING));
            addSequential(Elevator.getInstance().lockElevatorCommand(false));
            addSequential(new SafeElevatorMove(Setpoint.FINGER_CLR));
            addSequential(DustPan.getInstance().intakeCommand(DustpanIntakeState.OFF));
            addSequential(new SafeElevatorMove(Setpoint.STOW));
            addSequential(Robot.limeBall.setLEDCommand(LED_STATE.OFF));
            addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
        }

        

    }

}