package jsslib.ncl;

import java.io.IOException;
import jsslib.shell.Exec;

/**
 *
 * @author Robert Schuster
 */
public class nclLauncher {

    /**
     * start ncl with a given ncl script and the given parameters
     * @param nclscript     the script to run
     * @param path          the path where to run the script
     * @param args          the arguments to the script, inside the script available as arg1, arg2 ....
     * @throws IOException
     */
    public static void launch(String nclscript, String path, Object ... args) throws IOException {
        String arguments = "";
        //copy the args to the arguments string
        int index = 1;
        for (Object arg:args) {
            if (arg.getClass().getCanonicalName().equals("java.lang.Float[]")) {
                arguments += "arg"+index+"=(/";
                for (int i=0;i<((Float[])arg).length;i++) {
                    arguments += ((Float[])arg)[i];
                    if (i < ((Float[])arg).length-1) arguments += ",";
                }
                arguments += "/) ";
            }
            index++;
        }
        String output = Exec.runToString("ncl " + nclscript + " " + arguments, path);
        System.out.println(output);
    }
}
