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
    cout << "--------------------------------------------" << endl;
    cout << "           JavaShellScript 0.1              " << endl;
    cout << "   Copyright (C) Robert Schuster 2009-2011  " << endl;
    cout << "--------------------------------------------" << endl;
    cout << endl;
    cout << "Make Java-Files executable as shell script." << endl;
    cout << "Requirement: installed JDK" << endl;
    cout << endl;
    cout << "Exsample with Main-Class (sample.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "public class sample {" << endl;
    cout << "   public static void main(String[] args) {" << endl;
    cout << "       System.out.println(\"Hello World!\");" << endl;
    cout << "   }" << endl;
    cout << "}" << endl;
    cout << endl;
    cout << "Sample without Main-Class (sample.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "System.out.println(\"Hello World!\");" << endl;
    cout << endl;
    cout << "Execute:" << endl;
    cout << "chmod +x sample.java" << endl;
    cout << "./sample.java" << endl;
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


