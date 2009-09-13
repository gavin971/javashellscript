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
 * This sample converts e text file into a object file, which can be embeded 
 * into a binary
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.Properties;
import jsslib.shell.ArgParser;

/**
 *
 * @author Robert Schuster
 */
public class txt2obj {
    public static void main(String[] args) {
        
        //parse the arguments to a properties object
        Properties arguments = ArgParser.ArgsToProperties(args);

        //the first unnamed argument should be the source file
        String sourcename = arguments.getProperty("unnamed0");

        //the secound unnamed argument is the destination file
        String destinationname = arguments.getProperty("o");

        //no source file?
        if (sourcename == null || destinationname == null) {
            show_discription();
            return;
        }

        //create fileobjects
        File source = new File(sourcename);

        //the sourcefile must exsist
        if (!source.canRead()) {
            System.out.println("\nError: can't read " + sourcename);
            show_discription();
            return;
        }

        //is there a namespace specified?
        String namespace = arguments.getProperty("namespace");

        try {
            //Create a tempfile
            File tempfile = File.createTempFile("txt2ojb_", ".cpp");

            //open the sourcefile
            BufferedReader sourcereader = new BufferedReader(new FileReader(source));

            //Buffer for one line of the source file
            String sourceline;

            //open the tempfile
            PrintWriter tempfilewriter = new PrintWriter(tempfile);

            //the name of the namespace 
            if (namespace != null)
                tempfilewriter.println(getNameSpaceStart(namespace));

            //the name of the export symbol is the filename without the suffix
            tempfilewriter.print(getSymbolName(source) + " = \"");

            //loop over all lines in the source file
            while ((sourceline = sourcereader.readLine()) != null) {
                tempfilewriter.println(sourceline + "\\");
            }

            //finish the text
            tempfilewriter.println("\";");

            //close the namespace
            if (namespace != null)
                tempfilewriter.println(getNameSpaceEnd(namespace));

            //close the tempfile
            tempfilewriter.close();

            //close the source file
            sourcereader.close();

            //compile the tempfile to the destination file using gpp
            String gpp = "g++ -c " + tempfile.getAbsolutePath() + " -o " + destinationname;
            //System.out.println(gpp);
            Process cpp_process = Runtime.getRuntime().exec(gpp);
            cpp_process.waitFor();

            //delete the tempfile
            tempfile.delete();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void show_discription() {
        System.out.println();
        System.out.println("txt2obj converts a textfile to an object file");
        System.out.println("which can be embeded into a binary.");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("txt2obj source.txt -o destination.o");
        System.out.println("txt2obj source.txt -o destination.o -namespace Test");
        System.out.println("txt2obj source.txt -o destination.o -namespace Test::InTest");
        System.out.println();
        System.out.println("To use the text inside the linked binary write:");
        System.out.println("extern const char* source_without_suffix;");
    }

    /**
     * Resturns the name of the symbol in the object file
     * @param file the source file
     * @return const char* filename_without_suffix
     */
    private static String getSymbolName(File file) {
        //get the name of the file without the path
        String filename = file.getName();

        //remove the suffix
        int index = filename.lastIndexOf(".");

        //found a suffix?
        if (index != -1) {
            filename = filename.substring(0, index);
        }

        //return the result
        return "const char* " + filename;
    }

    /**
     * returns namespace Test { namespace InTest {
     * from Test::InTest
     * @param namespace
     * @return
     */
    private static String getNameSpaceStart(String namespace) {
        String[] nss = namespace.split("::");
        String result = "";
        for (int i=0;i<nss.length;i++) {
            result += "namespace " + nss[i] + " {\n";
        }
        return result;
    }

    /**
     * returns } } from Test::InTest
     * @param namespace
     * @return
     */
    private static String getNameSpaceEnd(String namespace) {
        String[] nss = namespace.split("::");
        String result = "";
        for (int i=0;i<nss.length;i++) {
            result += "}\n";
        }
        return result;
    }
}
