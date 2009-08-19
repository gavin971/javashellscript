/*
 * Diese Klasse ist von com.lowagie.text.pdf.parser.PdfTextExtractor abgeleitet
 * und erweitert diese um Tabellen besser aus pdf-Datei exportieren zu können
 */

package jsslib.pdf;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author robert schuster
 */
public class TextExtractor {

	/** The PdfReader that holds the PDF file. */
    private final PdfReader reader;
    /** The processor that will extract the text. */
    private final TextExtractingPdfContentStreamProcessor extractionProcessor;

    /**
     * Creates a new Text Extractor object.
     * @param datei	die PDF-datei
     * @throws IOException falls die Datei nicht geöffnet werden kann
     */
    public TextExtractor(String datei) throws IOException {
        this.reader = new PdfReader(datei);
        extractionProcessor = new TextExtractingPdfContentStreamProcessor();
    }

    /**
     * Gets the content stream of a page.
     * @param pageNum	the page number of page you want get the content stream from
     * @return	a byte array with the content stream of a page
     * @throws IOException
     */
    private byte[] getContentBytesForPage(int pageNum) throws IOException {
        RandomAccessFileOrArray f = reader.getSafeFile();
        byte[] contentBytes = reader.getPageContent(pageNum, f);
        f.close();
        return contentBytes;
    }

    /**
     * Gets the text from a page.
     * @param page	the page number of the page
     * @return	a String with the content as plain text (without PDF syntax)
     * @throws IOException
     */
    public String getTextFromPage(int page) throws IOException {
        PdfDictionary pageDic = reader.getPageN(page);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        extractionProcessor.processContent(getContentBytesForPage(page), resourcesDic);
        return (String) extractionProcessor.getResultantText(TextExtractingPdfContentStreamProcessor.RETURN_STRING);
    }

    public ArrayList<String[]> getTextFromPageAsList(int page) throws IOException {
        PdfDictionary pageDic = reader.getPageN(page);
        PdfDictionary resourcesDic = pageDic.getAsDict(PdfName.RESOURCES);
        extractionProcessor.processContent(getContentBytesForPage(page), resourcesDic);
        return (ArrayList<String[]>) extractionProcessor.getResultantText(TextExtractingPdfContentStreamProcessor.RETURN_LIST);
    }

    /**
     * Gibt das Reader-Objekt zurück
     * @return
     */
    public PdfReader getPdfReader() {
        return reader;
    }

    /**
     * Gibt die Anzahl der Seiten in der PDF-Datei zurück
     * @return
     */
    public int getAnzahlDerSeiten() {
        return reader.getNumberOfPages();
    }
}
