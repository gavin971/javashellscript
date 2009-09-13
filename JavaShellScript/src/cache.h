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


    vector<bs::path> *getEintraegeInVerzeichnis(bs::path &verzeichnisname);
private:
    /** 
     * Name des cache-Verzeichnisses des aktuellen Benutzers
     */
    boost::filesystem::path *verzeichnis;

    string getCacheDirTeil1(vector<bs::path> *dateiliste);
    string getCacheDirComplett(vector<bs::path> *dateiliste);
    string CacheDirTeil1;
    string CacheDirComplett;

};

#endif	/* _CACHE_H */

