/* 
 * File:   jss.cpp
 * Author: robert
 *
 * Created on 8. August 2009, 09:08
 */

#include <stdlib.h>
#include <iostream>
#include <fstream>
#include <vector>
#include <boost/filesystem.hpp>

//der Cache speichert compilierte Dateien bis zu einem bestimmten alter
#include "cache.h"
#include "config.h"
#include "preprozessor.h"

using namespace std;
namespace bs = boost::filesystem;


/**
 * Zeiger auf den Preprozessor
 */
preprozessor *Pp;

/**
 * Zeiger auf die Konfiguration
 */
config *conf;

/**
 * Anleitung ausgeben.
 * Gibt eine Kurze Anleitung auf dem Bildschirm aus.
 */
void Beschreibung() {
    cout << endl;
    cout << "----------------------------------" << endl;
    cout << "   JavaShellScript Interpreter    " << endl;
    cout << "Copyright (C) Robert Schuster 2009" << endl;
    cout << "----------------------------------" << endl;
    cout << endl;
    cout << "Macht Java-Dateien als Shell-Script ausführbar." << endl;
    cout << "Voraussetzung: JDK muss installiert sein!" << endl;
    cout << endl;
    cout << "Beispiel mit Main-Klasse (beispiel.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "public class beispiel {" << endl;
    cout << "   public static void main(String[] args) {" << endl;
    cout << "       System.out.println(\"Hallo Welt!\");" << endl;
    cout << "   }" << endl;
    cout << "}" << endl;
    cout << endl;
    cout << "Beispiel ohne Main-Klasse (beispiel.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "System.out.println(\"Hallo Welt!\");" << endl;
    cout << endl;
    cout << "Ausführen:" << endl;
    cout << "chmod +x beispiel.java" << endl;
    cout << "./beispiel.java" << endl;
}


/*
 * 
 */
int main(int argc, char** argv) {

    //Wenn es keine Argumente gibt, beschreibung und raus hier
    if (argc == 1) {
        Beschreibung();
        return (EXIT_SUCCESS);
    }

    //Config-Objekt erzeugen
    conf = new config();

    //Preprozessor initialisieren
    //Dabei werden die Dateien in den Cache kopiert und Compiliert
    Pp = new preprozessor(argc, argv, conf);

    //Falls es probleme gab, raus hier!
    if (Pp->getFehlerStatus() != 0) {
        //Den Cache-Eintrag löschen, falls schon vorhanden
        Pp->Cache->AktuellenEintragLoeschen();
        //Programm beenden
        return (EXIT_FAILURE);
    }
    
    //Ausführen
    int ergebnis = Pp->Ausfuehren();
    
    //Alte Cache-Einträge löschen (älter als 7 Tage)
    Pp->Cache->AlteEintrageLoeschen(604800);

    return ergebnis;
}


