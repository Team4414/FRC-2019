package frc.util.logging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * CSVLogger Class.
 *
 * <P> Allows for the contents of a provided {@link Log} object to be published to the filesystem as a CSV</P>
 */
public class CSVLogger {

    /**
     * The path to a directory where the logged csv files are to be stored in the filesystem.
     */
    private static final String FILE_PATH = "/media/sda1/";
    /**
     * Log CSV Method.
     *
     * <P> Publishes a {@link Log} object to the filesystem as a CSV file</P>
     *
     * @param fileName The name of output file as a String.
     *                 **Note: Omit the '.csv' extension from this parameter!**
     *                 **Note: Optionally, a relative path can be provided for subdirectories**
     * @param input The {@link Log} object to be published.
     * @return True if operation is successful. False if operation has failed.
     */
    public static boolean logCSV(String fileName, Log input){

        System.out.println("HHHE");

        //Make sure Log is not empty
        try{
            int tVal = input.asArray().length;
        }catch(Exception e){
            // System.out.println("!!!!!!!!!! Attempted to publish empty Log to CSV !!!!!!!!!!");
            return false;
        }
        
        //Grab the row-major data from the Log, flip it to column-major, and store it as an array
        Object[][] data = flipArray(input.asArray());

        //Grab the key array from the Log
        String[] keys = input.getKeys();

        StringBuilder outputString = new StringBuilder();

        //Print out the first row (The ordered keys as a label for the CSV)
        for (int i = 0; i < keys.length; i++){
            outputString.append(keys[i]);
            if (i+2 <= keys.length)
                outputString.append(",");

        }

        //Print out the contents of the Log file
        for (Object[] dataset : data){
            outputString.append("\n");
            for (int i = 0; i < dataset.length; i++){
                outputString.append(dataset[i].toString());
                if (i+2 <= keys.length)
                    outputString.append(",");
            }
        }

        //Write that string to the filesystem
        return writeStringToFile(fileName, outputString.toString());
    }

    /**
     * Log CSV Method.
     * 
     * <P> Publishes an array list of Objects to the filesystem by calling their toString methods </P>
     * 
     * @param fileName The name of output file as a String.
     *                 **Note: Omit the '.csv' extension from this parameter!**
     *                 **Note: Optionally, a relative path can be provided for subdirectories**
     * @param list The {@link ArrayList} to be published. 
     * @return True if operation is successful. False if operation has failed.
     */
    public static boolean logCSV(String fileName, ArrayList<Object> list){
        
        StringBuilder outputString = new StringBuilder();

        for (int i = 0; i < list.size(); i++){
            outputString.append(list.get(i).toString());
        }

        System.out.println(list.size());

        return writeStringToFile(fileName, outputString.toString());
    }


    /**
     * From CSV Method
     * 
     * <P> Returns an array list of Strings that represent the rows of a provided .csv file </P>
     * 
     * @param fileName The name of output file as a String.
     *                 **Note: Omit the '.csv' extension from this parameter!**
     *                 **Note: Optionally, a relative path can be provided for subdirectories**
     * @return An Array List of row content from the provided file.
     */
    public static ArrayList<String> fromCSV(String fileName){

        ArrayList<String> records = new ArrayList<>();
        Scanner scanner;

        try {
            scanner = new Scanner(new File(FILE_PATH + fileName + ".csv"));
        } catch (FileNotFoundException e){
            System.err.println("!!!!! FILE NOT FOUND EXCEPTION FOR \"" + FILE_PATH + fileName + "\" !!!!!");
            System.err.println(e);
            return new ArrayList<>();
        }

        while (scanner.hasNextLine()){
            records.add(scanner.nextLine());
        }

        scanner.close();
        return records;
    }

    /**
     * Flip Array Method.
     *
     * <P> Flips a provided two dimensional array from row-major to column-major or vice versa</P>
     *
     * @param input The two dimensional array to flip
     * @return The flipped array
     */
    private static Object[][] flipArray(Object[][] input){
        Object[][] temp = new Object[input[0].length][input.length];

        for (int i = 0; i < input.length; i++){
            for (int j = 0; j < input[0].length; j++){
                temp[j][i] = input[i][j];
            }
        }

        return temp;
    }

    /**
     * Write String to File Method.
     *
     * <P> Writes a provided String to a .csv file and publishes it to the filesystem</P>
     * @param fileName The name of output file.
     *                 **Note: Omit the '.csv' extension from this parameter!**
     *                 **Note: Optionally, a relative path can be provided for subdirectories**
     * @param contents The contents of the file.
     * @return True if the operation is successful. False if the operation fails.
     */
    private static boolean writeStringToFile(String fileName, String contents){

        PrintWriter fileWriter;

        File tmpFile = new File(FILE_PATH + fileName + ".csv");
        if (tmpFile.exists()){
            tmpFile.delete();
        }

        try {
            fileWriter = new PrintWriter(FILE_PATH + fileName + ".csv");
        } catch (FileNotFoundException e){
            System.err.println("!!!!! FILE NOT FOUND EXCEPTION FOR \"" + FILE_PATH + fileName + "\" !!!!!");
            return false;
        }

        fileWriter.write(contents);
        fileWriter.close();

        return true;
    }

    /**
     * Read String from File method.
     * 
     * <P> Reads a file from the filesystem into a String </P>
     * 
     * @param fileName The name of output file.
     *                 **Note: Omit the '.csv' extension from this parameter!**
     *                 **Note: Optionally, a relative path can be provided for subdirectories**
     * @return The contents of this file as a String.
     */
    private static String readStringfromFile(String fileName){
        String content = "";

        try {
            content = new String(Files.readAllBytes(Paths.get(FILE_PATH + fileName + ".csv")));
        } catch (IOException e){
            System.err.println("!!!!! FILE NOT FOUND EXCEPTION FOR \"" + FILE_PATH + fileName + "\" !!!!!");
        }

        return content;
    }


}
