package frc.robot.commands.panels;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.commands.elevator.SafeElevatorMove;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.DustPan.DustpanBoomState;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.PPintake.PPState;

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
                PPintake.getInstance().setPP(PPState.HOLDING);;
                return true;
            }
        });

        addSequential(new SafeElevatorMove(Setpoint.FINGER_CLR));
        addSequential(new SafeElevatorMove(Setpoint.STOW));
    }
}