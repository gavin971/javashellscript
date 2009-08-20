/*
 * Diese Klasse stellt eine Möglichkeit zur Automatischen Parallelisierung 
 * einer For-Schleife unter Java dar.
 */

package jsslib.parallel;

/**
 *
 * @author robert schuster
 */
public abstract class ForLoop {

    /**
     * Modus "normal", die Schleife wird in nThreads gleiche Teile zerlegt und 
     * diese werden ohne kommunikation untereinander abgearbeitet
     */
    public final static int NORMAL = 1;
    /**
     * Modus "verteilen", es werden nThreads erzeugt, es gibt aber nur einen Zähler,
     * immer wenn ein Thread einen Schleifen-Durchlauf abgeschlossen hat, fragt
     * er beim Zähler nach dem nächsten zu bearbeitenden Index. Dadurch wird eine 
     * gleichmässige Auslastung garantiert, ein Schleifendurchlauf sollte aber lange dauern.
     */
    public final static int VERTEILEN = 2;

    /**
     * Die Anweisungen in Loop werden auf nThreads Threads verteilt ausgeführt.
     * @param start
     * @param stop
     * @param schritt
     * @param nThreads
     */
    public ForLoop(int start, int stop, int schritt, int nThreads) {
        Init_Normal(start, stop, schritt, nThreads);
    }

    /**
     * Die Anweisungen in Loop werden auf nThreads Threads verteilt ausgeführt.
     *
     * @param start
     * @param stop
     * @param schritt
     * @param nThreads
     * @param modus = NORMAL oder VERTEILEN
     */
    public ForLoop(int start, int stop, int schritt, int nThreads, int modus) {
        switch (modus) {
            case NORMAL:
                Init_Normal(start, stop, schritt, nThreads);
                break;
            case VERTEILEN:
                Init_Verteilen(start, stop, schritt, nThreads);
                break;
        }
    }

    /**
     * Es werden nThreads angelegt, die aber nicht gleich große Stücke bearbeiten,
     * sondern nach jedem Schleifendurchlauf nachfragen welchen Index sie als nächstes
     * bekommen. Das ist zwar langsamer, führt aber bei sehr langen Schleifen-durchläufen
     * zu einer besseren Auslastung.
     * @param start
     * @param stop
     * @param schritt
     * @param nThreads
     */
    private void Init_Verteilen(int start, int stop, int schritt, int nThreads) {
        //Kontrollieren, ob die zahlen zusammen passen
        if (!SchrittKontrolle(start, stop, schritt)) return;

        //Kontrollieren, ob die Anzahl der Threads positiv ist
        if (!ThreadAnzahlKontrolle(nThreads)) return;

        //Verteiler erzeugen
        Verteiler verteiler = new Verteiler(start, stop, schritt);

        //Die Threads erzeugen und starten
        LoopThreadVerteilen[] lts = new LoopThreadVerteilen[nThreads];
        for (int i=0;i<nThreads;i++) {
            lts[i] = new LoopThreadVerteilen(verteiler);
            lts[i].start();
        }

        //Darauf warten, das alle fertig werden
        for (int i=0;i<nThreads;i++) {
            try {
            lts[i].join();
            } catch (InterruptedException ex) {ex.printStackTrace();}
        }

    }

