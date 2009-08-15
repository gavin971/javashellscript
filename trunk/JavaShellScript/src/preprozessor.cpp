/* 
 * File:   preprozessor.cpp
 * Author: robert
 * 
 * Created on 12. August 2009, 16:12
 */

#include <string>


#include "preprozessor.h"

preprozessor::preprozessor(int argc, char** argv) {
    //cache initialisieren
    Cache = new cache();
    //die Dateiliste muss durch getDateiListe innitialisiert werden.
    DateiListe = NULL;
    Quelldateizeilen = NULL;
    //Argumente Übernehmen
    Argumente = argv;
    AnzahlDerArgumente = argc;
    //keine Fehler!
    FehlerStatus = 0;
    //keine Optionen
    jvm_optionen = "";
    classpath = "";

    //die dateien im lib-verzeichnis zum classpath hinzufügen
    addLibToClasspath();

    //Dateiliste erstellen
    getDateiListe();

    //prüfen, ob die Dateien im Cache noch aktuell sind
    if (FehlerStatus != 0) return;
    if (!Cache->isInCache(DateiListe)) {
        //Die Dateien sind nicht im Cache, also neu prozessieren
        prozess();
        if (FehlerStatus != 0) return;
        //jetzt Compilieren
        Compilieren();
    }
}

preprozessor::~preprozessor() {
    if (DateiListe != NULL)
        delete DateiListe;
}

/**
 * Gibt eine Liste alle beteiligter Dateien zurück
 * @param quelldatei ist der Name der Aufrufenden Datei
 */
vector<bs::path> *preprozessor::getDateiListe() {
    if (DateiListe != NULL) return DateiListe;

    DateiListe = new vector<bs::path>();

    //der erste eintrag in der Liste ist die quelldatei selbst
    string quelldatei(Argumente[1]);
    bs::path quelle(quelldatei);
    DateiCheck(quelldatei);
    DateiListe->push_back(quelle);

    //quelle einlesen
    QuelleEinlesen();
    if (FehlerStatus != 0) return NULL;

    //nach datei-Anweisungen suchen
    for (int i=0;i<Quelldateizeilen->size();i++) {
        //bei der ersten Zeile, die nicht mit # anfängt abbrechen
        if (Quelldateizeilen->at(i).find("#",0) != 0) break;

        //wenn eine Zeile mit #file anfängt folgt ein dateiname
        int index;
        if ((index = Quelldateizeilen->at(i).find("#file",0)) != string::npos) {
            string dateiname = Quelldateizeilen->at(i).substr(index+6,string::npos);
            bs::path datei(dateiname);
            DateiCheck(datei);
            DateiListe->push_back(datei);
            //die Zeile für Java auskommentieren
            Quelldateizeilen->at(i) = "//" + Quelldateizeilen->at(i);
        }
    }

    //in der zweiter Zeile könnten Parameter für die JVM stehen.
    if (Quelldateizeilen->at(1).find("#option",0) == 0) {
        jvm_optionen = Quelldateizeilen->at(1).substr(7,string::npos);
        Quelldateizeilen->at(1) = "//" + Quelldateizeilen->at(1);
        int cpi = jvm_optionen.find("-cp ",0);
        if (cpi != string::npos) {
            int nextleer = jvm_optionen.find(" ",cpi+7);
            int ende_sub = string::npos;
            int ende_del = string::npos;
            if (nextleer != string::npos) {
                ende_sub = nextleer-cpi-4;
                ende_del = nextleer-cpi;
            }
            classpath += jvm_optionen.substr(cpi+4,ende_sub);
            //den classpath aus den optionen entfernen
            jvm_optionen.erase(cpi,ende_del);
        }

    }
    
    return DateiListe;
}

/**
 * Prüft ob eine Datei wirklich existiert. Wenn nicht wird die Fehler-Variable
 * gesetzt.
 */
void preprozessor::DateiCheck(bs::path datei) {
    if (!bs::exists(datei)) {
        cout << "FEHLER: auf " << datei.string() << " kann nicht zugegriffen werden!" << endl;
        FehlerStatus = 1;
    }

}

