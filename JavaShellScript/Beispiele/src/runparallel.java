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
 * Dieses Beispiel führt die übergebenen Parameter als Shell-Befehle parallel aus
 * und warten bis alle Befehle beendet wurden.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;
import jsslib.parallel.ForLoop;
import jsslib.shell.ArgParser;

/**
 *
 * @author robert schuster
 */
public class runparallel {

    public static void main(String[] args) {
        //Prüfen, ob es mindestens zwei Parameter gibt
        if (args.length < 2) {
            BeschreibungAnzeigen();
            return;
        }

        Properties arguments = ArgParser.ArgsToProperties(args);
        if (arguments == null) {
            System.out.println("Error in the command line arguments!");
            BeschreibungAnzeigen();
            return;
        }

        //Innerhalb des Loops kann nur auf final deklarierte Variablen zugegriffen werden
        final String[] commands = ArgParser.getUnnamedArguments(arguments);

        //die Parallele For-Schleife der jsslib wird genutzt
        new ForLoop(0, commands.length-1, 1, commands.length) {
            @Override
            public void Loop(int i) {
                char[] puffer = new char[1000];
                try {
                    //den Befehl als neuen Prozess starten
                    Process prozess = Runtime.getRuntime().exec(commands[i]);
                    
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
                                System.out.print(puffer[x]);
                        }

                        //fehler übernehmen
                        while (error.ready()) {
                            int anzahl = error.read(puffer, 0, 1000);
                            for (int x=0;x<anzahl;x++)
                                System.out.print(puffer[x]);
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
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        };

    }

    private static void BeschreibungAnzeigen() {
        System.out.println();
        System.out.println("Dieses Script startet die übergebenen Befehle");
        System.out.println("als parallele Prozesse und wartet bis alle fertig sind.");
        System.out.println();
        System.out.println("Anwendung:");
        System.out.println("./runparallel './befehl_eins mit parametern' './befehl_zwei mit parametern'");
    }
}
