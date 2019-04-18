package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.auton.MoveCommand.FieldSide;
import frc.robot.commands.auton.MoveCommand.VisionCancel;
import frc.robot.commands.auton.MoveCommand.ZeroOdometeryMode;
import frc.robot.commands.elevator.ZeroElevator;
import frc.robot.commands.panels.StationGrab;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.PPintake.PPState;
import frc.robot.vision.AutoDriveIn;
import frc.robot.vision.AutoScoreCommand;
import frc.robot.vision.VisionHelper;
import frc.util.Limelight.LED_STATE;

public class CargoShip extends CommandGroup{
    public CargoShip(FieldSide side){
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.OFF));
        // addSequential(new MoveCommand(Robot.autonPaths.get("TestPath"), Side.BALL, VisionCancel.RUN_FULL_PATH, ZeroOdometeryMode.FIRST_PATH, FieldSide.RIGHT));
        addParallel(new ZeroElevator());
        addParallel(PPintake.getInstance().setPPCommand(PPState.HOLDING));
        addParallel(new DelayedCommand(Elevator.getInstance().jogElevatorCommand(Setpoint.STOW), 1));
        addParallel(new DelayedLimelightCommand(3));
        addSequential(new MoveCommand(Robot.autonPaths.get("HabToBCS"), Side.PANEL, VisionCancel.CANCEL_ON_VISION, ZeroOdometeryMode.FIRST_PATH, side));
        addSequential(new PIDTurn(((side == FieldSide.LEFT) ? -90 : 90), 0), 1);
        addSequential(new AutoScoreCommand());
        addSequential(new MoveCommand(Robot.autonPaths.get("BCSTurnToFeeder"), Side.BALL, VisionCancel.RUN_FULL_PATH, ZeroOdometeryMode.NO_ZERO, side));
        addParallel(Elevator.getInstance().jogElevatorCommand(Setpoint.PANEL_GRAB));
        addSequential(new PIDTurn(180, 0), 1.5);
        addParallel(new AutoDriveIn.AutoDriveInForPanel());
        addSequential(new StationGrab());
        addParallel(new DelayedCommand( Elevator.getInstance().jogElevatorCommand(Setpoint.STOW), 1));
        addSequential(new MoveCommand(Robot.autonPaths.get("BCSTurnToFeeder"), Side.BALL, VisionCancel.RUN_FULL_PATH, ZeroOdometeryMode.NO_ZERO, side));
        addSequential(new PIDTurn(90, 0), 1.5);
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.ON));
        addSequential(new AutoScoreCommand());
    }
}