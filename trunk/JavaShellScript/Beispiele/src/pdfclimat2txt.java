//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * Dieses Beispiel nutzt iText um aus einer pdf-datei tabellen zu extrahieren
 * und in eine Text-datei zu schreiben
 */
import java.io.*;
import java.util.ArrayList;
import jsslib.pdf.TextExtractor;


/**
 *
 * @author Robert Schuster
 */
public class pdfclimat2txt {
    public static void main(String[] args) {

        //Prüfen, ob es zwei Parameter gibt, falls nicht Beschreibung anzeigen
        if (args.length != 2) {
            BeschreibungAnzeigen();
            return;
        }

        //Laufzeit stoppen
        long startzeit = System.currentTimeMillis();

        //io-fehler abfangen
        try {
            //der TextExtractor liest die pdf-datei
            TextExtractor pte = new TextExtractor(args[0]);

            //Datei für Ergebnis öffnen
            //PrintWriter datei = new PrintWriter("climat_test.txt"); geht zwar auch,
            //der Puffer ist aber insbesondere beim langsamen Netzwerk-Laufwerken
            //nützlich
            PrintWriter datei = new PrintWriter(new BufferedWriter(new FileWriter(args[1])));

            //wie viele Seiten hat die PDF-datei?
            int AnzahlDerSeiten = pte.getAnzahlDerSeiten();

            //Schleife über die interessanten Seiten
            for (int seite=1;seite<=AnzahlDerSeiten;seite++) {

                System.out.print("Aktuelle Seite: " + seite);

                //Seiteninhalt als ArrayList holen
                //Jedes Element der Liste ist ein String[] mit den Wörtern der Zeile
                ArrayList<String[]> inhalt = pte.getTextFromPageAsList(seite);

                //prüfen, ob es sich um eine Seite handelt, die in der ersten Zeile
                //nur SURFACE steht. Falls nicht weiter zur nächsten Seite
                if (!inhalt.get(0)[0].trim().equals("SURFACE")) {
                    System.out.println();
                    continue;
                }
                System.out.println("  CLIMATs !");
                
                //Schleife über alle Zeilen
                for (int i = 0;i<inhalt.size();i++) {
                    String[] zeile = inhalt.get(i);

                    //Ist in der Zeile die richtige Anzahl an worten zu finden?
                    if (zeile.length == 17) {
                        //enthalt die zeile auch wirklich inhalt?
                        boolean hatInhalt = false;
                        for (int x=0;x<zeile.length;x++) {
                            if (!zeile[x].trim().isEmpty()) {
                                hatInhalt = true;
                                break;
                            }
                        }

                        //Schleife über alle Wörter, wenn die Zeile inhalt hat
                        if (hatInhalt) {
                            for (int x=0;x<zeile.length;x++) {
                                //Eine feste breite ist gewünscht.
                                //Die länge des textes wird zu diesem Zweck auf
                                //ganze 10 erhöht.
                                int wortlaenge = zeile[x].length();
                                int rest = wortlaenge % 10;
                                wortlaenge = wortlaenge + 10 - rest;
                                //erstes wort linksbündig, alles andere rechtsbündig
                                if (x==0) wortlaenge *= -1;
                                //wort mit der auf ganze 10 aufgerundeten Länge in die zieldatei schrieben
                                datei.print(String.format("%" + wortlaenge + "s", zeile[x].trim()));
                            }
                            //Zeilenumbruch
                            datei.println();
                        }
                    }
                }

            }

            datei.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Laufzeit ausgeben
        System.out.println("Laufzeit in Sekunden: " + ((System.currentTimeMillis()-startzeit)/1000));
    }

    /**
     * Hinweise zur Benutzung anzeigen
     */
    private static void BeschreibungAnzeigen() {
        System.out.println();
        System.out.println("Dieses Beispiel-Script extrahiert den Inhalt einer");
        System.out.println("\"Monthly Climatic Data for the World\"-PDF-Datei");
        System.out.println("von http://www7.ncdc.noaa.gov/IPS/mcdw/mcdw.html");
        System.out.println();
        System.out.println("Anwendung:");
        System.out.println("pdfclimat2txt climat.pdf climat.txt");
    }
}
