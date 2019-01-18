package frc.util.logging;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Log Class.
 *
 * <P>Represents a log of the changes to a certain set of provided objects over time</P>
 *
 * @author Avidh Bavkar (Team 7404: HighTide) [avidhbavkar@gmail.com]
 * @version 2.0
 * @since   1.0
 */
public class Log {

    /**
     * The raw contents of the log.
     */
    private LinkedHashMap<String, ArrayList<Object>> contents;


    /**
     * Default Constructor.
     */
    public Log(){
        clear();
    }

    /**
     * Copy Constructor.
     *
     * <P> Initializes a log as a copy of a provided log.</P>
     *
     * @param copyFrom the log object to create a copy of.
     */
    public Log(Log copyFrom){
        contents = copyFrom.getRaw();
    }

    /**
     * Append Method.
     *
     * <P> Appends an individual value to the log. </P>
     */
    void append(String key, Object object){

        for (String entry: contents.keySet())
            if (entry.equals(key))
                key = entry;

        if (!contents.containsKey(key))
            contents.put(key, new ArrayList<>());
        contents.get(key).add(object);
    }

    /**
     * As Raw Method.
     *
     * <P> Returns a raw copy of the contents of the log, useful for creating copies but perhaps not much else</P>
     *
     * @return A copy of the contents of the log in it's raw hashmap form.
     */
    @SuppressWarnings("unchecked")
    private LinkedHashMap<String, ArrayList<Object>> getRaw(){
        return (LinkedHashMap<String, ArrayList<Object>>) contents.clone();
    }


    /**
     * As Array List Method.
     *
     * @return The contents of this log file in the form of a 2 dimensional {@link ArrayList}
     */
    public ArrayList<ArrayList<Object>> asArrayList(){
        ArrayList<ArrayList<Object>> returnMe = new ArrayList<>();

        for (Map.Entry<String, ArrayList<Object>> entry: contents.entrySet())
            returnMe.add(entry.getValue());

        return returnMe;
    }

    /**
     * As Array Method.
     *
     * @return The contents of this log file in the form of a 2 dimensional {@link Object} array
     */
    public Object[][] asArray(){
        ArrayList<ArrayList<Object>> arrayList = this.asArrayList();
        Object[][] returnMe = new Object[arrayList.size()][arrayList.get(0).size()];

        for (int i = 0; i < returnMe.length; i++)
            for (int j = 0; j < returnMe[0].length; j++)
                returnMe[i][j] = arrayList.get(i).get(j);

        return returnMe;
    }

    /**
     * Get Keys Method.
     *
     * @return The String keys to this Log, which represent the labels to the different fields of the log.
     */
    public String[] getKeys(){
        Object[] obj = contents.keySet().toArray();
        String[] returnMe = new String[obj.length];
        for (int i = 0; i < obj.length; i++)
            returnMe[i] = obj[i].toString();
        return returnMe;
    }

    public void clear(){
        contents = new LinkedHashMap<>();
    }
}
