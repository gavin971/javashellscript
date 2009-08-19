/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsslib.pdf;

import java.util.ArrayList;
import java.util.Collections;

/**
 *
 * @author robert
 */
public class TextZeile implements Comparable {
    public Float koordinate;
    public ArrayList<TextInfo> zeileninhalt;

    public TextZeile(float koordinate) {
        this.koordinate = koordinate;
        zeileninhalt = new ArrayList<TextInfo>();
    }

    /**
     * Gibt den Text der Zeile aus.
     * Dabei werden die Worte nach x-Koordinate sortiert hintereinander geschrieben
     * und dabei durch zwei Leerzeichen getrennt.
     * @return
     */
    public String getText() {
        String ergebnis = "";
        //Sortiert die wörter nach x-Koordinate
        Collections.sort(zeileninhalt);
        //Schleife über alle einträge
        for (int i=0;i<zeileninhalt.size();i++) {
            ergebnis += zeileninhalt.get(i).text + " ";
        }
        return ergebnis;
    }

    /**
     * Gibt den Zeileninhalt als Array zurück. Dabei hat jedes Text-Objekt einen
     * eigenen Eintrag.
     * @return
     */
    public String[] getTextArray() {
        String[] ergebnis = new String[zeileninhalt.size()];
        //Sortiert die wörter nach x-Koordinate
        Collections.sort(zeileninhalt);
        //Texte ins Array schreiben
        for (int i=0;i<zeileninhalt.size();i++) {
            ergebnis[i] = zeileninhalt.get(i).text;
        }
        return ergebnis;
    }

    /**
     * Sortiert nach der Y-Koordinate
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        return koordinate.compareTo(((TextZeile)o).koordinate);
    }


}
