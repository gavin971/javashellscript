//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
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

        //Innerhalb des Loops kann nur auf final deklarierte Variablen zugegriffen werden
        Properties arguments = ArgParser.ArgsToProperties(args);
        if (arguments == null) {
            System.out.println("Error in the command line arguments!");
            BeschreibungAnzeigen();
            return;
        }

        int index = 0;
        String value;
        final String[] commands = new String[arguments.size()];
        while ((value = arguments.getProperty("unnamed"+index)) != null) {
            commands[index] = value;
            index++;

        }

        //die Parallele For-Schleife der jsslib wird genutzt
        new ForLoop(0, index-1, 1, index) {
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
