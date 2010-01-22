package jsslib.util;

import java.util.EventObject;

/**
 *          Ein Event für beliebige Zwecke.
 * 
 *          Das Event sendende Programm muß folgendes enthalten:
 * 
 *          Ein Objekt vom Typ UserEventListener:
 *          public UserEventListener uel = null;
 * 
 *          Eine Funktion zum Hinzufügen eines Eventlisteners:
 *          public void addUserEventListener(UserEventListener l) {
 *               uel.add(l);
 *           }
 * 
 *          Im Konstructor:
 *          uel = new UserEventListener();
 * 
 *          Zum Auslösen des Events:
 *          uel.UserEventPerformed(new UserEvent(this, 0)); 
 * 
 *          Der Empänger muß die addUserEventListener Funktion des Senders aufrufen 
 *          und die UserEventPerformed Methode des EventListeners überschreiben:
 *          sender.addUserEventListener(new UserEventListener() {
 *                  public void UserEventPerformed(UserEvent e) {
 *                      Bei_UserEvent_ausfuehren();
 *                  } 
 *              });
 *
 * @author  Robert Schuster
 * 
 *
 */

public class UserEvent extends EventObject {

    public int info;
    public String info_text;
    
    public UserEvent(Object source, int info_id) {
        super(source);
        info = info_id;
    }

    public UserEvent(Object source, int info_id, String info_text) {
        super(source);
        info = info_id;
        this.info_text = info_text;
    }
}
