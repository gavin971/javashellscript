//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * Dieses Beispiel führt die übergebenen Parameter als Shell-Befehle parallel aus
 * und warten bis alle Befehle beendet wurden.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import jsslib.parallel.ForLoop;

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
        final String[] befehle = args;

        //die Parallele For-Schleife der jsslib wird genutzt
        new ForLoop(0, args.length-1, 1, args.length) {
            @Override
            public void Loop(int i) {
                char[] puffer = new char[1000];
                try {
                    //den Befehl als neuen Prozess starten
                    Process prozess = Runtime.getRuntime().exec(befehle[i]);
                    
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