int preprozessor::getFehlerStatus() {
    return FehlerStatus;
}

/**
 * Liest die Quelle vollständig ein für weitere bearbeitungen.
 * setzt bei Problemen den FehlerStatus auf 2.
 */
void preprozessor::QuelleEinlesen() {
    ifstream quelle;
    //Quelle öffnen
    quelle.open(Argumente[1]);

    //prüfen, ob das öffnen geklappt hat
    if (!quelle.is_open()) {
        cerr << "FEHLER: " << Argumente[1] << " konnte nicht geöffnet werden!" << endl;
        FehlerStatus = 2;
        return;
    }

    //quell-datei vollständig einlesen
    Quelldateizeilen = new vector<string>();
    while (!quelle.eof()) {
        string einezeile;
        getline(quelle, einezeile);
        Quelldateizeilen->push_back(einezeile);
    }
    //quell-datei schließen
    quelle.close();

}

/**
 * Entfernt das #! aus der ersten Zeile.
 * das #! wird vom Betriebssystem gebraucht, um jss zu starten, javac akzeptiert
 * es aber nicht, also weg damit.
 * Setzt den FehlerStatus auf 3 falls was schief geht.
 */
void preprozessor::prozess() {
    //Ein und ausgabe Dateien
    ofstream ziel;

    //Ziel öffnen
    string quelldatei = DateiListe->at(0).string();

    string tempdir = Cache->getCacheDir(DateiListe) + "/";
    string zieldatei = tempdir + bs::basename(bs::path(quelldatei)) + ".java";
    ziel.open(zieldatei.c_str());

   //prüfen, ob das öffnen geklappt hat
    if (!ziel.is_open()) {
        cerr << "FEHLER: " << zieldatei << " konnte nicht geöffnet werden!" << endl;
        FehlerStatus = 3;
        return;
    }

    //erst mal die Datei komplett durchsuchen um festzustellen, ob es eine
    //zeile mit "public class" gibt
    bool hatPublicClass = false;
    for (int i=0;i<Quelldateizeilen->size();i++) {
        if (Quelldateizeilen->at(i).find("public",0) != string::npos) {
            if (Quelldateizeilen->at(i).find("class",0) != string::npos) {
                hatPublicClass = true;
                break;
            }
        }
    }

    //wenn keine Klasse in der Quell-Datei deklariert ist, dann wird das hier
    //erledigt!
    if (!hatPublicClass) {
        //Mainklasse anhand des Dateinamens bestimmen
        string mainklasse = getMainClass(Argumente[1]);

        //Falls import verwendet wird, muss das hier eingefügt werden
        for (int i=0;i<Quelldateizeilen->size();i++) {
            if (Quelldateizeilen->at(i).find("import",0) != string::npos) {
                ziel << Quelldateizeilen->at(i) << endl;
                Quelldateizeilen->erase(Quelldateizeilen->begin()+i);
                i--;
            }
        }

        //Standard-Klassen importieren
        ziel << "import java.util.*; import java.io.*; import javax.swing.*; ";

        //Klasse erzeugen
        ziel << "public class " << mainklasse << "{ ";

        //Mainfunktion
        ziel << "public static void main(String[] args) { ";

        //Das System-Look-And-Feel soll verwendet werden
        ziel << "try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception e) {} ";

        //Konstructor für Klasse (damit auch nicht statische Funktionen aufgerufen
        //werden können
        ziel << "new " + mainklasse + "(); } ";
        ziel << "public " + mainklasse + "() { ";

        //Alles in Try-catch-Block, damit nicht jede anweisung einzeln abgefangen werden muss
        ziel << "try { " << endl;
    }


    //wenn es eine Klassen-Definition gibt, dann muss die erste Zeile rein, damit
    //die Anzahl der Zeilen Stimmt
    if (hatPublicClass) {
        ziel << "//" << Quelldateizeilen->at(0) << endl;
    }

    //Inhalt der Quelle ins Ziel schreiben
    //die erste Zeile wird weg gelassen, da nur für das OS wichtig
    for (int i=1;i<Quelldateizeilen->size();i++) {
        ziel << Quelldateizeilen->at(i) << endl;
    }

    //Klasse und main abschließen
    if (!hatPublicClass) {
        ziel << "} catch (Exception ex) {ex.printStackTrace();} }}" << endl;
    }

    //Dateien wieder schließen
    ziel.close();

    //weitere dateien ins Ziel-Verzeichniss kopieren
    for (int i=1;i<DateiListe->size();i++) {
        bs::path quelle = DateiListe->at(i);
        string zielname = tempdir + bs::basename(quelle) + ".java";
        bs::path zielpath(zielname);
        bs::copy_file(quelle,zielpath);
    }

}

