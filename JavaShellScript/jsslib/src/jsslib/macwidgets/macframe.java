package jsslib.macwidgets;

import com.explodingpixels.macwidgets.BottomBar;
import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacUtils;
import com.explodingpixels.macwidgets.SourceList;
import com.explodingpixels.macwidgets.SourceListCategory;
import com.explodingpixels.macwidgets.SourceListItem;
import com.explodingpixels.macwidgets.SourceListModel;
import com.explodingpixels.macwidgets.UnifiedToolBar;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import jsslib.screen.SLine;
import jsslib.util.CommonFunctions;

/**
 *
 * @author robert schuster
 */
public class macframe extends JFrame {

    public UnifiedToolBar toolBar;
    public BottomBar bottomBar;
    public SourceListModel listModel;
    public SourceList sourceList;
    public JPanel contentPanel;

    /**
     * create a window with a UnifiedToolbar and a Bottombar in MacOS X style
     */
    public macframe() {
        //exit on close!
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Dreigeteilte Fläche
        this.setLayout(new GridBagLayout());

        //Add mac-toolbar
        MacUtils.makeWindowLeopardStyle(this.getRootPane());
        toolBar = new UnifiedToolBar();
        toolBar.installWindowDraggerOnWindow(this);
        GridBagConstraints toolBar_gbc = CommonFunctions.getGridBagConstraints();
        toolBar_gbc.weighty=0;
        toolBar_gbc.gridwidth = 3;
        this.add(toolBar.getComponent(), toolBar_gbc);

        listModel = new SourceListModel();
        sourceList = new SourceList(listModel);
        sourceList.setFocusable(false);
        sourceList.getComponent().setPreferredSize(new Dimension(200, 0));
        GridBagConstraints sourceList_gbc = CommonFunctions.getGridBagConstraints();
        sourceList_gbc.gridy = 1;
        sourceList_gbc.weightx = 0;
        this.add(sourceList.getComponent(), sourceList_gbc);

        GridBagConstraints panel_gbc = CommonFunctions.getGridBagConstraints();
        panel_gbc.gridy = 1;
        panel_gbc.gridx = 2;
        contentPanel = new JPanel();
        contentPanel.setOpaque(true);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setVisible(true);
        contentPanel.setLayout(new GridLayout(1, 1));
        this.add(contentPanel, panel_gbc);

        GridBagConstraints linie_gbc = CommonFunctions.getGridBagConstraints();
        linie_gbc.gridy = 1;
        linie_gbc.gridx = 1;
        linie_gbc.weightx = 0;
        SLine linie = new SLine(SLine.VERTIKAL);
        linie.setVisible(true);
        linie.setPreferredSize(new Dimension(1, 0));
        linie.setForeground(Color.GRAY);
        this.add(linie, linie_gbc);

        bottomBar = new BottomBar(BottomBarSize.LARGE);
        GridBagConstraints bottomBar_gbc = CommonFunctions.getGridBagConstraints();
        bottomBar_gbc.gridy = 2;
        bottomBar_gbc.gridwidth = 3;
        bottomBar_gbc.weighty = 0;
        this.add(bottomBar.getComponent(), bottomBar_gbc);
    }

    /**
     * add a component to the left of the Unifiedtoolbar
     * @param component
     */
    public void addToToolbarLeft(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        toolBar.addComponentToLeft(component);
        this.validate();
    }

    /**
     * add a component to the right of the Unifiedtoolbar
     * @param component
     */
    public void addToToolbarRight(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        toolBar.addComponentToRight(component);
        this.validate();
    }

    /**
     * add a component to the center of the Unifiedtoolbar
     * @param component
     */
    public void addToToolbarCenter(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        toolBar.addComponentToCenter(component);
        this.validate();
    }

    /**
     * add a component to the left of the Bottombar
     * @param component
     */
    public void addToBottombarLeft(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        bottomBar.addComponentToLeft(component);
        this.validate();
    }

    /**
     * add a component to the right of the Bottombar
     * @param component
     */
    public void addToBottombarRight(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        bottomBar.addComponentToRight(component);
        this.validate();
    }

    /**
     * add a component to the center of the Bottombar
     * @param component
     */
    public void addToBottombarCenter(JComponent component) {
        if (component instanceof JButton)
            component.putClientProperty("JButton.buttonType", "textured");
        bottomBar.addComponentToCenter(component);
        this.validate();
    }

    /**
     * add a Category to the sourcelist
     * @param name
     */
    public void addCategory(String name) {
        SourceListCategory category = new SourceListCategory(name);
        listModel.addCategory(category);
    }

    public void addItemToCategory(String categoryname, String itemname) {
        List<SourceListCategory> categorys = listModel.getCategories();
        SourceListCategory category = null;
        for (int i=0;i<categorys.size();i++) {
            if (categorys.get(i).getText().equals(categoryname)) {
                category = categorys.get(i);
                break;
            }
        }
        if (category == null) return;
        listModel.addItemToCategory(new SourceListItem(itemname), category);
    }

    /**
     * Moves the Window to the Screen center
     */
    public void setPositionCenterOnScreen() {
        //Daten der Bildschrime holen
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Point center = ge.getCenterPoint();
        center.x -= this.getWidth()/2;
        center.y -= this.getHeight()/2;
        if (center.x < 0 || center.y <0) this.setBounds(ge.getMaximumWindowBounds());
        else this.setLocation(center);
    }
}
