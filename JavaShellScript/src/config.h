/* 
 * File:   config.h
 * Author: robert
 *
 * Created on 17. August 2009, 16:40
 */

#ifndef _CONFIG_H
#define	_CONFIG_H

#include <string>
#include <iostream>
#include <fstream>
#include <boost/filesystem.hpp>

//Für den Mac
#ifdef MACOS
#include <CoreFoundation.h>
#endif

namespace bs = boost::filesystem;
using namespace std;

class config {
public:
    config();
    virtual ~config();
    bs::path getJssPath();

    string getJavaExe();
    string getJavacExe();

private:
    /**
     * Gibt das Verzeichnis der jss-Datei zurück, muss für jedes Betriebssystem
     * extra implementiert werden
     */
    string GetExeName();

    /**
     * Verfolgt einen Link bis zu seinem Ursprung. Dabei können auch mehrere Links
     * hintereinander folgen.
     */
    string GetLinkQuelle(string link);

    /**
     * Enthält das Programmverzeichnis
     */
    bs::path jsspath;

    void ConfDateiEinlesen(bs::path datei);

    string javaexe;
    string javacexe;

};

#endif	/* _CONFIG_H */

