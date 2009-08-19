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
     * @param reader	the reader with the PDF
     */
    public TextExtractor(PdfReader reader) {
        this.reader = reader;
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


}
