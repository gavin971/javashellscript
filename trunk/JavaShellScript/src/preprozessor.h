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
#include <unistd.h>
#include "cache.h"
#include "config.h"

using namespace std;
namespace bs = boost::filesystem;

class preprozessor {
public:
    /**
     * Zeiger auf das Cache-Objekt.
     * Das Cache-Objekt verwaltet den Cache.
     */
    cache *Cache;

    preprozessor(int argc, char** argv, config* co);
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
     * Zeiger auf die Konfiguration, wird mit dem Konstructor übergeben
     */
    config* conf;
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

