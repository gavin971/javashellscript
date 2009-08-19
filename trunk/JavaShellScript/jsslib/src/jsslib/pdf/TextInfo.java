/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsslib.pdf;

import com.lowagie.text.pdf.parser.Matrix;

/**
 *
 * @author robert schuster
 */
public class TextInfo implements Comparable {

    public Matrix anfang;
    public Matrix ende;
    public String text;

    public TextInfo(Matrix anfang, Matrix ende, String text) {
        this.anfang = anfang;
        this.ende = ende;
        this.text = text;
    }

    /**
     * Sortiert nach der X-Koordinate
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        return new Float(anfang.get(Matrix.I32)).compareTo(((TextInfo)o).anfang.get(Matrix.I32));
    }



}
