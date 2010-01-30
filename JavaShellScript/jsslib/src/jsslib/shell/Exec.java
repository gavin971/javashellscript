package jsslib.shell;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Run system-commands
 * @author robert schuster
 */
public class Exec {

    /**
     * Hashtable with all Variables and values
     */
    private static Hashtable<String,String> env_hash = new Hashtable<String, String>();

    /**
     * Add a variable to the environment
     * @param var
     * @param value
     */
    public static void setEnv(String var, String value) {
        env_hash.put(var, value);
    }

    /**
     * @return the Environment-Variables as a String[] for the use in exec-calls
     */
    public static String[] getEnvStrArr() {
        Enumeration<String> vars = env_hash.keys();
        ArrayList<String> all = new ArrayList<String>();
        while (vars.hasMoreElements()) {
            String key = vars.nextElement();
            all.add(key + "=" + env_hash.get(key));
        }
        Object[] all_obj = all.toArray();
        if (all_obj.length == 0) return null;
        String[] result = new String[all_obj.length];
        for (int i=0;i<result.length;i++) result[i] = (String)all_obj[i];
        return result;
    }

    /**
     * run a command in a specified directory, discard the output
     * @param command
     * @param dir
     * @throws IOException
     */
    public static void runAndWait(String command, String dir) throws IOException {
        try {
            Runtime.getRuntime().exec(command, getEnvStrArr(), new File(dir)).waitFor();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * run a command in a specified directory and return the output as a string
     * @param command
     * @param dir
     * @return
     * @throws IOException
     */
    public static String runToString(String command, String dir) throws IOException {
        String output = "";
        char[] puffer = new char[1000];
        //create a new Prozess
        Process prozess = Runtime.getRuntime().exec(command, getEnvStrArr(), new File(dir));

        //die Ausgabe des Prozesses abfangen
        BufferedReader ausgabe =
                new BufferedReader(new InputStreamReader(prozess.getInputStream()));
        BufferedReader error =
                new BufferedReader(new InputStreamReader(prozess.getErrorStream()));

        //Endlosschleife, bis der Prozess beendet ist
        while (true) {

            //Ausgabe übernehmen
            while (ausgabe.ready()) {
                int anzahl = ausgabe.read(puffer, 0, 1000);
                for (int x=0;x<anzahl;x++)
                    output += puffer[x];
            }

            //fehler übernehmen
            while (error.ready()) {
                int anzahl = error.read(puffer, 0, 1000);
                for (int x=0;x<anzahl;x++)
                    output += puffer[x];
            }

            //Gibt es schon einen Rückgabe-wert? wenn ja, dann raus hier
            try {
                int wert = prozess.exitValue();
                break;
            //wenn nein, wird diese Exception ausgelöst, und es kann wieder am stream
            //gelesen werden
            } catch (IllegalThreadStateException ex) { }


            //10 ms warten bis es von vorn los geht
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) { }

        }

        return output;
    }

    /**
     * run a command in a specified directory and return the output as a string
     * @param command
     * @param dir           the path to run the command in
     * @param outputfile    name of the output-file inclusive path
     * @throws IOException
     */
    public static void runToFile(String command, String dir, String outputfile) throws IOException {
        byte[] puffer = new byte[10000];
        //create a new Prozess
        Process prozess = Runtime.getRuntime().exec(command, getEnvStrArr(), new File(dir));

        //in datei umleiten
        BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(outputfile));
        int anzahl = -1;

        BufferedInputStream input = new BufferedInputStream(prozess.getInputStream());
        BufferedInputStream error = new BufferedInputStream(prozess.getErrorStream());

        //Endlosschleife, bis der Prozess beendet ist
        while (true) {

            //Ausgabe übernehmen
            while (input.available() > 0) {
                output.write(input.read());
            }

            //fehler übernehmen
            while (error.available() > 0) {
                output.write(error.read());
            }

            //Gibt es schon einen Rückgabe-wert? wenn ja, dann raus hier
            try {
                int wert = prozess.exitValue();
                break;
            //wenn nein, wird diese Exception ausgelöst, und es kann wieder am stream
            //gelesen werden
            } catch (IllegalThreadStateException ex) { }


            //10 ms warten bis es von vorn los geht
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) { }

        }

        output.flush();
        output.close();
    }
}