    /**
     * Prüft ob die Anzahl der Threads Positiv ist.
     * @param nThreads
     * @return
     */
    private boolean ThreadAnzahlKontrolle(int nThreads) {
        if (nThreads < 1) {
            System.err.println("FEHLER: ForLoop: Die Anzahl der Threads ist zu klein!");
            System.err.println("FEHLER: ForLoop: nThreads = " + nThreads);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Verteilt die einzelnen Indizes an anfragende Threads
     */
    class Verteiler {

        final static int FERTIG = 2147483647;

        int start;
        int stop;
        int schritt;
        int zaehler;

        /**
         * Erzeugt einen verteilzähler für den Verteil-Modus
         * @param start
         * @param stop
         * @param schritt
         */
        public Verteiler(int start, int stop, int schritt) {
            this.start = start;
            this.stop = stop;
            this.schritt = schritt;
            this.zaehler = start - schritt;

            //stop wird verschoben, falls schritt die differenz zwischen
            //start und stop nicht teilt
            int spanne = stop - start;
            int rest = spanne % schritt;
            this.stop -=rest;
            if (rest != 0) {
                System.err.println("FEHLER: ForLoop: schritt ist nicht Teiler von stop - start!");
                System.err.println("FEHLER: ForLoop: => stop wird nicht erreicht!");
            }
        }

        /**
         * Gibt den nächsten zu bearbeitenden Index zurück, oder FERTIG,
         * falls alle indizes bearbeitet wurden.
         * @return
         */
        public synchronized int getNext() {
            //Prüfen ob weiter gezählt werden kann und machen
            //wenn erfolgreich gleich das ergebnis zurück geben
            if (start <= stop) {
                if (zaehler < stop) {
                    zaehler += schritt;
                    return zaehler;
                }
            } else {
                if (zaehler > stop) {
                    zaehler += schritt;
                    return zaehler;
                }
            }
            //Wenn nicht mehr weiter gezählt wurde, dann wird hier FERTIG
            //zurück gegeben
            return FERTIG;
        }


    }

    /**
     * Die Anweisungen in Loop werden auf nThreads Threads verteilt ausgeführt.
     * @param start
     * @param stop
     * @param schritt
     * @param nThreads
     */
    private void Init_Normal(int start, int stop, int schritt, int nThreads) {
        //Kontrollieren, ob die zahlen zusammen passen
        if (!SchrittKontrolle(start, stop, schritt)) return;

        //Kontrollieren, ob die Anzahl der Threads Positiv ist
        if (!ThreadAnzahlKontrolle(nThreads)) return;
        //Die gesamte differenz zwischen start und stop wird auf
        //nThreads viele Abschnitte verteilt.
        int spanne = stop - start;
        spanne = (int) (spanne + Math.signum(spanne));
        if (start == stop) spanne = 1;
        //wenn es zu viele Threads gibt, dann anzahl reduzieren
        if (Math.abs(spanne) < nThreads) nThreads = Math.abs(spanne);
        int spanne_pro_thread = spanne / nThreads;
        if (start < stop && spanne_pro_thread < 1) spanne_pro_thread = 1;
        if (start > stop && spanne_pro_thread > -1) spanne_pro_thread = -1;
        int[] teil_start = new int[nThreads];
        int[] teil_stop = new int[nThreads];
        for (int i=0;i<nThreads;i++) {
            teil_start[i] = start+spanne_pro_thread*i;
            if (start < stop)
                teil_stop[i] = start+spanne_pro_thread*(i+1)-1;
            else
                teil_stop[i] = start+spanne_pro_thread*(i+1)+1;
            //System.out.println(teil_start[i] + " - " + teil_stop[i]);
        }
        //wenn die Anzahl der Threads nicht Teiler der Spanne ist stimmt
        //das Ende nicht, also anpassen
        teil_stop[nThreads-1] = stop;
        //System.out.println(teil_start[nThreads-1] + " - " + teil_stop[nThreads-1]);

        //Die Threads erzeugen und starten
        LoopThread[] lts = new LoopThread[nThreads];
        for (int i=0;i<nThreads;i++) {
            lts[i] = new LoopThread(teil_start[i],teil_stop[i],schritt);
            lts[i].start();
        }

        //Darauf warten, das alle fertig werden
        for (int i=0;i<nThreads;i++) {
            try {
            lts[i].join();
            } catch (InterruptedException ex) {ex.printStackTrace();}
        }

    }

    /**
     * Gibt true zurück, wenn die Parameter ok sind
     * @param start
     * @param stop
     * @param schritt
     * @return
     */
    private boolean SchrittKontrolle(int start, int stop, int schritt) {
        if (start < stop && schritt < 1) {
            System.err.println("FEHLER: ForLoop: start < stop und schritt < 1");
            System.err.println("FEHLER: start = " + start + ", stop = " + stop + ", schritt = " + schritt);
            return false;
        }
        if (start > stop && schritt > -1) {
            System.err.println("FEHLER: ForLoop: start > stop und schritt > -1");
            System.err.println("FEHLER: start = " + start + ", stop = " + stop + ", schritt = " + schritt);
            return false;
        }
        return true;
    }

    class LoopThreadVerteilen extends Thread {

        /**
         * Zeiger auf den gemeinsamen verteiler
         */
        private Verteiler verteiler;

        public LoopThreadVerteilen(Verteiler verteiler) {
            this.verteiler = verteiler;
        }

        @Override
        public void run() {
            int index;
            //Solange weiter machen, bis der verteiler FERTIG meldet
            while ((index = verteiler.getNext()) != Verteiler.FERTIG) {
                Loop(index);
            }
        }

    }

    class LoopThread extends Thread {

        int lt_start;
        int lt_stop;
        int lt_schritt;

        /**
         * Die Aufgabe in Loop wird von mehreren LoopThreads bearbeitet,
         * jeder übernimmt einen in den Parametern angegebenen Bereich.
         * @param start
         * @param stop
         * @param schritt
         */
        LoopThread(int start, int stop, int schritt) {
            lt_start = start;
            lt_stop = stop;
            lt_schritt = schritt;
        }

        @Override
        public void run() {
            //System.out.println("lt_start: "+ lt_start + " lt_stop: "+ lt_stop);
            if (lt_start < lt_stop) {
                for (int index = lt_start; index <= lt_stop; index += lt_schritt) {
                    Loop(index);
                }
            } else if (lt_start > lt_stop) {
                for (int index = lt_start; index >= lt_stop; index += lt_schritt) {
                    Loop(index);
                }
            }
            if (lt_start == lt_stop) {
                Loop(lt_start);
            }
        }

    }

    /**
     * Loop enthält den auszuführenden Code und muss bei der
     * erzeugung überschrieben werden.
     * @param i
     */
    abstract public void Loop(int i);

}
