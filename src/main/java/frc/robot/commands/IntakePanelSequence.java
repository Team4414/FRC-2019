package frc.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.commands.actions.GrabPanel;
import frc.robot.commands.actions.IntakePanel;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.Finger.FingerClapperState;

public class IntakePanelSequence extends CommandGroup{

    private boolean mHasHatch;

    public IntakePanelSequence(){
        addSequential(new Superstructure(Superstructure.intakePanel));
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                Finger.getInstance().setFinger(FingerClapperState.OPEN);
                return true;
            }
        });
        // addSequential(new WaitCommand(1));
        // addSequential(new IntakePanel());
        // addSequential(new WaitCommand(0.5));
        // addSequential(new Command(){
        
        //     @Override
        //     protected boolean isFinished() {
        //         mHasHatch = true;
        //         return true;
        //     }
        // });
        // addSequential(new GrabPanel());
        // addSequential(new Superstructure(Superstructure.lowStow));
    }

    @Override
    protected void end() {
        // new GrabPanel().start();
        // // if (!mHasHatch){
        // //     new Superstructure(Superstructure.lowStow).start();
        // // }
    }

    @Override
    protected void interrupted() {
        this.end();
    }

}