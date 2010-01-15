/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jsslib.macwidgets;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import javax.swing.JPanel;

/**
 *
 * @author robert
 */
public class ITunesHeaderPanel extends JPanel {

    // the hard-coded preferred height. ideally this would be derived
    // from the font size.
    private int HEADER_HEIGHT = 25;

    // the background colors used in the multi-stop gradient.
    private static Color BACKGROUND_COLOR_1 = new Color(0x393939);
    private static Color BACKGROUND_COLOR_2 = new Color(0x2e2e2e);
    private static Color BACKGROUND_COLOR_3 = new Color(0x232323);
    private static Color BACKGROUND_COLOR_4 = new Color(0x282828);

    // the color to use for the top and bottom border.
    private static Color BORDER_COLOR = new Color(0x171717);

    // the inner shadow colors on the top of the header.
    private static Color TOP_SHADOW_COLOR_1 = new Color(0x292929);
    private static Color TOP_SHADOW_COLOR_2 = new Color(0x353535);
    private static Color TOP_SHADOW_COLOR_3 = new Color(0x383838);

    // the inner shadow colors on the bottom of the header.
    private static Color BOTTOM_SHADOW_COLOR_1 = new Color(0x2c2c2c);
    private static Color BOTTOM_SHADOW_COLOR_2 = new Color(0x363636);

    public ITunesHeaderPanel(int height) {
        HEADER_HEIGHT = height;
    }


    @Override
    public Dimension getPreferredSize() {
        return new Dimension(-1, HEADER_HEIGHT);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D graphics = (Graphics2D) g.create();

        // calculate the middle of the area to paint.
        int midY = getHeight()/2;

        // paint the top half of the background with the corresponding
        // gradient. note that if we were using Java 6, we could use a
        // LinearGradientPaint with multiple stops.
        Paint topPaint = new GradientPaint(0, 0, BACKGROUND_COLOR_1,
                0, midY, BACKGROUND_COLOR_2);
        graphics.setPaint(topPaint);
        graphics.fillRect(0, 0, getWidth(), midY);

        // paint the top half of the background with the corresponding
        // gradient.
        Paint bottomPaint = new GradientPaint(0, midY + 1, BACKGROUND_COLOR_3,
                0, getHeight(), BACKGROUND_COLOR_4);
        graphics.setPaint(bottomPaint);
        graphics.fillRect(0, midY, getWidth(), getHeight());

        // draw the top inner shadow.
        graphics.setColor(TOP_SHADOW_COLOR_1);
        graphics.drawLine(0, 1, getWidth(), 1);
        graphics.setColor(TOP_SHADOW_COLOR_2);
        graphics.drawLine(0, 2, getWidth(), 2);
        graphics.setColor(TOP_SHADOW_COLOR_3);
        graphics.drawLine(0, 3, getWidth(), 3);

        // draw the bottom inner shadow.
        graphics.setColor(BOTTOM_SHADOW_COLOR_1);
        graphics.drawLine(0, getHeight() - 3, getWidth(), getHeight() - 3);
        graphics.setColor(BOTTOM_SHADOW_COLOR_2);
        graphics.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);

        // draw the top and bottom border.
        graphics.setColor(BORDER_COLOR);
        graphics.drawLine(0, 0, getWidth(), 0);
        graphics.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        graphics.dispose();
    }
}