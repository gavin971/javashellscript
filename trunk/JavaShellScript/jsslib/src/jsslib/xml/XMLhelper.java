package jsslib.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Hilfsfunktionen zum arbeiten mit den xml-daten
 * @author robert schuster
 */
public class XMLhelper {

    /**
     * Gibt den ersten vorkommenden Knoten mit dem passenden Namen zurück
     * @param Name  kann / enthalten, wird dann als weiteres child behandelt
     * @param node
     * @return
     */
    public static Node getChildNodeByName(String Name, Node node) {
        Node result = null;
        if (node == null) return null;
        if (Name.contains("/")) {
            String[] names = Name.split("/");
            result = getChildNodeByName(names[0], node);
            for (int i=1;i<names.length;i++) {
                result = getChildNodeByName(names[i], result);
            }
        } else {
            NodeList nl = node.getChildNodes();
            for (int i=0;i<nl.getLength();i++) {
                if (nl.item(i).getNodeName().equals(Name)) {
                    result = nl.item(i);
                    break;
                }
            }            
        }
        return result;
    }

    /**
     * Gibt alle Child-Nodes zurück, die einen bestimmten Namen haben
     * @param Name
     * @param node
     * @return
     */
    public static NodeList getChildNodesByName(String Name, Node node) {
        if (node == null) return null;
        NodeList nl = ((Element)node).getElementsByTagName(Name);
        return nl;
    }

    /**
     * Gibt den Text eines XML-Knotens zurück, auch wenn dieser in einem CDATA-Tag
     * eingeschlossen ist.
     * @param node
     * @return
     */
    public static String getNodeText(Node node) {
        if (node == null) return null;
        NodeList nl = node.getChildNodes();
        //wenn es mehr als ein Kind gibt, dann wird der Text aneinander gehangen
        String ergebnis = "";
        for (int i=0;i<nl.getLength();i++) {
            //recursive durch weitere kinder
            if (nl.item(i).hasChildNodes()) ergebnis += getNodeText(nl.item(i));
            //text übernehmen
            int typ = nl.item(i).getNodeType();
            if (typ == Node.TEXT_NODE || typ == Node.CDATA_SECTION_NODE)
                ergebnis += nl.item(i).getNodeValue();
        }

        return ergebnis;
    }

    /**
     * Gibt den Text eines Child-Nodes zurück
     * @param childname
     * @param node
     * @return
     */
    public static String getChildNodeText(String childname, Node node) {
        if (node == null) return null;
        Node child = getChildNodeByName(childname, node);
        return getNodeText(child);
    }

    /**
     * Gibt den Inhalt eines Child-Nodes als Float zurück, sofern er entsprechend
     * formatiert ist, ansonsten null
     * @param childname
     * @param node
     * @return
     */
    public static Float getChildNodeFloat(String childname, Node node) {
        if (node == null) return null;
        String nodetext = getChildNodeText(childname, node);
        try {
            return Float.parseFloat(nodetext);
        } catch (Exception ex){
            return null;
        }
    }
    
    /**
     * Gibt den Inhalt eines Attributs eines Childnodes zurück
     * @param childname
     * @param attrname
     * @param node
     * @return
     */
    public static String getChildNodeAttributeText(String childname, String attrname, Node node) {
        if (node == null) return null;
        Node child = getChildNodeByName(childname, node);
        if (child == null) return null;
        Node attr = child.getAttributes().getNamedItem(attrname);
        if (attr == null) return null;
        return attr.getNodeValue();
    }
    
    /**
     * Gibt den Inhalt eines Attributs eines Nodes zurück
     * @param attrname
     * @param node
     * @return
     */
    public static String getNodeAttributeText(String attrname, Node node) {
        if (node == null) return null;
        Node attr = node.getAttributes().getNamedItem(attrname);
        if (attr == null) return null;
        return attr.getNodeValue();
    }
    
    /**
     * Gibt den Wert eines Attributes als Integer zurück
     * @param childname
     * @param attrname
     * @param node
     * @return
     */
    public static Integer getChildNodeAttributeInt(String childname, String attrname, Node node) {
        if (node == null) return null;
        String text = getChildNodeAttributeText(childname, attrname, node);
        if (text == null) return null;
        Integer result = null;
        try {
            result = Integer.parseInt(text);
        } catch (NumberFormatException ex) {};
        return result;
    }
    
    /**
     * Gibt den Wert eines Attributes als Integer zurück
     * @param childname
     * @param attrname
     * @param node
     * @return
     */
    public static Float getChildNodeAttributeFloat(String childname, String attrname, Node node) {
        if (node == null) return null;
        String text = getChildNodeAttributeText(childname, attrname, node);
        if (text == null) return null;
        Float result = null;
        try {
            result = Float.parseFloat(text);
        } catch (NumberFormatException ex) {};
        return result;
    }
}
