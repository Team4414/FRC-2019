package frc.robot.commands.actions;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.Finger;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.Intake.IntakeWheelState;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Finger.FingerClapperState;
import frc.robot.subsystems.Hand.HandState;

public class Unjam extends CommandGroup{

    public Unjam(){
        addParallel(DustPan.getInstance().intakeCommand(DustpanIntakeState.UNJAM));
        addParallel(DustPan.getInstance().deployCommand(true));
        addParallel(Intake.getInstance().deployCommand(true));
        addParallel(Intake.getInstance().intakeCommand(IntakeWheelState.UNJAM));
        addParallel(Finger.getInstance().setArmCommand(false));
        addParallel(Finger.getInstance().setFingerCommand(false));
        addParallel(Hand.getInstance().setHandCommand(HandState.DROP));
    }

    @Override
    public void cancel() {
        super.cancel();
        DustPan.getInstance().intake(false);
        DustPan.getInstance().deploy(false);
        Intake.getInstance().deploy(false);
        Intake.getInstance().intake(false);
        Finger.getInstance().setFinger(FingerClapperState.HOLDING);
        Hand.getInstance().set(HandState.OFF);
    }
}