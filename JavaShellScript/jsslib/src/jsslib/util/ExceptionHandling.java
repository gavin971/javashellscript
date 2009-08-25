/*
 * Some usefull things to handle exceptions
 */

package jsslib.util;

/**
 *
 * @author robert schuster
 */
public class ExceptionHandling {

    /**
     * Convert the result of Exception.getStackTrace to a String
     * @param trace
     * @return
     */
    public static String StackTraceToString(StackTraceElement[] trace) {
        String result = "";
        for (int i=0;i<trace.length;i++) {
            result += trace[i].toString() + "\n";
        }
        return result;
    }
}
