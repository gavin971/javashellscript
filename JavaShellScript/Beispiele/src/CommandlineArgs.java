//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * This examples illustrates the usage of the ArgParser for commandline arguments
 */

import java.util.Properties;
import jsslib.shell.ArgParser;

/**
 *
 * @author robert schuster
 */
public class CommandlineArgs {
    public static void main(String[] args) {
        //if there are no args
        if (args.length == 0) {
            System.out.println();
            System.out.println("This Example illustrates the usage of");
            System.out.println("commandline arguments with the ArgParser");
            System.out.println();
            System.out.println("Some samples to try:");
            System.out.println("CommandlineArgs -name value");
            System.out.println("CommandlineArgs -name {a complex value with spaces inside}");
            System.out.println("CommandlineArgs {a complex unnamed value with spaces inside}");
            System.out.println("CommandlineArgs arg1 arg2 arg3 -name value");
        }

        //print the args:
        for (int i=0;i<args.length;i++)
            System.out.println(i + ".: " + args[i]);

        //parse the arguments to a properties object
        Properties arguments = ArgParser.ArgsToProperties(args);

        //check for errors
        if (arguments == null) {
            System.out.println("Error in the command line arguments! -> Exit!");
            return;
        }

        //List the arguments
        arguments.list(System.out);
    }
}
