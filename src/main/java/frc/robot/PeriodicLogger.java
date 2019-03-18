package frc.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.Notifier;
import frc.util.logging.CSVLogger;
import frc.util.logging.ILoggable;
import frc.util.logging.Loggable;

public class PeriodicLogger implements Runnable{

    private static final double kTimestep = 0.10;
    private static Notifier mNotifier;

    private static ArrayList<Loggable> allLogs;

    private static PeriodicLogger instance;
    public static PeriodicLogger getInstance(){
        if (instance == null)
            instance = new PeriodicLogger();
        return instance;
    }

    private PeriodicLogger(){
        mNotifier = new Notifier(this);
        allLogs = new ArrayList<Loggable>();
    }

    public void addLoggable(ILoggable loggable){
        allLogs.add(loggable.setupLogger());
    }

    public void clearAll(){
        for (Loggable loggable : allLogs){
            loggable.clearLog();
        }
    }

    public void allToCSV(){
        for (Loggable loggable : allLogs){
            CSVLogger.logCSV(loggable.kFilePath, loggable.get());
        }
    }

    @Override
    public void run() {
        for (Loggable loggable : allLogs){
            loggable.log();
        }
    }

    public void start(){
        mNotifier.startPeriodic(kTimestep);
    }

    public void stop(){
        mNotifier.stop();
    }
}