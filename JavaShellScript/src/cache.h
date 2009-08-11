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

using namespace std;

class cache {
public:
    cache();
    virtual ~cache();
    /** 
     * Gibt ein verzeichnis für die Compilierten dateien zurück
     */
    string getCacheDir(string datei);
    /**
     * Gibt true zurück, wenn diese Datei schon im Cache ist
     */
    bool isInCache(string datei);

private:
    /** 
     * Name des cache-Verzeichnisses des aktuellen Benutzers
     */
    boost::filesystem::path *verzeichnis;

    string getCacheDirTeil1(string datei);
    string getCacheDirComplett(string datei);

};

#endif	/* _CACHE_H */

