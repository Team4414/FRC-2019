package frc.util.logging;
/**
 * Loggable Class.
 *
 * <P>Provides a framework for extending classes to log values to a {@link Log} object </P>
 */
public abstract class Loggable {

    public String kFilePath;

    public class LogObject{
        public String key;
        public Object data;

        public LogObject(String key, Object data){
            this.key = key;
            this.data = data;
        }
    }

    /**
     * The {@link Log} object that stores all logged values for this class.
     */
    private Log log;

    /**
     * Constructor
     *
     * @param keys The labels to the data in the order that they will be passed in on "collectData()".
     */
    public Loggable(String logFilePath){
        log = new Log();
        kFilePath = logFilePath;
    }

    /**
     * Constructor
     */
    public Loggable(){
        this(null);
    }


    /**
     * Collect Data Abstract Method.
     *
     * <P> Must return a "snapshot" of values to be logged</P>
     *
     * @return an array of {@link LogObject} that represent a "snapshot" of the values to be logged
     */
    protected abstract LogObject[] collectData();

    /**
     * Log Method.
     *
     * <P> Must be called at a periodic interval to capture and write a "snapshot" to the log </P>
     */
    public void log(){
        LogObject[] logMe = collectData();

        for (LogObject logObj: logMe){
            log.append(logObj.key, logObj.data);
        }
    }


    /**
     * Get Method.
     *
     * <P> Returns a copy of the log at the time it is called, perhaps useful for CSV publishing</P>
     * @return a copy of the Log object.
     */
    public Log get(){
        return new Log(log);
    }

    public void clearLog(){
        log.clear();
    }
}
