package jsslib.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Locale;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDataFormatter;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator.CellValue;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 *
 * @author robert schuster
 */
public class ExcelReader {

    /**
     * Zellen könnten Formeln enthalten, die ausgewertet werden müssen
     */
    private HSSFFormulaEvaluator evaluator;

    /**
     * Workbook-Objekt für den zugriff auf den Datei-Inhalt
     */
    private HSSFWorkbook wb;

    private int FehlerStatus = 0;

    /**
     * Erzeugt ein Reader-Objekt mit der Übergebenen Excel-Datei
     * @param datei
     */
    public ExcelReader(String datei) {
        //Prüfen ob es sich um eine Excel 2007 Datei handelt.
        if (datei.contains(".xlsx")) {
            System.out.println("Excel 2007/2008 Dateien sind noch nicht unterstützt!");
            FehlerStatus = 1;
            return;
        }

        //Kann die Datei zum lesen geöffnet werden?
        File quelle = new File(datei);
        if (!quelle.canRead()) {
            System.out.println(quelle.getName() + " kann nicht geöffnet werden!\n");
            FehlerStatus = 2;
            return;
        }

        //Excel-Datei komplett einlesen
        //neues Workbook erstellen
        wb = null;
        try {
            InputStream inp = new FileInputStream(quelle);
            wb = new HSSFWorkbook(inp);
            inp.close();
        } catch (Exception e) {
            System.out.println(quelle.getName() + " kann nicht eingelesen werden. Handelt es sich um eine Excel-Datei?\n");
            FehlerStatus = 3;
            return;
        }

        //Falls Formeln ausgewertet werden müssen
        evaluator = new HSSFFormulaEvaluator(wb);
    }

    /**
     * Gibt den FehlerStatus zurück
     * @return  1 = .xlsx datei
     *          2 = datei nicht lesbar
     *          3 = kein korrektes Excel-Format
     */
    public int getFehlerStatus() {
        return FehlerStatus;
    }

    /**
     * Gibt die Anzahl der Tabellen zurück
     * @return
     */
    public int getNumberOfSheets() {
        return wb.getNumberOfSheets();
    }

    /**
     * Gibt den Namen der Tabelle an der Stelle index zurück
     * @param index 0 = erste Tabelle
     * @return
     */
    public String getSheetName(int index) {
        return wb.getSheetName(index);
    }

    /**
     * Gibt die Anzahl der Zeilen in einer Tabelle zurück
     * Achtung! Es können auch zeilen dabei sein, die null sind
     * @param tabelle
     * @return
     */
    public int getRowCount(int tabelle) {
        return wb.getSheetAt(tabelle).getLastRowNum()+1;
    }

    /**
     * Gibt eine Zeile der Tabelle als String zurück
     * @param tabelle Der Index der Tabelle
     * @param zeile Der Index der Zeile
     * @param format Zahl = feste Breite, Zeichen = Trennzeichen
     * @return "" wenn die Zeile leer ist, Sonst den inhalt der Zeile
     */
    public String getRow(int tabelle, int zeile, Object format) {
        String ergebnis = "";
        HSSFRow row = wb.getSheetAt(tabelle).getRow(zeile);

        //Wird gebraucht um festzustellen, pb es sich um ein Datum handeln könnte
        HSSFDataFormatter formater = new HSSFDataFormatter();

        //format-anweisungen umsetzen
        String formatvorn = "";
        String formathinten = "";

        //Formatierung festlegen
        if (format instanceof Integer) {
            //Feste breite
            formatvorn = "%" + format;
        } else if (format instanceof String) {
            //Trennzeichen
            formatvorn = "%";
            formathinten = format.toString();
        } else {
            return "FEHLER: Der Parameter format ist ungültig! Es muss eine Zahl oder ein String sein!";
        }

        if (row != null) {
            for (int x=0;x<=row.getLastCellNum();x++) {
                HSSFCell zelle = row.getCell(x);
                String text = "";

                if (zelle != null) {
                    CellValue wert = evaluator.evaluate(zelle);

                    if (wert != null) {
                        switch (wert.getCellType()) {
                            case HSSFCell.CELL_TYPE_BLANK:
                                text = String.format(formatvorn + "s" + formathinten, "");
                                break;
                            case HSSFCell.CELL_TYPE_BOOLEAN:
                                text = String.format(formatvorn + "b" + formathinten, wert.getBooleanValue());
                                break;
                            case HSSFCell.CELL_TYPE_ERROR:
                                text = String.format(formatvorn + "s" + formathinten, "ERROR");
                                break;
                            case HSSFCell.CELL_TYPE_NUMERIC:
                                //prüfen, ob es sich um ein Spezielles Format handelt
                                Format dataformat = formater.createFormat(zelle);
                                if (dataformat != null) {
                                    //datum selbst formatieren
                                    //if (dataformat instanceof SimpleDateFormat) {
                                    //    text = String.format(formatvorn + "tF" + formathinten, zelle.getDateCellValue());
                                    //} else {
                                        text = String.format(formatvorn + "s" + formathinten, formater.formatCellValue(zelle,evaluator));
                                    //}
                                } else
                                    //kein besonderes Format
                                    text = String.format(Locale.ENGLISH, formatvorn + "f" + formathinten, wert.getNumberValue());
                                break;
                            case HSSFCell.CELL_TYPE_STRING:
                                text = String.format(formatvorn + "s" + formathinten, wert.getStringValue());
                                break;
                        }
                    } else {
                        text = String.format(formatvorn + "s" + formathinten, "NULL");
                    }
                    
                } else {
                    text = String.format(formatvorn + "s" + formathinten, "");
                }
                ergebnis += text;
            }
        }

        return ergebnis;
    }

}
