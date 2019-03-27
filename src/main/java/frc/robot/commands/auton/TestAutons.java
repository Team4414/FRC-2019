package frc.robot.commands.auton;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;
import frc.robot.Robot;
import frc.robot.commands.panels.StationGrab;
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
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.OFF));
        addParallel(new DelayedLimelightCommand(2.5));
        addParallel(PPintake.getInstance().setPPCommand(PPState.HOLDING));
        addParallel(Elevator.getInstance().jogElevatorCommand(Setpoint.STOW));
        addSequential(new MoveCommand(Robot.autonPaths.get("HabToRocket_L"), true, true, true));
        addSequential(new AutoScoreCommand());
        addSequential(Robot.limePanel.setLEDCommand(LED_STATE.OFF));
        addSequential(new MoveCommand(Robot.autonPaths.get("RocketToTurn_L"), false, false, true));
        addSequential(VisionHelper.getActiveCam().setLEDCommand(LED_STATE.ON));
        addSequential(new MoveCommand(Robot.autonPaths.get("TurntoFeeder_L"), true, true));
        addParallel(new StationGrab());
        addParallel(new AutoDriveIn.AutoDriveInForPanel());
        // addSequential(new MoveCommand(Robot.autonPaths.get("TestPath"), true));
        // addSequential(new MoveCommand(Robot.autonPaths.get("OtherPath"), true));
    }
}