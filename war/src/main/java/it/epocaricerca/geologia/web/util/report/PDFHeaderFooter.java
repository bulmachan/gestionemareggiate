package it.epocaricerca.geologia.web.util.report;

import java.io.IOException;
import java.net.MalformedURLException;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class PDFHeaderFooter extends PdfPageEventHelper {

	
    /** Current page number (will be reset for every chapter). */
    private int pagenumber;
    
    private Image rerLogo;
    
    Font fontIntestazione = FontFactory.getFont("Arial", 9, Font.NORMAL,BaseColor.BLACK);
    
    public PDFHeaderFooter() throws BadElementException, MalformedURLException, IOException
    {
    	rerLogo = Image.getInstance(this.getClass().getResource("/images/logo_RER.png"));
		rerLogo.scaleToFit(198, 22);
		rerLogo.setAlignment(Image.MIDDLE);
    }
    
    /**
     * Initialize one of the headers.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onOpenDocument(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onOpenDocument(PdfWriter writer, Document document) {
    }
    
    /**
     * Initialize one of the headers, based on the chapter title;
     * reset the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onChapter(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document, float,
     *      com.itextpdf.text.Paragraph)
     */
    public void onChapter(PdfWriter writer, Document document,
            float paragraphPosition, Paragraph title) {
        pagenumber = 1;
    }

    /**
     * Increase the page number.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onStartPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onStartPage(PdfWriter writer, Document document) {
        pagenumber++;
        
        Rectangle rect = writer.getBoxSize("header");
        
        
        rerLogo.setAbsolutePosition( (rect.getLeft() + rect.getRight()) / 2 - rerLogo.getScaledWidth()/2, rect.getTop());
        try {
			writer.getDirectContent().addImage(rerLogo);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       
        
        Chunk intestazione1 = new Chunk("Giunta Regionale\n", fontIntestazione);
		Chunk intestazione2 = new Chunk("Direzione Generale Ambiente e Difesa del suolo e della costa", fontIntestazione);
		
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(intestazione1),
                (rect.getLeft() + rect.getRight()) / 2, rect.getTop() -18, 0);
        
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(intestazione2),
                (rect.getLeft() + rect.getRight()) / 2, rect.getTop() -30, 0);
    }
    
    /**
     * Adds the header and the footer.
     * @see com.itextpdf.text.pdf.PdfPageEventHelper#onEndPage(
     *      com.itextpdf.text.pdf.PdfWriter, com.itextpdf.text.Document)
     */
    public void onEndPage(PdfWriter writer, Document document) {
        Rectangle rect = writer.getBoxSize("footer");
       
        ColumnText.showTextAligned(writer.getDirectContent(),
                Element.ALIGN_CENTER, new Phrase(String.format("%d", pagenumber)),
                (rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18, 0);
    }
}
