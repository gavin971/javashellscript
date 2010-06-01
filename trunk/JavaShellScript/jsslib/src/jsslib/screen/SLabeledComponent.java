package jsslib.screen;

import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import javax.swing.JLabel;
import javax.swing.JPanel;
import jsslib.util.CommonFunctions;

/**
 * Eine beliebige Komponente mit einem darüber liegendem Label
 * 
 * @author Robert Schuster
 */
public class SLabeledComponent extends JPanel {

    public JLabel Label;
    public Component Componente;
    
    public SLabeledComponent() {
        //Die Beiden Componenten
        Label = new JLabel();
        Label.setOpaque(false);
        //Das Layout
        this.setLayout(new GridLayout(2,1));
        this.add(Label);
        this.setOpaque(false);
    }

    /**
     * Gibt die Möglichkeit das Label besser zu platzieren
     * @param labelinsets
     */
    public SLabeledComponent(Insets labelinsets) {
        //Die Beiden Componenten
        Label = new JLabel();
        Label.setOpaque(false);
        JPanel lp = new JPanel();
        lp.setOpaque(false);
        lp.setLayout(new GridBagLayout());
        GridBagConstraints gbc = CommonFunctions.getGridBagConstraints();
        gbc.insets = labelinsets;
        lp.add(Label,gbc);
        //Das Layout
        this.setLayout(new GridLayout(2,1));
        this.add(lp);
        this.setOpaque(false);
    }

    /**
     * Die Schriftart für Label und Textfeld setzen
     * @param arg0
     */
    @Override
    public void setFont(Font arg0) {
        if (Label != null) Label.setFont(arg0);
        if (Componente != null) Componente.setFont(arg0);
        super.setFont(arg0);
    }
    
    /**
     * Fügt eine beliebige Componente ein
     * @param c
     */
    public void setComponent(Component c) {
        this.Componente = c;
        this.add(Componente);
    }
        
}