/**
 * Gibt den Namen der Main-Klasse zurück.
 * @param argument dateiname der java-datei
 */
string preprozessor::getMainClass(char* argument) {
    string mainklasse(argument);
    bs::path datei(mainklasse);
    mainklasse = bs::basename(datei);
    return mainklasse;
}

/**
 * Java-dateien compilieren.
 * Compiliert alle dateien im Temp-Dir.
 * Setzt FehlerStatus auf 4 wenn was schief läuft
 */
int preprozessor::Compilieren() {
    string tempdir = Cache->getCacheDir(DateiListe);
    //falls die jvm_optionen einen Classpath enthalten muss dieser
    //mit übernommen werden
    string cp = "";
    if (!classpath.empty()) cp = "-cp "+classpath;
    string komando = "javac " + cp + " " + tempdir + "/*.java";
    int ergebnis = system(komando.c_str());
    if (ergebnis != 0) FehlerStatus = 4;
    return ergebnis;

}

/**
 * Führt das Compilierte Programm aus.
 * das compilierte Programm wird mit den übergebenden Parametern ausgeführt.
 */
int preprozessor::Ausfuehren() {
    string tempdir = Cache->getCacheDir(DateiListe);
    string komando = "java " + jvm_optionen + " -cp " + tempdir;
    if (!classpath.empty()) komando += ":" + classpath;

    //der name der Main-Klasse ist der Dateiname im Argument 1
    string mainklasse = getMainClass(Argumente[1]);

    //Mainklasse anhängen
    komando += " " + mainklasse;

    //Argumente anhängen
    for (int i=2;i<AnzahlDerArgumente;i++) {
        komando += " " + string(Argumente[i]);
    }

    //Ausführen
    return system(komando.c_str());
}

/**
 * Fügt die jar-dateien im Lib-Verzeichnis zum Classpath hinzu
 */
void preprozessor::addLibToClasspath() {
    string jssdir = GetPathName();

    //prüfen ob jssdir ein link ist, ja ja folgen bis zur eigentlichen datei
    bs::path jsspath(jssdir+"/jss");
    while (bs::is_symlink(jsspath)) {
        char buf[1024];
        ssize_t len;
        if ((len = readlink(jsspath.directory_string().c_str(), buf, sizeof(buf)-1)) != -1)
            buf[len] = '\0';
        if (len == -1) return;
        string neuedatei(buf);
        jsspath = bs::path(neuedatei);
    }
    jsspath = jsspath.remove_filename();

    bs::path libdir = jsspath /= bs::path("lib");
    //der Cache verfügt über eine Funktion um verzeichniseinträge zu finden
    vector<bs::path> *libs = Cache->getEintraegeInVerzeichnis(libdir);

    //Bei fehler raus hier!
    if (libs == NULL) return;
    
    //in einer Schleife über alle einträge alle jar-Dateien zum classpath hinzufügen
    for (int i=0;i<libs->size();i++) {
        if (libs->at(i).directory_string().find(".jar",0) != string::npos) {
            classpath += libdir.directory_string() + "/" + libs->at(i).directory_string() + ":";
        }
    }
}

/**
 * Gibt das Verzeichnis der jss-Datei zurück, muss für jedes Betriebssystem
 * extra implementiert werden
 */
string preprozessor::GetPathName()
{
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

    return std::string(path);
}


