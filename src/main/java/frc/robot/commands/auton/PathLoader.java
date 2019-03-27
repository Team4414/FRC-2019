package frc.robot.commands.auton;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

public class PathLoader{

    private static String kPathLocation = "/home/lvuser/deploy/output/";
    private static String kPathSuffix = ".pf1.csv";

    public static LinkedHashMap<String, Trajectory> loadPaths(){
        LinkedHashMap<String, Trajectory> paths = new LinkedHashMap<String, Trajectory>();

        paths.put("HabToRocket_L", getTraj("L1toFR_L"));
        paths.put("RocketToTurn_L", getTraj("FRtoTurn_L"));
        paths.put("TurntoFeeder_L", getTraj("TurntoFeeder_L"));

        return paths;
    }

    private static Trajectory getTraj(String name){
        try{
            return Pathfinder.readFromCSV(
                new File(
                    kPathLocation + name + kPathSuffix
                )
            );
        }catch(IOException e){
            System.out.println("!!!!!!!!!! IO Exception on Reading Traj !!!!!!!!!!");
            return null;
        }
    }
}