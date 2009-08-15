//#!/usr/bin/env jss
//#option -Dfile.encoding=UTF8
/*
 * Dies ist ein Beispiel-Script zur Konvertierung einer Excel-Datei
 * in eine Text-datei
 */

import java.io.*;
import jsslib.excel.*;


/**
 *
 * @author Robert Schuster
 */
public class xls2txt {

    /**
     * Main-Routine, startet das Script
     * @param args
     */
    public static void main(String[] args) {
        //um nicht nur statische Methoden nutzen zu können
        //muss ein Objekt vom typ xls2txt erstellt werden
        new xls2txt(args);
    }

    /**
     * Konstructor der Klasse xls2txt
     * der größte Teil des Programmcodes findet sich hier wieder
     * @param args die Komandozeilen-Parameter
     */
    public xls2txt(String[] args) {
        //es müssen mindestens zwei Parameter übergeben werden. Beide
        //müssen den Namen einer Datei enthalten
        if (args.length < 2) {
            Beschreibung_Anzeigen();
            return;
        }

        //Ein Excel-DateiReader erzeugen, dieser befindet sich in der jsslib
        ExcelReader xlsreader = new ExcelReader(args[0]);

        //Prüfen ob es formatangaben gibt
        Object format = new Integer(20);
        for (int i=2;i<args.length;i++) {
            if (args[i].equals("-format")) {
                //Format gefunden, in Nachfolgenden wert schauen
                if (args.length > i+1) {
                    //kann der Parameter in einer Zahl umgewandelt werden?
                    Integer zahl = null;
                    try { zahl = Integer.parseInt(args[i+1]); } catch (NumberFormatException e) {}
                    //hat das geklappt?
                    if (zahl != null) format = zahl;
                    //wenn nicht scheint es ein Trennzeichen zu sein
                    else format = args[i+1];
                    //handelt es sich um den Tabulator?
                    if (args[i+1].equals("tab")) format = "\t";
                } else {
                    Beschreibung_Anzeigen();
                    return;
                }
            }
        }

        //prüfen, ob die Datei ohne Probleme eingelesen werden konnte
        if (xlsreader.getFehlerStatus() != 0) {
            Beschreibung_Anzeigen();
            return;
        }

        //der try-catch-Block fängt fehler beim Schreiben der Zieldatei ab
        try {
            //zieldatei erstellen
            PrintWriter ziel;
            ziel = new PrintWriter(args[1]);

            //Schleife über alle Tabellen
            for (int s=0;s<xlsreader.getNumberOfSheets();s++) {

                //wenn es mehr als eine Tabelle gibt wird der Titel drüber geschrieben
                if (xlsreader.getNumberOfSheets() > 1)
                    ziel.println(xlsreader.getSheetName(s));

                //Schleife über alle Zeilen der Tabelle
                for (int y=0;y<xlsreader.getRowCount(s);y++) {
                    ziel.println(xlsreader.getRow(s, y, format));
                }

                //Zwei Leerzeilen hinter jeder Tabelle
                if (xlsreader.getNumberOfSheets() > 1 && s < xlsreader.getNumberOfSheets()-1) {
                    ziel.println();
                    ziel.println();
                }

            }

            //Zieldatei wieder schließen
            ziel.close();

        //Evtl. aufgetretene Fehler behandeln
        } catch (IOException e) {
            System.out.println(args[1] + " kann nicht erstellt werden.\n");
            Beschreibung_Anzeigen();
        }

    }

    /**
     * zeigt an, wie das Script zu verwenden ist
     */
    private void Beschreibung_Anzeigen() {
        System.out.println("Verwendung des Scripts:");
        System.out.println("./xls2txt.java quelldatei.xls zieldatei.txt [...OPTIONEN]");
        System.out.println();
        System.out.println("Optionen:\n");
        System.out.println("  -format x     Formatierung der Textdatei:");
        System.out.println("                x = Ganze Zahl -> feste Breite der Spalten");
        System.out.println("                x = \"\\;\"       -> Trennzeichen ;");
        System.out.println("                x = ,          -> Trennzeichen ,");
        System.out.println("                x = tab        -> Trennzeichen Tabulatur");
    }


}
