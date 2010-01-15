/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsslib.util;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author robert
 */
public class CommonFunctions {

    /**
     * @return returns a standard GridBagContraints object
     */
    public static GridBagConstraints getGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(0,0,0,0);
        return gbc;
    }

    /**
     * read in a .properties-File
     * @param filename
     * @return
     */
    public static Properties getPropertiesFromFile(String filename) {
	Properties props = new Properties();
	try {
            FileInputStream propInFile = new FileInputStream( filename );
            props.load(propInFile);
            propInFile.close();
	} catch(IOException ex) {
            ex.printStackTrace();
            return null;
	}
        return props;
    }

}
