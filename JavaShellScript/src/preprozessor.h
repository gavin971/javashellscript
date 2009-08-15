/* 
 * File:   preprozessor.h
 * Author: robert schuster
 *
 * Der Preprozessor bereitet das Compilieren vor, kopiert alle Dateien und
 * ersetzt Script-Spezifische Befehle dort Java-Code
 *
 * Created on 12. August 2009, 16:12
 */

#ifndef _PREPROZESSOR_H
#define	_PREPROZESSOR_H

#include <string>
#include <iostream>
#include <fstream>
#include <boost/filesystem.hpp>
#include <CoreFoundation/CoreFoundation.h>
#include <unistd.h>
#include "cache.h"

using namespace std;
namespace bs = boost::filesystem;

class preprozessor {
public:
    /**
     * Zeiger auf das Cache-Objekt.
     * Das Cache-Objekt verwaltet den Cache.
     */
    cache *Cache;

    preprozessor(int argc, char** argv);
    virtual ~preprozessor();
    vector<bs::path> *getDateiListe();
    /**
     * Siehe FehlerStatus;
     */
    int getFehlerStatus();
    /**
     * Enthält die Quelldatei Zeilenweise
     */
    vector<string> *Quelldateizeilen;
    void prozess();
    string getMainClass(char* argument);
    /**
     * Optionen für den Start von java
     * hier sollte der Inhalt der zweiten Zeile rein
     */
    string jvm_optionen;
    string classpath;
    /**
     * Im Cache befindlichen Datei Ausführen
     */
    int Ausfuehren();
private:
    /**
     * die Argumente der main-Funktion
     */
    int AnzahlDerArgumente;
    char** Argumente;
    /**
     * Diese Liste enthält alle beteiligten Dateien, nach einem aufruf von
     * getDateiListe();
     */
    vector<bs::path> *DateiListe;
    /** 
     * 0 wenn alles ok ist, sonst eine Zahl größer null.
     */
    int FehlerStatus;
    /** 
     * Prüft ob eine Datei wirklich existiert. Wenn nicht wird die Fehler-Variable 
     * gesetzt.
     */
    void DateiCheck(bs::path datei);

    /**
     * Liest die Quelldatei vollständig ein.
     */
    void QuelleEinlesen();
    /** 
     * Compiliert die Dateien im Temp-dir
     */
    int Compilieren();

    void addLibToClasspath();
    /**
     * Gibt das Verzeichnis der jss-Datei zurück, muss für jedes Betriebssystem
     * extra implementiert werden
     */
    string GetPathName();
};

#endif	/* _PREPROZESSOR_H */

