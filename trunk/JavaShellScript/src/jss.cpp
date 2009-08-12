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
#include "preprozessor.h"

using namespace std;
namespace bs = boost::filesystem;


/**
 * Zeiger auf den Preprozessor
 */
preprozessor *Pp;



/**
 * Java-dateien compilieren.
 * Compiliert alle dateien im Temp-Dir.
 * @param tempdir
 */
int Compilieren(string tempdir) {
    string komando = "javac " + tempdir + "*.java";
    return system(komando.c_str());
    
}

/** 
 * Führt das Compilierte Programm aus.
 * das compilierte Programm wird mit den übergebenden Parametern ausgeführt.
 * @param a an integer argument. 
 * @param s a constant character pointer. 
 */
int Ausfuehren(string tempdir, int argc, char** argv) {

    string komando = "java " + Pp->jvm_optionen + " -cp " + tempdir;

    //der name der Main-Klasse ist der Dateiname im Argument 1
    string mainklasse = Pp->getMainClass(argv[1]);

    //Mainklasse anhängen
    komando += " " + mainklasse;

    //Argumente anhängen
    for (int i=2;i<argc;i++) {
        komando += " " + string(argv[i]);
    }

    //Ausführen
    return system(komando.c_str());
}

/**
 * Löscht die Compilierten Dateien.
 * Langtext.
 * @param a an integer argument.
 * @param s a constant character pointer.
 */
void TempLoeschen(string tempdir) {
    string komando = "rm -rf " + tempdir;
    system(komando.c_str());
}

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

    //Preprozessor initialisieren
    Pp = new preprozessor(argc, argv);

    //Falls es probleme gab, raus hier!
    if (Pp->getFehlerStatus() != 0) {
        return (EXIT_FAILURE);
    }
    
    //Prüfen, ob die datei schon im Cache vorhanden ist
    string tempdir;
    if (!Pp->Cache->isInCache(Pp->getDateiListe())) {
        //im Argument 1 steht die java-Datei, diese muss um die erste Zeile
        //erleichtert werden
        Pp->prozess();
        bs::path d = Pp->getDateiListe()->at(0);
        tempdir = Pp->Cache->getCacheDir(d.string()) + "/";
        //die java-datei im tempdir compilieren
        if (Compilieren(tempdir) != 0) {
            return (EXIT_FAILURE);
        }
    } else {
        bs::path d = Pp->getDateiListe()->at(0);
        tempdir = Pp->Cache->getCacheDir(d.string());
    }

    //Ausführen
    int ergebnis = Ausfuehren(tempdir, argc, argv);
    
    //Temporäre Dateien löschen
    //TempLoeschen(tempdir);

    return ergebnis;
}


