package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.Robot.Side;
import frc.robot.commands.auton.MoveCommand.FieldSide;
import frc.robot.commands.auton.MoveCommand.VisionCancel;
import frc.robot.commands.auton.MoveCommand.ZeroOdometeryMode;
import frc.robot.commands.elevator.ZeroElevator;
import frc.robot.commands.panels.StationGrab;
import frc.robot.commands.panels.WaitForPanel;
import frc.robot.subsystems.Elevator;
import frc.robot.subsystems.PPintake;
import frc.robot.subsystems.Elevator.Setpoint;
import frc.robot.subsystems.PPintake.PPState;
import frc.robot.vision.AutoDriveIn;
import frc.robot.vision.AutoScoreCommand;
import frc.robot.vision.VisionHelper;
import frc.util.Limelight.LED_STATE;

public class TestAutons extends CommandGroup{
    public TestAutons(){

        // addSequential(new YeetBot(-0.75, 1.25));
        // addSequential(new WaitCommand(0.5));
        // addSequential(new TurnToVision(true));
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.OFF));
        addParallel(new ZeroElevator());
        addParallel(new DelayedLimelightCommand(2.5));
        addParallel(PPintake.getInstance().setPPCommand(PPState.HOLDING));
        addParallel(new DelayedCommand(Elevator.getInstance().jogElevatorCommand(Setpoint.HATCH_MID), 1));
        addSequential(new MoveCommand(Robot.autonPaths.get("HabToRocket_L"), Side.PANEL, VisionCancel.CANCEL_ON_VISION, ZeroOdometeryMode.FIRST_PATH));
        addSequential(new AutoScoreCommand());
        addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
        addSequential(new MoveCommand(Robot.autonPaths.get("RocketToTurn_L"), Side.BALL, VisionCancel.RUN_FULL_PATH, ZeroOdometeryMode.NO_ZERO));
        addParallel(Elevator.getInstance().jogElevatorCommand(Setpoint.STOW));
        addParallel(new DelayedLimelightCommand(1));
        addSequential(new TurnToVision(false));
        // addSequential(new MoveCommand(Robot.autonPaths.get("TurntoFeeder_L"), Side.PANEL, VisionCancel.CANCEL_ON_VISION, ZeroOdometeryMode.NO_ZERO));
        addParallel(new AutoDriveIn.AutoDriveInForPanel());
        addSequential(new StationGrab());
        addParallel(new DelayedCommand( Elevator.getInstance().jogElevatorCommand(Setpoint.HATCH_MID), 2));
        addSequential(new MoveCommand(Robot.autonPaths.get("FeedertoBR"), Side.BALL, VisionCancel.RUN_FULL_PATH, ZeroOdometeryMode.FIRST_PATH));
        addSequential(new DelayedLimelightCommand(0.7));
        addSequential(new TurnToVision(true));
        addSequential(new AutoScoreCommand());
        // addSequential(new MoveCommand(Robot.autonPaths.get("TestPath"), true));
        // addSequential(new MoveCommand(Robot.autonPaths.get("OtherPath"), true));
    }
}