/*
 * Diese Klasse erweitert den SimpleTextExtractingPdfContentStreamProcessor
 * um besser Tabellen exportieren zu können
 */

package jsslib.pdf;

import com.lowagie.text.pdf.parser.GraphicsState;
import com.lowagie.text.pdf.parser.Matrix;
import com.lowagie.text.pdf.parser.PdfContentStreamProcessor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author robert schuster
 */
public class TextExtractingPdfContentStreamProcessor extends PdfContentStreamProcessor {

    /** keeps track of a text matrix. */
    Matrix lastTextLineMatrix = null;
    /** keeps track of a text matrix. */
    Matrix lastEndingTextMatrix = null;

    ArrayList<TextInfo> seiteninhalt = null;

    /**
     * Creates a new text extraction processor.
     */
    public TextExtractingPdfContentStreamProcessor() {
    }

    @Override
    public void reset() {
        super.reset();
        lastTextLineMatrix = null;
        lastEndingTextMatrix = null;
        seiteninhalt = new ArrayList<TextInfo>();
    }

    public final static int RETURN_STRING = 1;
    public final static int RETURN_LIST = 2;
    /**
     * Returns the result so far.
     * @param typ Gibt an ob ein String RETURN_STRING oder eine ArrayList<String[]> RETURN_LIST
     *            zurück gegeben werden soll.
     * @return	a Object with the resulting text.
     */
    public Object getResultantText(int typ){

        //zunächst den Seiteninhalt nach in Spalten einordnen
        Hashtable<Float,TextZeile> allezeilen = new Hashtable<Float,TextZeile>();

        //if einer Schleife alle Objekte in die richtige zeile einordnen
        for (int i=0;i<seiteninhalt.size();i++) {
            //Koordinate abfragen
            Float ykoordinate = seiteninhalt.get(i).anfang.get(Matrix.I31);
            //Ist diese Koordinate schon in der Liste
            if (allezeilen.containsKey(ykoordinate)) {
                allezeilen.get(ykoordinate).zeileninhalt.add(seiteninhalt.get(i));
            } else {
            //diese Zeile ist noch nicht in der Liste
                TextZeile tz = new TextZeile(ykoordinate);
                tz.zeileninhalt.add(seiteninhalt.get(i));
                allezeilen.put(ykoordinate, tz);
            }
        }

        //die Zeilenkoordinaten sortieren, dazu erst mal in ein Array packen
        Float[] zeilenkoordinaten = new Float[allezeilen.size()];
        Enumeration<Float> allezeilen_enum = allezeilen.keys();
        int index = 0;
        while(allezeilen_enum.hasMoreElements()) {
            zeilenkoordinaten[index] = allezeilen_enum.nextElement();
            index++;
        }

        //sortieren
        Arrays.sort(zeilenkoordinaten);

        //Text in ergebnis einfügen
        switch (typ) {
            case RETURN_STRING:
                StringBuffer result = new StringBuffer();
                for (int i=0;i<zeilenkoordinaten.length;i++) {
                    result.append(allezeilen.get(zeilenkoordinaten[i]).getText());
                    result.append("\n");
                }
                return result.toString();
            case RETURN_LIST:
                ArrayList<String[]> ergebnis = new ArrayList<String[]>();
                for (int i=0;i<zeilenkoordinaten.length;i++) {
                    ergebnis.add(allezeilen.get(zeilenkoordinaten[i]).getTextArray());
                }
                return ergebnis;
        }

        //Unglültiger Typ angegeben
        return "Ungültiger Typ!";
    }

    /**
     * Writes text to the result.
     * @param text	The text that needs to be displayed
     * @param endingTextMatrix	a text matrix
     * @see com.lowagie.text.pdf.parser.PdfContentStreamProcessor#displayText(java.lang.String, com.lowagie.text.pdf.parser.Matrix)
     */
    public void displayText(String text, Matrix endingTextMatrix){
        seiteninhalt.add(new TextInfo(getCurrentTextMatrix(),endingTextMatrix,text));
//        System.out.println(text);
//        System.out.println(endingTextMatrix);
//        System.out.println(getCurrentTextMatrix());
//        if (true) return;
//        boolean hardReturn = false;
//        if (lastTextLineMatrix != null && lastTextLineMatrix.get(Matrix.I31) < getCurrentTextLineMatrix().get(Matrix.I31)){
//        //if (!textLineMatrix.equals(lastTextLineMatrix)){
//            hardReturn = true;
//        }
//
//        float currentX = getCurrentTextMatrix().get(Matrix.I31);
//        if (hardReturn){
//            //System.out.println("<Hard Return>");
//            result.append('\n');
//        } else if (lastEndingTextMatrix != null){
//            float lastEndX = lastEndingTextMatrix.get(Matrix.I31);
//
//            //System.out.println("Displaying '" + text + "' :: lastX + lastWidth = " + lastEndX + " =?= currentX = " + currentX + " :: Delta is " + (currentX - lastEndX));
//            GraphicsState gstate = new GraphicsState(gs());
//            float spaceGlyphWidth = 1; //gstate.font.getWidth(' ')/1000f;
//            float spaceWidth = 0.1f; //(spaceGlyphWidth * gstate.fontSize + gstate.characterSpacing + gstate.wordSpacing) * gstate.horizontalScaling; // this is unscaled!!
//            Matrix scaled = new Matrix(spaceWidth, 0).multiply(getCurrentTextMatrix());
//            float scaledSpaceWidth = scaled.get(Matrix.I31) - getCurrentTextMatrix().get(Matrix.I31);
//
//            if (currentX - lastEndX > 0 ){
//                //System.out.println("<Implied space on text '" + text + "'> lastEndX=" + lastEndX + ", currentX=" + currentX + ", spaceWidth=" + spaceWidth);
//                result.append(' ');
//            }
//        } else {
//            //System.out.println("Displaying first string of content '" + text + "' :: currentX = " + currentX);
//        }
//
//        //System.out.println("After displaying '" + text + "' :: Start at " + currentX + " end at " + endingTextMatrix.get(Matrix.I31));
//
//        result.append(text);
//
//        lastTextLineMatrix = getCurrentTextLineMatrix();
//        lastEndingTextMatrix = endingTextMatrix;

    }

}
