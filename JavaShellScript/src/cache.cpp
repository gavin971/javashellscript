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
 * File:   cache.cpp
 * Author: robert
 * 
 * Created on 11. August 2009, 15:30
 */

#include <sstream>
#include <boost/filesystem/operations.hpp>

#include "cache.h"

namespace bs = boost::filesystem;

/**
 * Das Cache Objekt-Erzeugen.
 * Der Cache enthält compilierte Dateien, die solange wieder verwendet
 * werden können, bis sich die Quell-datei geändert hat, oder sie zu alt ist.
 */
cache::cache() {
    //zuerst prüfen, ob es für den aktuellen benutzer schon ein cache-verzeichnis gibt
    string dir = string(getenv("HOME")) + string("/.jss/cache/");
    verzeichnis = new bs::path(dir);

    //verzeichnis anlegen, falls es nicht schon existiert
    if (!bs::exists(*verzeichnis)) {
        bs::create_directories(*verzeichnis);
    }

    CacheDirTeil1 = "";
    CacheDirComplett = "";
}

cache::~cache() {
    delete verzeichnis;
}

/** 
 * Gibt ein Verzeichnis für die compilierten dateien zurück.
 * Falls das Verzeichnis noch nicht existiert wird es angelegt
 * @param datei Name der Quell-Code-Datei inkl verzeichnis
 */
string cache::getCacheDir(vector<bs::path> *dateiliste) {
    string ergebnis = getCacheDirComplett(dateiliste);

    //prüfen ob das Verzeichnis schon existiert, falls nicht anlegen
    bs::path cachedir(ergebnis);
    if(!bs::exists(cachedir)) {
        //falls noch eine alte Version im Cache liegt, dann kann diese
        //jetzt gelöscht werden
        vector<bs::path> *cacheinhalt = getEintraegeInVerzeichnis(*verzeichnis);
        for (int i=0;i<cacheinhalt->size();i++) {
            bs::path eintrag = *verzeichnis;
            eintrag /= cacheinhalt->at(i);
            //prüfen ob CacheDirTeil1 im eintrag vorkommt. Falls ja löschen!
            if (eintrag.string().find(CacheDirTeil1,0) != string::npos) {
                bs::remove_all(eintrag);
            }
        }
        delete cacheinhalt;

        //Verzeichnis erstellen
        bs::create_directories(cachedir);
    }
    return ergebnis;
}

/**
 * Gibt den nicht vom Dateidatum abhängenden Teil des Cache-verzeichnisses zurück.
 */
string cache::getCacheDirTeil1(vector<bs::path> *dateiliste) {
    if (!CacheDirTeil1.empty())
        //Falls schon mal bestimmt altes Ergebnis zurück geben
        return CacheDirTeil1;

    string ergebnis = verzeichnis->string();

    //Vollständigen Pfad beschaffen
    bs::path quelle_mit_dir = bs::complete(dateiliste->at(0));

    //Slash entfernen
    string temp = quelle_mit_dir.string();
    string::size_type i = temp.find("./");
    if (i != string::npos) temp.erase(i,2);
    replace(temp.begin(),temp.end(),'/','_');

    ergebnis += temp;

    CacheDirTeil1 = ergebnis;
    return ergebnis;
}

/**
 * Gibt den gesamten Namen des Cache-verzeichnisses zurück.
 */
string cache::getCacheDirComplett(vector<bs::path> *dateiliste) {
    if (!CacheDirComplett.empty())
        //Falls schon mal bestimmt altes Ergebnis zurück geben
        return CacheDirComplett;

    string ergebnis = getCacheDirTeil1(dateiliste);

    //Groesse und alter der dateien bestimmen und aufsummieren
    int size = 0;
    double zeit = 0;
    
    for (int i=0;i<dateiliste->size();i++) {
        size +=  bs::file_size(dateiliste->at(i));
        zeit += bs::last_write_time(dateiliste->at(i));
    }

    stringstream temp2;
    temp2.setf(ios_base::fixed, ios_base::floatfield);
    temp2.precision(0);
    temp2 << ergebnis;
    temp2 << "__" << size << "__" << zeit;
    ergebnis = temp2.str();

    CacheDirComplett = ergebnis;
    return ergebnis;
}

/**
 * Prüft ob diese Datei schon im Cache zu finden ist
 */
bool cache::isInCache(vector<bs::path> *dateiliste) {
    bs::path quelldatei(getCacheDirComplett(dateiliste));
    return bs::exists(quelldatei);
}

/**
 * wie ls
 * Gibt eine Liste mit allen Einträgen in dem Übergebenen Verzeichnis zurück
 * @param verzeichnis Das zu durchsuchende Verzeichnis
 */
vector<bs::path> *cache::getEintraegeInVerzeichnis(bs::path &verzeichnisname) {
    string verzeichnis_str = verzeichnisname.string();

    //Verzeichnis öffnen
    DIR *verzeichnis_dir = opendir(verzeichnis_str.c_str());
    if (verzeichnis_dir == NULL) {
        cout << "FEHLER: Cache kann nicht durchsucht werden!" << endl;
        return NULL;
    }
    //vector für die Einträge
    vector<bs::path> *ergebnis = new vector<bs::path>();

    //eintrag für die Benutzung mit readdir
    dirent* eintrag;
    while ((eintrag = readdir(verzeichnis_dir)) != NULL) {
        ergebnis->push_back(bs::path(string(eintrag->d_name)));
    }

    //Verzeichnis wieder schließen
    closedir(verzeichnis_dir);

    return ergebnis;
}

/**
 * Löscht Cache-Einträe, die älter als AlterInSek sind.
 */
void cache::AlteEintrageLoeschen(long AlterInSek) {
    //Inhalt beschaffen
    vector<bs::path> *cacheinhalt = getEintraegeInVerzeichnis(*verzeichnis);

    //Aktuelle Systemzeit abfrage
    time_t AktuelleZeit;
    time(&AktuelleZeit);

    //alle einträge prüfen, bei zwei starten, damit . und .. nicht berührt werden
    for (int i=2;i<cacheinhalt->size();i++) {
        bs::path eintrag = *verzeichnis;
        eintrag /= cacheinhalt->at(i);
        if ((AktuelleZeit - bs::last_write_time(eintrag)) > AlterInSek) {
            bs::remove_all(eintrag);
        }
    }
}

/**
 * Löscht den Aktuellen Eintrag, nachdem zum Beispiel beim Compilieren
 * Was schief gegangen ist
 */
void cache::AktuellenEintragLoeschen() {
    if (!CacheDirComplett.empty()) {
        bs::remove_all(bs::path(CacheDirComplett));
    }
}
