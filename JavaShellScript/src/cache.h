/* 
 * File:   cache.h
 * Author: robert schuster
 *
 * Created on 11. August 2009, 15:30
 */

#ifndef _CACHE_H
#define	_CACHE_H

#include <string>
#include <iostream>
#include <boost/filesystem.hpp>
#include <sys/types.h>
#include <dirent.h>
#include <time.h>

using namespace std;
namespace bs = boost::filesystem;

class cache {
public:
    cache();
    virtual ~cache();
    /** 
     * Gibt ein verzeichnis für die Compilierten dateien zurück
     */
    string getCacheDir(vector<bs::path> *dateiliste);
    /**
     * Gibt true zurück, wenn diese Datei schon im Cache ist
     */
    bool isInCache(vector<bs::path> *dateiliste);
    /**
     * Löscht Cache-Einträe, die älter als AlterInSek sind.
     */
    void AlteEintrageLoeschen(long AlterInSek);
    /**
     * Löscht den Aktuellen Eintrag, nachdem zum Beispiel beim Compilieren 
     * Was schief gegangen ist
     */
    void AktuellenEintragLoeschen();

private:
    /** 
     * Name des cache-Verzeichnisses des aktuellen Benutzers
     */
    boost::filesystem::path *verzeichnis;

    string getCacheDirTeil1(vector<bs::path> *dateiliste);
    string getCacheDirComplett(vector<bs::path> *dateiliste);
    string CacheDirTeil1;
    string CacheDirComplett;

    vector<bs::path> *getEintraegeInVerzeichnis(bs::path &verzeichnisname);

};

#endif	/* _CACHE_H */

