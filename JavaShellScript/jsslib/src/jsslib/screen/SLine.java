package jsslib.screen;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * Eine einfache Linie von einer Ecke zur anderen
 * @author Robert Schuster
 */
public class SLine extends JPanel {

    //Konstanten für den Typ der Linie
    public final static int LINKS_OBEN_RECHTS_UNTEN = 1;
    public final static int LINKS_UNTEN_RECHTE_OBEN = 2;
    public final static int HORIZONTAL = 3;
    public final static int VERTIKAL = 4;
    private int typ;
    
    public SLine(int typ) {
        this.typ = typ;
        switch (typ) {
            case HORIZONTAL:
                setPreferredSize(new Dimension(0, 1));
                break;
            case VERTIKAL:
                setPreferredSize(new Dimension(1, 0));
                break;
        }
    }
    
    /**
     * Hier wird die Linie gezeichnet
     * @param arg0
     */
    @Override
    protected void paintComponent(Graphics arg0) {
        Graphics2D g = (Graphics2D)arg0;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        //Typ unterscheiden und Koordinaten bestimmen
        Point[] Punkte = new Point[2];
        switch (typ) {
            case LINKS_OBEN_RECHTS_UNTEN:
                Punkte[0] = new Point(0,0);
                Punkte[1] = new Point(this.getWidth()-1,this.getHeight()-1);
                break;
            case LINKS_UNTEN_RECHTE_OBEN:
                Punkte[0] = new Point(0,this.getHeight()-1);
                Punkte[1] = new Point(this.getWidth()-1,0);
                break;
            case HORIZONTAL:
                Punkte[0] = new Point(0,0);
                Punkte[1] = new Point(this.getWidth()-1,0);
                break;
            case VERTIKAL:
                Punkte[0] = new Point(0,0);
                Punkte[1] = new Point(0,this.getHeight()-1);
                break;
        }
        g.drawLine(Punkte[0].x, Punkte[0].y, Punkte[1].x, Punkte[1].y);        
    }
    
}
