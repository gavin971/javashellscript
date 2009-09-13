//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8

/**
 * Copyright (c) 2009, Robert Schuster
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the
 * following conditions are met:
 *
 * - Redistributions of source code must retain the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer.
 * - Redistributions in binary form must reproduce the above
 *   copyright notice, this list of conditions and the
 *   following disclaimer in the documentation and/or other
 *   materials provided with the distribution.
 * - Neither the name of Robert Schuster nor the names of his
 *   contributors may be used to endorse or promote products
 *   derived from this software without specific prior written
 *   permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND
 * CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
            return;
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
