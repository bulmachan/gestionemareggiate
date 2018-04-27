package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.AllegatoManager;
import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.IndirizzoManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Indirizzo;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.JSFUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.report.PDFHeaderFooter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class ReportSTBController {

	private static Logger logger = Logger.getLogger(ReportSTBController.class);

	private RelazioneSTBManager relazioneSTBManager;
	private DannoSTBManager dannoSTBManager;
	private RelazioneGeneraleSTBManager stbGeneraleManager;
	
	private long itemToAct;

	private RelazioneSTB relazioneSTB;
	private RelazioneGeneraleSTB relazioneGeneraleSTB;
	private boolean relazioneTecnica = false;

	private Font fontText = FontFactory.getFont("Arial", 10, Font.NORMAL,BaseColor.BLACK);
	private Font fontTextBold = FontFactory.getFont("Arial", 10, Font.BOLD,BaseColor.BLACK);
	private DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy", Locale.ITALY);
	private NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.ITALY);
	
	private List<Indirizzo> indirizzi;

	//Form
	private String title;
	private Map<Indirizzo, Boolean> checkedIndirizzi = new HashMap<Indirizzo, Boolean>();
	private String responsabile;
	
	private boolean formMessageRendered = false;
	private String formMessage;

	public String nuovoReport() throws EntityNotFoundException
	{

		cleanForm();
		responsabile = title+" "+JSFUtils.getRequestHeaderByKey("nome") +" "+JSFUtils.getRequestHeaderByKey("cognome");
		relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		dannoSTBManager = (DannoSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.DannoSTBManagerName, JNDIUtils.DannoSTBManagerName);

		stbGeneraleManager = JNDIUtils.getRelazioneGeneraleSTBManager();
		
		if(relazioneTecnica)
			relazioneSTB = relazioneSTBManager.findItemById(itemToAct);
		else
		{
			relazioneGeneraleSTB = stbGeneraleManager.findItemById(itemToAct);
			this.relazioneGeneraleSTB.setRelazioniSTB(stbGeneraleManager.getRelazioniSTB(itemToAct));
		}

		return "report";
	}

	public String generaReportItem() throws DocumentException, MalformedURLException, IOException, EntityNotFoundException{

		if(checkFormFields()){

			File temp = File.createTempFile("report_"+itemToAct, ".pdf");
			//File temp = new File("/Users/damiano/Desktop/test_pdf.pdf");
			FileOutputStream file = new FileOutputStream(temp);

			Document document = new Document( PageSize.A4, 36, 36, 120, 36 );

			PDFHeaderFooter event = new PDFHeaderFooter();


			PdfWriter writer = PdfWriter.getInstance(document, file);
			writer.setBoxSize("header", new Rectangle(36, 54, 559, 788));
			writer.setBoxSize("footer", new Rectangle(36, 54, 559, 788));
			writer.setPageEvent(event);
			document.open();
			
			
			
			// prima pagina
			
			String stb = "";
			if(relazioneTecnica)
				stb = relazioneSTB.getStb().getNome();
			else
				stb = "";


			Chunk responsabile1 = new Chunk(stb, fontTextBold);
			Chunk responsabile2 = new Chunk("Il Responsabile del Servizio", fontTextBold);
			Chunk responsabile3 = new Chunk(this.title+" "+responsabile, fontText);
			Paragraph responsabileParagraph = new Paragraph();
			responsabileParagraph.setSpacingAfter(23);
			responsabileParagraph.setAlignment(Element.ALIGN_LEFT);
			responsabileParagraph.add(responsabile1);
			responsabileParagraph.add(Chunk.NEWLINE);
			responsabileParagraph.add(responsabile2);
			responsabileParagraph.add(Chunk.NEWLINE);
			responsabileParagraph.add(responsabile3);


			document.add(responsabileParagraph);


			Paragraph indirizziParagraph = new Paragraph();
			indirizziParagraph.setAlignment(Element.ALIGN_RIGHT);
			indirizziParagraph.setSpacingAfter(26);

			if(checkedIndirizzi != null){
				Set<Indirizzo> indirizzi = this.checkedIndirizzi.keySet();
				for (Indirizzo indirizzo : indirizzi) {
					if(this.checkedIndirizzi.get(indirizzo)){
						Chunk indirizzoChunck = new Chunk(indirizzo.getNome()+"\n"+indirizzo.getIndirizzo(),fontText);
						indirizziParagraph.add(indirizzoChunck);
						indirizziParagraph.add(Chunk.NEWLINE);
						indirizziParagraph.add(Chunk.NEWLINE);
					}
				}

				document.add(indirizziParagraph);
			}
			
			String descrizione = "";
			if(relazioneTecnica) {
				/* stampa del codice della relazione */ 
				if (relazioneSTB.getCodice() != null)  {
					Chunk codiceChunk = new Chunk("Relazione tecnica: " + relazioneSTB.getCodice(), fontText);
					Paragraph codiceParagraph = new Paragraph();
					codiceParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
					codiceParagraph.setSpacingAfter(26);
					codiceParagraph.add(codiceChunk);
					codiceParagraph.add(Chunk.NEWLINE);
					codiceParagraph.add(Chunk.NEWLINE);
					document.add(codiceParagraph);
				}
				descrizione = relazioneSTB.getDescrizione() != null ? relazioneSTB.getDescrizione() : "" ;
			}
			else {
				descrizione = relazioneGeneraleSTB.getInformazioniGenerali() +"\n"+relazioneGeneraleSTB.getInformazioniMeteo();
			}

			Chunk descrizioneChunk = new Chunk(descrizione, fontText);
			Paragraph descrizioneParagraph = new Paragraph();
			descrizioneParagraph.setAlignment(Element.ALIGN_JUSTIFIED);
			descrizioneParagraph.setSpacingAfter(26);
			descrizioneParagraph.add(descrizioneChunk);
			descrizioneParagraph.add(Chunk.NEWLINE);
			descrizioneParagraph.add(Chunk.NEWLINE);
			descrizioneParagraph.add(new Chunk(dateFormat.format(new Date()),fontText));



			document.add(descrizioneParagraph);

			Chunk responsabile4 = new Chunk(stb, fontText);
			Chunk responsabile5 = new Chunk("Il Responsabile del Servizio", fontText);
			Chunk responsabile6 = new Chunk("("+this.title+" "+responsabile+")", fontText);
			Paragraph firma = new Paragraph();

			firma.setAlignment(Element.ALIGN_RIGHT);
			firma.add(responsabile4);
			firma.add(Chunk.NEWLINE);
			firma.add(responsabile5);
			firma.add(Chunk.NEWLINE);
			firma.add(responsabile6);

			document.add(firma);


			// seconda pagina

			List<DannoSTB> danniStbs = null;
			if(relazioneSTBManager!= null)
				danniStbs = relazioneSTBManager.getDanniSTB(relazioneSTB.getId());
			else
				danniStbs = this.getRelazioneSTB().getDanni();
			
			if(danniStbs.size()>0){
			
				document.add(Chunk.NEXTPAGE);
	
				if(relazioneTecnica)
					generaPdf(relazioneSTB,document);
				else
				{
					List<RelazioneSTB> relazioniSTB = relazioneGeneraleSTB.getRelazioniSTB();
					for (RelazioneSTB relazioneSTB : relazioniSTB) {
						generaPdf(relazioneSTB,document);
						document.add(Chunk.NEXTPAGE);
					}
				}
			}
			
			
			
			document.close();
			
			AttachmentUtils.downloadFile(temp);


		}

		return "";
	}	

	public void generaPdf(RelazioneSTB relazioneSTB, Document document) throws DocumentException, MalformedURLException, IOException, EntityNotFoundException
	{
		logger.info("Genera Report PDF per relazione tecnica "+relazioneSTB.getId());

	

		//TODO ordinare per comune

		List<DannoSTB> danniStbs = null;
		if(relazioneSTBManager!= null)
			danniStbs = relazioneSTBManager.getDanniSTB(relazioneSTB.getId());
		else
			danniStbs = this.getRelazioneSTB().getDanni();

		for (DannoSTB dannoSTB : danniStbs) {
			Chunk comune = new Chunk("Comune di "+dannoSTB.getLocalita().getComune().getNome()+"\n", fontTextBold);
			comune.setUnderline(0.1f, -2f);

			Chunk localita = new Chunk(dannoSTB.getLocalita().getNome()+"\n", fontTextBold);
			Chunk descrizione = new Chunk(dannoSTB.getDescrizione()+"\n", fontText);

			Paragraph localitaParagraph = new Paragraph();
			localitaParagraph.add(localita);
			localitaParagraph.setAlignment(Element.ALIGN_CENTER);


			document.add(comune);
			document.add(localitaParagraph);
			if(dannoSTB.getDescrizione() != null)
				document.add(descrizione);
			document.add(Chunk.NEWLINE);
			
			
			//Sezione allegati immagini
			
			List<Allegato> allegati = null;
			if(dannoSTBManager != null)
				allegati = dannoSTBManager.getAllegati(dannoSTB.getId());
			else
				allegati = dannoSTB.getAllegati();

			for (Allegato allegato : allegati) {
				if(allegato.getTipo().getNome().equalsIgnoreCase("immagine"))
				{
					Image img;
					if(allegato.getFile() != null)
					{
						img = Image.getInstance(allegato.getFile());
					}else
					{
						AllegatoManager allegatoManager = JNDIUtils.retrieveEJB(JNDIUtils.AllegatoManagerName, JNDIUtils.AllegatoManagerName);

						img = Image.getInstance( allegatoManager.getAllegatoAttachment(allegato.getId()));
					}

					img.scaleToFit(380, 280);
					img.setAlignment(Image.MIDDLE);

					
					String nomeAllegato = allegato.getNome().lastIndexOf('.') != -1 ? allegato.getNome().substring(0, allegato.getNome().lastIndexOf('.')) : allegato.getNome();

					Paragraph imgParagraph = new Paragraph();
					imgParagraph.setAlignment(Element.ALIGN_CENTER);
					imgParagraph.setSpacingAfter(30);
					imgParagraph.add(new Chunk(img,0,0,true));
					imgParagraph.add(Chunk.NEWLINE);
					imgParagraph.add(new Chunk(nomeAllegato,fontText));
					
					
					document.add(imgParagraph);
					
				}
			}
			
			

		}
		
		if(danniStbs.size()>0){
			Paragraph stimaDanniParagraph = new Paragraph();
			stimaDanniParagraph.setSpacingAfter(10);
			stimaDanniParagraph.add(new Chunk("COMPUTO DEI DANNI E RICHIESTA DI FINANZIAMENTO.",fontText));
		
			document.add(stimaDanniParagraph);
			
			
			//sezione costi danni
			PdfPTable tbl = new PdfPTable(5);
			tbl.setWidths(new int[]{2,4,2,2,2});
			tbl.setSpacingBefore(3);
			tbl.setWidthPercentage(100);
			
	
			PdfPCell cell_localita = new PdfPCell(new Phrase("LOCALITA'", fontTextBold));
			cell_localita.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_localita.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_localita);
			
			
			PdfPCell cell_descrizione = new PdfPCell(new Phrase("DESCRIZIONE DANNI", fontTextBold));
			cell_descrizione.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_descrizione.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_descrizione);
			
			PdfPCell cell_danni = new PdfPCell(new Phrase("DANNI", fontTextBold));
			cell_danni.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_danni.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_danni);
			
			PdfPCell cell_interventi = new PdfPCell(new Phrase("INTERVENTI PROGRAMMATI", fontTextBold));
			cell_interventi.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_interventi.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_interventi);
			
			
			PdfPCell cell_richiesta = new PdfPCell(new Phrase("RICHIESTA (IVA incl.)", fontTextBold));
			cell_richiesta.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_richiesta.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_richiesta);
		
		
			float totale_danni = 0;
			
			for (DannoSTB dannoSTB : danniStbs) {
				
				totale_danni += dannoSTB.getStimaCostiDanni();
				
				PdfPCell cell_localita_text = new PdfPCell(new Phrase(dannoSTB.getLocalita().getNome(), fontText));
				cell_localita_text.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_localita_text.setVerticalAlignment(Element.ALIGN_TOP);
				tbl.addCell(cell_localita_text);
				
				
				PdfPCell cell_descrizione_text = new PdfPCell(new Phrase(dannoSTB.getDescrizione(), fontText));
				cell_descrizione_text.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_descrizione_text.setVerticalAlignment(Element.ALIGN_TOP);
				tbl.addCell(cell_descrizione_text);
				
				
				
			
				PdfPCell cell_danni_text = new PdfPCell(new Phrase(	currency.format(dannoSTB.getStimaCostiDanni()), fontText));
				cell_danni_text.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_danni_text.setVerticalAlignment(Element.ALIGN_TOP);
				tbl.addCell(cell_danni_text);
				
				PdfPCell cell_interventi_text = new PdfPCell(new Phrase("", fontTextBold));
				cell_interventi_text.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_interventi_text.setVerticalAlignment(Element.ALIGN_TOP);
				tbl.addCell(cell_interventi_text);
				
				
				PdfPCell cell_richiesta_text = new PdfPCell(new Phrase("", fontTextBold));
				cell_richiesta_text.setHorizontalAlignment(Element.ALIGN_CENTER);
				cell_richiesta_text.setVerticalAlignment(Element.ALIGN_TOP);
				tbl.addCell(cell_richiesta_text);
				
			}
			
			PdfPCell cell_localita_text = new PdfPCell(new Phrase("", fontText));
			cell_localita_text.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_localita_text.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_localita_text);
			
			
			PdfPCell cell_descrizione_text = new PdfPCell(new Phrase("TOTALE", fontText));
			cell_descrizione_text.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_descrizione_text.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_descrizione_text);
			
			
			
		
			PdfPCell cell_danni_text = new PdfPCell(new Phrase(	currency.format(totale_danni), fontText));
			cell_danni_text.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_danni_text.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_danni_text);
			
			PdfPCell cell_interventi_text = new PdfPCell(new Phrase("", fontTextBold));
			cell_interventi_text.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_interventi_text.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_interventi_text);
			
			
			PdfPCell cell_richiesta_text = new PdfPCell(new Phrase("", fontTextBold));
			cell_richiesta_text.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell_richiesta_text.setVerticalAlignment(Element.ALIGN_TOP);
			tbl.addCell(cell_richiesta_text);
			
			
	
			document.add(tbl);
			
		}
		

		


	}


	private void cleanForm()
	{
		this.checkedIndirizzi.clear();
		this.title = "";
		this.formMessageRendered = false;
		this.formMessage = "";
	}

	private boolean checkFormFields() {

		boolean isOneAddressSelected = false;

		if(checkedIndirizzi != null){
			Set<Indirizzo> indirizzi = this.checkedIndirizzi.keySet();
			for (Indirizzo indirizzo : indirizzi) {
				if(this.checkedIndirizzi.get(indirizzo)){
					isOneAddressSelected = true;
				}
			}
		}
		
		if(isOneAddressSelected == false)
		{
			formMessageRendered = true;
			formMessage = Messages.FORM_ADDRESS_MISSING;
			return false;
		}

		formMessage = "";
		formMessageRendered = false;
		return true;
	}

	public long getItemToAct() {
		return itemToAct;
	}


	public void setItemToAct(long itemToAct) {
		this.itemToAct = itemToAct;
	}

	public RelazioneSTB getRelazioneSTB() {
		return relazioneSTB;
	}

	public void setRelazioneSTB(RelazioneSTB relazioneSTB) {
		this.relazioneSTB = relazioneSTB;
	}

	public String getResponsabile() {
		return responsabile;
	}

	public void setResponsabile(String responsabile) {
		this.responsabile = responsabile;
	}

	public Map<Indirizzo, Boolean> getCheckedIndirizzi() {
		return checkedIndirizzi;
	}

	public void setCheckedIndirizzi(Map<Indirizzo, Boolean> checkedIndirizzi) {
		this.checkedIndirizzi = checkedIndirizzi;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public List<Indirizzo> getIndirizzi() {
		if (null == this.indirizzi || this.indirizzi .isEmpty() )
			updateIndirizzi();
		return this.indirizzi;
	}

	private void updateIndirizzi() {
		IndirizzoManager indirizzoManager = JNDIUtils.retrieveEJB(JNDIUtils.IndirizzoManagerName, JNDIUtils.IndirizzoManagerName);
		this.indirizzi = indirizzoManager.selectAll();

	}

	public boolean isFormMessageRendered() {
		return formMessageRendered;
	}

	public void setFormMessageRendered(boolean formMessageRendered) {
		this.formMessageRendered = formMessageRendered;
	}

	public String getFormMessage() {
		return formMessage;
	}

	public void setFormMessage(String formMessage) {
		this.formMessage = formMessage;
	}

	public boolean isRelazioneTecnica() {
		return relazioneTecnica;
	}

	public void setRelazioneTecnica(boolean relazioneTecnica) {
		this.relazioneTecnica = relazioneTecnica;
	}

	public RelazioneGeneraleSTB getRelazioneGeneraleSTB() {
		return relazioneGeneraleSTB;
	}

	public void setRelazioneGeneraleSTB(RelazioneGeneraleSTB relazioneGeneraleSTB) {
		this.relazioneGeneraleSTB = relazioneGeneraleSTB;
	}

}
