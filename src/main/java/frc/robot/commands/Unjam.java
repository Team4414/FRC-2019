package frc.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.subsystems.DustPan;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.DustPan.DustpanIntakeState;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.Hand;
import frc.robot.subsystems.Hand.HandState;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Intake.IntakeWheelState;
import frc.robot.subsystems.PPintake.PPState;

public class Unjam extends CommandGroup {

    public Unjam() {
        addParallel(DustPan.getInstance().intakeCommand(DustpanIntakeState.UNJAM));
        addParallel(Elevator.getInstance().jogElevatorCommand(Setpoint.FUEL_LOW));
        addParallel(DustPan.getInstance().deployCommand(true));
        addParallel(Intake.getInstance().deployCommand(true));
        addParallel(Intake.getInstance().intakeCommand(IntakeWheelState.UNJAM));
        addParallel(PPintake.getInstance().setArmCommand(false));
        addParallel(PPintake.getInstance().setPPCommand(PPState.UNJAM));
        addParallel(Hand.getInstance().setHandCommand(HandState.DROP));
    }

    @Override
    public void cancel() {
        super.cancel();
        Elevator.getInstance().setPosition(Setpoint.STOW);
        DustPan.getInstance().intake(false);
        DustPan.getInstance().deploy(false);
        Intake.getInstance().deploy(false);
        Intake.getInstance().intake(false);
        PPintake.getInstance().setPP(PPState.OFF);
        Hand.getInstance().set(HandState.OFF);
    }
}