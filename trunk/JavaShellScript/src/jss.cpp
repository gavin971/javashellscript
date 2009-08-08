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

using namespace std;

/**
 * Gibt den Namen der Main-Klasse zurück.
 * @param argument dateiname der java-datei
 */
string getMainClass(char* argument) {
    string mainklasse(argument);
    int index = mainklasse.find_last_of("/",mainklasse.length()-1);
    if (index == string::npos) index = 0;
    int index_punkt = mainklasse.find_last_of(".",mainklasse.length()-1);
    if (index_punkt == string::npos) index_punkt = mainklasse.length()-1;
    mainklasse = mainklasse.substr(index+1,index_punkt-index-1);
    return mainklasse;
}

/**
 * Entfernt das #! aus der ersten Zeile.
 * das #! wird vom Betriebssystem gebraucht, um jss zu starten, javac akzeptiert
 * es aber nicht, also weg damit.
 * @param datei Zeiger auf den Dateinamen
 */
string ErsteZeile(char* datei) {
    //Ein und ausgabe Dateien
    ifstream quelle;
    ofstream ziel;

    //Quelle öffnen
    quelle.open(datei);

    //prüfen, ob das öffnen geklappt hat
    if (!quelle.is_open()) {
        cerr << "FEHLER: " << datei << " konnte nicht geöffnet werden!" << endl;
        return "FEHLER";
    }

    //Ziel öffnen
    string zieldatei = datei;
    int index = zieldatei.find_last_of("/",zieldatei.length()-1);

    //zieldatei = getenv("TMPDIR") + zieldatei.substr(index+1,zieldatei.length()-1);

    string tempdir =  "_jss_XXXXXXXXXX";
    tempdir = getenv("TMPDIR") + tempdir;
    tempdir = mkdtemp((char*)tempdir.c_str());
    tempdir += "/";
    zieldatei = tempdir + zieldatei.substr(index+1,string::npos);
    ziel.open(zieldatei.c_str());
    
   //prüfen, ob das öffnen geklappt hat
    if (!quelle.is_open()) {
        cerr << "FEHLER: " << zieldatei << " konnte nicht geöffnet werden!" << endl;
        return "FEHLER";
    }

    //quell-datei vollständig einlesen
    vector<string> zeilen;    
    while (!quelle.eof()) {
        string einezeile;
        getline(quelle, einezeile);
        zeilen.push_back(einezeile);
    }
    //quell-datei schließen
    quelle.close();

    //erst mal die Datei komplett durchsuchen um festzustellen, ob es eine
    //zeile mit "public class" gibt
    bool hatPublicClass = false;
    for (int i=0;i<zeilen.size();i++) {
        if (zeilen.at(i).find("public",0) != string::npos) {
            if (zeilen.at(i).find("class",0) != string::npos) {
                hatPublicClass = true;
                break;
            }
        }
    }

    //wenn keine Klasse in der Quell-Datei deklariert ist, dann wird das hier
    //erledigt!
    if (!hatPublicClass) {
        //Mainklasse anhand des Dateinamens bestimmen
        string mainklasse = getMainClass(datei);

        //Falls import verwendet wird, muss das hier eingefügt werden
        for (int i=0;i<zeilen.size();i++) {
            if (zeilen.at(i).find("import",0) != string::npos) {
                ziel << zeilen.at(i) << endl;
                zeilen.erase(zeilen.begin()+i);
                i--;
            }
        }

        //Standard-Klassen importieren
        ziel << "import java.util.*; import java.io.*; ";

        //Klasse erzeugen
        ziel << "public class " << mainklasse << "{ ";

        //Mainfunktion
        ziel << "public static void main(String[] args) { ";

        //Konstructor für Klasse (damit auch nicht statische Funktionen aufgerufen
        //werden können
        ziel << "new " + mainklasse + "(); } ";
        ziel << "public " + mainklasse + "() { ";

        //Alles in Try-catch-Block, damit nicht jede anweisung einzeln abgefangen werden muss
        ziel << "try { " << endl;
    }


    //Inhalt der Quelle ins Ziel schreiben
    for (int i=0;i<zeilen.size();i++) {
        if (zeilen.at(i).find("#",0) != 0) {
            ziel << zeilen.at(i) << endl;
        }
    }

    //Klasse und main abschließen
    if (!hatPublicClass) {
        ziel << "} catch (Exception ex) {ex.printStackTrace();} }}" << endl;
    }

    //Dateien wieder schließen
    quelle.close();
    ziel.close();

    return tempdir;
}

/**
 * Java-dateien compilieren.
 * Compiliert alle dateien im Temp-Dir.
 * @param tempdir
 */
int Compilieren(string tempdir) {
    string komando = "javac " + tempdir + "*.java";
    return system(komando.c_str());
    
}

/** 
 * Führt das Compilierte Programm aus.
 * das compilierte Programm wird mit den übergebenden Parametern ausgeführt.
 * @param a an integer argument. 
 * @param s a constant character pointer. 
 */
int Ausfuehren(string tempdir, int argc, char** argv) {

    string komando = "java -cp " + tempdir;

    //der name der Main-Klasse ist der Dateiname im Argument 1
    string mainklasse = getMainClass(argv[1]);

    //Mainklasse anhängen
    komando += " " + mainklasse;

    //Argumente anhängen
    for (int i=2;i<argc;i++) {
        komando += " " + string(argv[i]);
    }

    //Ausführen
    return system(komando.c_str());
}

/**
 * Löscht die Compilierten Dateien.
 * Langtext.
 * @param a an integer argument.
 * @param s a constant character pointer.
 */
void TempLoeschen(string tempdir) {
    string komando = "rm -rf " + tempdir;
    system(komando.c_str());
}

/**
 * Anleitung ausgeben.
 * Gibt eine Kurze Anleitung auf dem Bildschirm aus.
 */
void Beschreibung() {
    cout << endl;
    cout << "----------------------------------" << endl;
    cout << "   JavaShellScript Interpreter    " << endl;
    cout << "Copyright (C) Robert Schuster 2009" << endl;
    cout << "----------------------------------" << endl;
    cout << endl;
    cout << "Macht Java-Dateien als Shell-Script ausführbar." << endl;
    cout << "Voraussetzung: JDK muss installiert sein!" << endl;
    cout << endl;
    cout << "Beispiel mit Main-Klasse (beispiel.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "public class beispiel {" << endl;
    cout << "   public static void main(String[] args) {" << endl;
    cout << "       System.out.println(\"Hallo Welt!\");" << endl;
    cout << "   }" << endl;
    cout << "}" << endl;
    cout << endl;
    cout << "Beispiel ohne Main-Klasse (beispiel.java):" << endl;
    cout << "#!/usr/bin/env jss" << endl;
    cout << "System.out.println(\"Hallo Welt!\");" << endl;
    cout << endl;
    cout << "Ausführen:" << endl;
    cout << "chmod +x beispiel.java" << endl;
    cout << "./beispiel.java" << endl;
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

    //im Argument 1 steht die java-Datei, diese muss um die erste Zeile
    //erleichtert werden
    string tempdir = ErsteZeile(argv[1]);
    if (tempdir.find("FEHLER") != string::npos) {
        return (EXIT_FAILURE);
    }

    //die java-datei im tempdir compilieren
    if (Compilieren(tempdir) != 0) {
        return (EXIT_FAILURE);
    }

    //Ausführen
    int ergebnis = Ausfuehren(tempdir, argc, argv);
    
    //Temporäre Dateien löschen
    TempLoeschen(tempdir);

    return ergebnis;
}


