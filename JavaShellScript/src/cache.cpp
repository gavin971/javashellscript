/* 
 * File:   cache.cpp
 * Author: robert
 * 
 * Created on 11. August 2009, 15:30
 */

#include <sstream>

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

    //
    //boost::filesystem::
}

cache::~cache() {
    delete verzeichnis;
}

/** 
 * Gibt ein Verzeichnis für die compilierten dateien zurück.
 * Falls das Verzeichnis noch nicht existiert wird es angelegt
 * @param datei Name der Quell-Code-Datei inkl verzeichnis
 */
string cache::getCacheDir(string datei) {
    string ergebnis = getCacheDirComplett(datei);

    //prüfen ob das Verzeichnis schon existiert, falls nicht anlegen
    bs::path cachedir(ergebnis);
    if(!bs::exists(cachedir)) {
        bs::create_directories(cachedir);
    }
    return ergebnis;
}

/**
 * Gibt den nicht vom Dateidatum abhängenden Teil des Cache-verzeichnisses zurück.
 */
string cache::getCacheDirTeil1(string datei) {
    string ergebnis = verzeichnis->string();

    //Vollständigen Pfad beschaffen
    bs::path quelldatei(datei);
    bs::path quelle_mit_dir = bs::complete(quelldatei);

    //Slash entfernen
    string temp = quelle_mit_dir.string();
    string::size_type i = temp.find("./");
    if (i != string::npos) temp.erase(i,2);
    replace(temp.begin(),temp.end(),'/','_');

    ergebnis += temp;
    return ergebnis;
}

/**
 * Gibt den gesamten Namen des Cache-verzeichnisses zurück.
 */
string cache::getCacheDirComplett(string datei) {
    string ergebnis = getCacheDirTeil1(datei);

    //Groesse und alter bestimmen
    bs::path quelldatei(datei);
    int size =  bs::file_size(quelldatei);
    time_t zeit = bs::last_write_time(quelldatei);

    stringstream temp2;
    temp2 << ergebnis;
    temp2 << "__" << size << "__" << zeit;
    ergebnis = temp2.str();

    return ergebnis;
}

/**
 * Prüft ob diese Datei schon im Cache zu finden ist
 */
bool cache::isInCache(string datei) {
    bs::path quelldatei(getCacheDirComplett(datei));
    return bs::exists(quelldatei);
}
