package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Superstructure;
import frc.robot.subsystems.DustPan.DustpanBoomState;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Finger.FingerClapperState;

public class GrabPanel extends CommandGroup{

    public GrabPanel(){
        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                DustPan.getInstance().intake(DustpanIntakeState.HOLD);
                DustPan.getInstance().deploy(DustpanBoomState.RETRACTED);
                return true;
            }
        });

        addSequential(new WaitCommand(1));

        addSequential(new Command(){
        
            @Override
            protected boolean isFinished() {
                Finger.getInstance().setFinger(FingerClapperState.HOLDING);
                return true;
            }
        });

        addSequential(new Superstructure(Elevator.Setpoint.FINGER_CLR));
        addSequential(new Superstructure(Superstructure.lowStow));
    }
}