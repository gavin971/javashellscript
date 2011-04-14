package jsslib.ncl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
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
     * @return              the output of the ncl-script
     * @throws IOException
     */
    public static String runToString(String nclscript, String path, Object ... args) throws IOException {
        String arguments = "";
        //copy the args to the arguments string
        int index = 1;
        for (Object arg:args) {
            if (arg.getClass().getCanonicalName().contains("[]")) {
                arguments += "'arg"+index+"=(/";
                for (int i=0;i<((Object[])arg).length;i++) {
                    if (((Object[])arg)[i].getClass() == String.class) {
                       arguments += "\""+((Object[])arg)[i] +"\"";
                    } else {
                       arguments += ((Object[])arg)[i];
                    }
                    if (i < ((Object[])arg).length-1) arguments += ",";
                }
                arguments += "/)' ";
            } else {
                arguments += "'arg"+index+"=" + arg + "' ";
            }
            index++;
        }
        //comando in einem Shell-Script ausführen
        File script = File.createTempFile("ncl", "sh");
        PrintWriter pw = new PrintWriter(script);
        pw.println("#!/bin/sh");
        pw.println("ncl " + nclscript + " " + arguments);
        pw.flush();
        pw.close();
        script.setExecutable(true);
        script.deleteOnExit();
        String output = Exec.runToString(script.getAbsolutePath(), path);
        script.delete();
        return output;
    }

    /**
     * Parse the output of a ncl-script and extract a named variable
     *
     * Example output:
     *
     * Variable: test_variable
     * Type: integer
     * Total Size: 8 bytes
     *             2 values
     * Number of Dimensions: 1
     * Dimensions and sizes:        [2]
     * Coordinates:
     * (0)        525
     * (1)        912
     *
     * @param varname
     * @param output
     * @return
     */
    public static Object parseOutput(String varname, String output) {
        // find the variable in the output
        int index = output.indexOf("Variable: " + varname);

        // find the type
        int typeindex = output.indexOf("Type: ", index);
        int newline = output.indexOf("\n", typeindex);
        String typestr = output.substring(typeindex+6, newline);
        Class type = null;
        if (typestr.equals("integer")) {
            type = Integer.class;
        } else if (typestr.equals("float")) {
            type = Float.class;
        }

        // find the dimension
        int noDindex = output.indexOf("Number of Dimensions: ", newline);
        newline = output.indexOf("\n", noDindex);
        int numberOfDimensions = Integer.parseInt(output.substring(noDindex+22, newline));
        int DimsIndex = output.indexOf("Dimensions and sizes:", newline);
        newline = output.indexOf("\n", DimsIndex);
        String DimStr = output.substring(DimsIndex+22, newline);
        String[] DimsStr = DimStr.split("]");
        int[] dims = new int[numberOfDimensions];
        //go throw the each DimsStr from back to the front until a non numeric value is found
        for (int i=0;i<DimsStr.length;i++) {
            int startindex = DimsStr[i].length();
            for (int j=DimsStr[i].length()-1;j>=0;j--) {
                if (!DimsStr[i].substring(j, DimsStr[i].length()).matches("[0-9]*"))  {
                    startindex = j+1;
                    break;
                }
            }
            dims[i] = Integer.parseInt(DimsStr[i].substring(startindex, DimsStr[i].length()));
        }

        //parse the values
        int coorIndex = output.indexOf("Coordinates:", newline);
        //create a new array for the output
        Object result = null;
        switch (numberOfDimensions) {
            case 1:
                result = new Object[dims[0]];
                for (int i0=0;i0<dims[0];i0++) {
                    int bindex = output.indexOf(")", newline);
                    newline = output.indexOf("\n", bindex);
                    String value = output.substring(bindex+1,newline).trim();
                    ((Object[])result)[i0] = parseStringToObject(value,type);
                }
                break;
            case 2:
                result = new Object[dims[0]][dims[1]];
                for (int i0=0;i0<dims[0];i0++) {
                    for (int i1=0;i1<dims[1];i1++) {
                        int bindex = output.indexOf(")", newline);
                        newline = output.indexOf("\n", bindex);
                        String value = output.substring(bindex+1,newline).trim();
                        ((Object[][])result)[i0][i1] = parseStringToObject(value,type);
                    }
                }
                break;
            case 3:
                result = new Object[dims[0]][dims[1]][dims[2]];
                for (int i0=0;i0<dims[0];i0++) {
                    for (int i1=0;i1<dims[1];i1++) {
                        for (int i2=0;i2<dims[2];i2++) {
                            int bindex = output.indexOf(")", newline);
                            newline = output.indexOf("\n", bindex);
                            String value = output.substring(bindex+1,newline).trim();
                            ((Object[][][])result)[i0][i1][i2] = parseStringToObject(value,type);
                        }
                    }
                }
                break;
            case 4:
                result = new Object[dims[0]][dims[1]][dims[2]][dims[3]];
                for (int i0=0;i0<dims[0];i0++) {
                    for (int i1=0;i1<dims[1];i1++) {
                        for (int i2=0;i2<dims[2];i2++) {
                            for (int i3=0;i3<dims[3];i3++) {
                                int bindex = output.indexOf(")", newline);
                                newline = output.indexOf("\n", bindex);
                                String value = output.substring(bindex+1,newline).trim();
                                ((Object[][][][])result)[i0][i1][i2][i3] = parseStringToObject(value,type);
                            }
                        }
                    }
                }
                break;
            default:
                System.out.println("ERROR: nclLauncher.parseOutput: maximum allowed number of dimensions is 4!");
                return null;
        }       
        return result;
    }

    /**
     * Parse the content of the String to an Object of the type ObjectType
     * @param text
     * @param ObjectType
     * @return
     */
    private static Object parseStringToObject(String text, Class ObjectType) {
        if (ObjectType == Integer.class) return Integer.parseInt(text);
        else if (ObjectType == Float.class) return Float.parseFloat(text);
        else if (ObjectType == Double.class) return Double.parseDouble(text);
        return null;
    }
}
