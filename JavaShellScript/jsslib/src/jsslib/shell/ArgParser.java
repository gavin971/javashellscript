package jsslib.shell;

import java.util.Properties;

/**
 * This Class has methods to parse command line arguments
 * @author robert schuster
 */
public class ArgParser {

    /**
     * This methode converts a string-array of comand line arguments into
     * to Properties object.
     *
     * Complex arguments can be framed by {}
     *
     * Arguments should start with a minus:
     *  -argname value
     *  -argname {a more complex value}
     *
     * Arguments without a name must be placed in front of the named ones
     * @param args
     * @return null if an error occourrs
     */
    public static Properties ArgsToProperties(String[] args) {
        Properties ergebnis = new Properties();

        //every unnamed argument gets an index
        int unnamedindex = 0;

        //loop over all words
        for (int i=0;i<args.length;i++) {
            //new named argument? ----------------------------------------------
            if (args[i].startsWith("-")) {
                //get the name of the Argument
                String name = args[i].substring(1);
                //if there is nothing return null
                if (name.length() == 0) return null;

                //find the value of this argument
                //if this is the last argument, then the value is true
                if (args.length-1==i) {
                    ergebnis.setProperty(name, "true");
                    break;
                }

                //if the next arg starts with a minus, then the value is true
                if (args[i+1].startsWith("-")) {
                    ergebnis.setProperty(name, "true");
                    continue;
                }

                //a complex argument
                if (args[i+1].startsWith("{")) {
                    Object[] tib = getTextInBrackets(args, i+1);
                    if (tib == null) return null;
                    else ergebnis.setProperty(name, (String)tib[1]);
                    i = (Integer)tib[0];
                    continue;
                }

                //no special case, just add the next word as value
                ergebnis.setProperty(name, args[i+1]);
                i++;
            } else {
                //a new unnamed argument
                String name = "unnamed" + unnamedindex;
                unnamedindex++;
                
                //a complex argument
                if (args[i].startsWith("{")) {
                    Object[] tib = getTextInBrackets(args, i);
                    if (tib == null) return null;
                    else ergebnis.setProperty(name, (String)tib[1]);
                    i = (Integer)tib[0];
                    continue;
                }

                //no special case, just add the word as value
                ergebnis.setProperty(name, args[i]);
            }
        }
        return ergebnis;
    }

    /**
     * Returns the Text in between brackets: {text} -> text
     * @param args an array of strings with { and }
     * @param start the first index of the array to look in
     * @return the number of indices steped throught and the text between the brackets
     */
    public static Object[] getTextInBrackets(String[] args, int start) {
        String result;
        Object[] resultarray = new Object[2];
        //find }
        //check the same index
        if (args[start].endsWith("}")) {
            if (args[start].length()>2) {
                result = args[start].substring(1, args[start].length()-1);
            } else {
                result = "true";
            }
            resultarray[0] = new Integer(start);
            resultarray[1] = result;
            return resultarray;
        }

        //search in the next indices
        String value = args[start].substring(1) + " ";
        boolean foundbracket = false;
        int j;
        for (j=start+1;j<args.length;j++) {
            if (args[j].endsWith("}")) {
                if (args[j].length() > 1) {
                    value += args[j].substring(0, args[j].length()-1);
                }
                foundbracket = true;
                break;
            }
            value += args[j] + " ";
        }
        //found a bracket?
        if (!foundbracket) {
            System.out.println("ERROR: found { without }");
            return null;
        } else {
            result = value;
        }
        resultarray[0] = new Integer(j);
        resultarray[1] = result;
        return resultarray;
    }
}
