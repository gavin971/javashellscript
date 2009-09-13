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
 * File:   config.cpp
 * Author: robert
 * 
 * Created on 17. August 2009, 16:40
 */

#include "config.h"

/** 
 * Hier wird die Konfigurationsdatei eingelesen und das 
 * Programm-Verzeichnis bestimmt
 */
config::config() {
    //Das Programmverzeichnis bestimmen
    string exelink = GetExeName();
    string exequelle = GetLinkQuelle(exelink);
    jsspath = bs::path(exequelle).remove_filename();

    //Standard-Werte
    javaexe = "java";
    javacexe = "javac";

    //Die Konfigurationsdatei einlesen, falls vorhanden
    bs::path conf_datei = bs::path(jsspath) /= bs::path("/jss.conf");
    if (bs::exists(conf_datei)) {
        ConfDateiEinlesen(conf_datei);
    }
}


config::~config() {
}

/**
 * Gibt das Verzeichnis der jss-Datei zurück, muss für jedes Betriebssystem
 * extra implementiert werden.
 * @return kann ein Link sein, muss also noch verfolgt werden
 */
#ifdef MACOS
string config::GetExeName() {
    char path[1024];
    CFBundleRef mainBundle = CFBundleGetMainBundle();
    if(!mainBundle)
	    return "";

    CFURLRef mainBundleURL = CFBundleCopyBundleURL(mainBundle);
	if(!mainBundleURL)
		return "";

    CFStringRef cfStringRef = CFURLCopyFileSystemPath(mainBundleURL, kCFURLPOSIXPathStyle);
	if(!cfStringRef)
		return "";

    CFStringGetCString(cfStringRef, path, 1024, kCFStringEncodingASCII);

    CFRelease(mainBundleURL);
    CFRelease(cfStringRef);

    return std::string(path)+"/jss";
}
#endif
#ifdef LINUX
string config::GetExeName() {
    string procexe = string("/proc/self/exe");
    char buf[1024];
    ssize_t len;
    if ((len = readlink(procexe.c_str(), buf, sizeof(buf)-1)) != -1)
        buf[len] = '\0';
    if (len == -1) return "";
    bs::path exename = bs::path(string(buf));
    return exename.directory_string();
}
#endif

string config::GetLinkQuelle(string link) {
    //prüfen ob jssdir ein link ist, ja ja folgen bis zur eigentlichen datei
    bs::path linkpath(link);
    while (bs::is_symlink(linkpath)) {
        char buf[1024];
        ssize_t len;
        if ((len = readlink(linkpath.directory_string().c_str(), buf, sizeof(buf)-1)) != -1)
            buf[len] = '\0';
        if (len == -1) return string("");
        string neuedatei(buf);
        linkpath = bs::path(neuedatei);
    }
    return linkpath.directory_string();
}

/**
 * Gibt das Programmverzeichnis als boost-Path zurück
 */
bs::path config::getJssPath() {
    return jsspath;
}

/** 
 * Liest die Configuration ein und wertet sie dabei auch aus
 */
void config::ConfDateiEinlesen(bs::path datei) {
    ifstream quelle;
    //Quelle öffnen
    quelle.open(datei.directory_string().c_str());

    //prüfen, ob das öffnen geklappt hat
    if (!quelle.is_open()) {
        cerr << "FEHLER: " << datei.directory_string() << " konnte nicht geöffnet werden!" << endl;
        return;
    }

    //quell-datei vollständig einlesen
    while (!quelle.eof()) {
        string einezeile;
        getline(quelle, einezeile);
        int index;
        index = einezeile.find("java=",0);
        if (index == 0) {
            javaexe = einezeile.substr(5,string::npos);
        }
        index = einezeile.find("javac=",0);
        if (index == 0) {
            javacexe = einezeile.substr(6,string::npos);
        }
    }
    //quell-datei schließen
    quelle.close();

}

string config::getJavaExe() {
    return javaexe;
}

string config::getJavacExe() {
    return javacexe;
}

