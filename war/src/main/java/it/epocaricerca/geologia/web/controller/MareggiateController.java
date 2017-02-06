package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.AllegatoManager;
import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.PagingManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.MareggiateControllerService;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.MareggiataBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.model.MareggiateWizardStep;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.JSFUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.report.PDFHeaderFooter;
import it.epocaricerca.geologia.web.validator.FileValidator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

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
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabSettings;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

public class MareggiateController extends FilterableHandler<Mareggiata> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4630772132553975495L;

	private static Logger logger = Logger.getLogger(MareggiateController.class);

	private boolean editMode = false;
	private boolean saveEnabled = false;

	private PagingManager pagingManager = JNDIUtils.getPagingManager();

	private Mareggiata selectedItem;
	private long idDetail;
	private Long itemToDelete;

	private long itemToAct;
	
	//MESSAGGI DI ERRORE
	private boolean mareggiataFormMessageRendered = false;
	private String mareggiataFormMessage;

	//WIZARD STATO
	private MareggiateWizardStep step;

	// FORM SELEZIONE
	private MareggiataBean mareggiataBeanEdit;

	private List<PrevisioneMeteo> filtered_previsioniMeteo;
	private List<PrevisioneImpatto> filtered_previsioniImpatti;
	private List<ImpattoReale> filtered_impattiReali;
	private List<CondizioneMeteo> filtered_condizioniMeteo;
	private List<RelazioneSTB> filtered_relazioniSTB;

	private Map<Long, Boolean> checkedRelazioniSTB = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> checkedPrevisioniMeteo = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> checkedImpattiReali = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> checkedCondizioniMeteo = new HashMap<Long, Boolean>();
	private Map<Long, Boolean> checkedPrevisioniImpatti = new HashMap<Long, Boolean>();


	// FORM GESTIONE ALLEGATI

	//Allegati
	private List<Allegato> allegatiTemp;
	private Map<String, TipoAllegato> tipiAllegato;
	private List<SelectItem> tipiAllegatoSelect;
	private String fileToClear;
	//private String selectedTipoAllegato;
	private Allegato attachmentToDownload;
	private Allegato attachmentToRemove;
	private List<Allegato> attachmentsToRemoveList;

	private FileUploadBean allegati;


	private static final String FAKE_PATH = "C:\\fakepath\\";
	private static final int MAX_UPLOADS = 10;
	

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;


	// PDF
	private Font fontText = FontFactory.getFont("Arial", 10, Font.NORMAL,BaseColor.BLACK);
	private Font fontTextBold = FontFactory.getFont("Arial", 10, Font.BOLD,BaseColor.BLACK);
	private Font fontTextBoldTitolo = FontFactory.getFont("Arial", 10, Font.BOLD,BaseColor.BLUE);
	private DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy", Locale.ITALY);
	private NumberFormat currency = NumberFormat.getCurrencyInstance(Locale.ITALY);
	
	
	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		updateDataModel();
	}

	public void filtraMareggiate() {
		logger.info("filtraMareggiate "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		updateDataModel();
	}

	private void cleanForm() {
		mareggiataFormMessageRendered = false;
		mareggiataBeanEdit = null;
		saveEnabled = false;
		checkedRelazioniSTB.clear();
		checkedPrevisioniMeteo.clear();
		checkedImpattiReali.clear();
		checkedCondizioniMeteo.clear();
		checkedPrevisioniImpatti.clear();
		
		if (null != tipiAllegato)
			tipiAllegato.clear();
		if (null != tipiAllegatoSelect)
			tipiAllegatoSelect.clear();
		
		this.allegati = new FileUploadBean(MAX_UPLOADS);

	}


	public String nuovaScheda() throws DocumentException, MalformedURLException, IOException, EntityNotFoundException{
		
		
		MareggiataManager mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerName, JNDIUtils.MareggiataManagerName);
		
		File temp = File.createTempFile("SchedaMareggiata_"+itemToAct, ".pdf");
		
		FileOutputStream file = new FileOutputStream(temp);

		Document document = new Document( PageSize.A4, 36, 36, 120, 36 );

		PDFHeaderFooter event = new PDFHeaderFooter();


		PdfWriter writer = PdfWriter.getInstance(document, file);
		writer.setBoxSize("header", new Rectangle(36, 54, 559, 788));
		writer.setBoxSize("footer", new Rectangle(36, 54, 559, 788));
		writer.setPageEvent(event);
		document.open();
		
		
		// DATI GENERALI
		
		Paragraph datiGeneraliParagraph = new Paragraph();
		datiGeneraliParagraph.setSpacingAfter(20);
		datiGeneraliParagraph.setAlignment(Element.ALIGN_LEFT);
		datiGeneraliParagraph.setTabSettings(new TabSettings(10f));
		
		datiGeneraliParagraph.add(new Chunk("DATI GENERALI", fontTextBold));
		datiGeneraliParagraph.add(Chunk.NEWLINE);		
		datiGeneraliParagraph.add(Chunk.NEWLINE);		
		datiGeneraliParagraph.add(Chunk.TABBING);
		datiGeneraliParagraph.add(new Chunk("Mareggiata "+selectedItem.getCodice(), fontTextBold));
		
		datiGeneraliParagraph.add(Chunk.NEWLINE);
		datiGeneraliParagraph.add(Chunk.TABBING);
		datiGeneraliParagraph.add(new Chunk("Evento dal ", fontTextBold));
		datiGeneraliParagraph.add(new Chunk(dateFormat.format(selectedItem.getInizioValidita()), fontText));
		datiGeneraliParagraph.add(new Chunk(" al ", fontTextBold));
		datiGeneraliParagraph.add(new Chunk(dateFormat.format(selectedItem.getFineValidita()), fontText));				

		// ALLEGATI PRIMA FOTO
		List<Allegato> allegati = selectedItem.getAllegati();
		int i=0;
		for (Allegato allegato : allegati) {
		
			if(allegato.getTipo().getNome().equalsIgnoreCase("immagine")){
				if(i<1){
					Image img;
					if(allegato.getFile() != null){
						img = Image.getInstance(allegato.getFile());
					} else {
						AllegatoManager allegatoManager = JNDIUtils.retrieveEJB(JNDIUtils.AllegatoManagerName, JNDIUtils.AllegatoManagerName);
						img = Image.getInstance( allegatoManager.getAllegatoAttachment(allegato.getId()));
					}
	
					img.scaleToFit(380, 280);
			
					//img.setAlignment(Image.MIDDLE);
	
					datiGeneraliParagraph.add(Chunk.NEWLINE);
					datiGeneraliParagraph.add(Chunk.NEWLINE);
					datiGeneraliParagraph.add(Chunk.TABBING);					
					datiGeneraliParagraph.add(new Chunk(img,0,0,true));
					datiGeneraliParagraph.add(Chunk.NEWLINE);
					
				}
				i++;
				
			}
		}
		// FINE ALLEGATI PRIMA FOTO		
		
		if(selectedItem.getDescrizione()!=null){
			datiGeneraliParagraph.add(Chunk.NEWLINE);
			datiGeneraliParagraph.add(Chunk.TABBING);
			datiGeneraliParagraph.add(new Chunk("Descrizione: ", fontTextBold));
			datiGeneraliParagraph.add(new Chunk(selectedItem.getDescrizione(), fontText));
		}

		
		document.add(datiGeneraliParagraph);
		
		// FINE DATI GENERALI





		
		

		// PREVISIONI
		Paragraph previsioniParagraph = new Paragraph();
		previsioniParagraph.setSpacingAfter(20);
		previsioniParagraph.setAlignment(Element.ALIGN_LEFT);
		previsioniParagraph.setTabSettings(new TabSettings(10f));
		        
		previsioniParagraph.add(Chunk.NEWLINE);
		previsioniParagraph.add(new Chunk("PREVISIONI", fontTextBold));

		/*this.selectedItem = (Mareggiata) dataModel.getRowData();

		
		//this.selectedItem.setPrevisioniMeteo(mareggiataManager.getPrevisioniMeteo(this.selectedItem.getId()));
		
		for (PrevisioneMeteo previsioneMeteo : mareggiataManager.getPrevisioniMeteo(this.selectedItem.getId())){
			previsioniParagraph.add(new Chunk("POLLO", fontTextBold));
		}*/
		
		
		// AVVISI METEO
		if(selectedItem.getPrevisioniMeteo().size()>0){
			previsioniParagraph.add(Chunk.NEWLINE);
			previsioniParagraph.add(Chunk.NEWLINE);
			previsioniParagraph.add(Chunk.TABBING);
			previsioniParagraph.add(new Chunk("Avvisi meteo di eventi costieri ("+selectedItem.getPrevisioniMeteo().size()+")", fontTextBold));
			
			//mareggiataManager.getPrevisioniMeteo(selectedItem.getId())
			
			for (PrevisioneMeteo previsioneMeteo : mareggiataManager.getPrevisioniMeteo(selectedItem.getId())){
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Avviso meteo: ", fontTextBold));
				previsioniParagraph.add(new Chunk(previsioneMeteo.getIdAvviso(), fontTextBold));
	
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Validità: dal ", fontTextBold));
				previsioniParagraph.add(new Chunk(dateFormat.format(previsioneMeteo.getInizioValidita()), fontText));
				previsioniParagraph.add(new Chunk(" al ", fontTextBold));
				previsioniParagraph.add(new Chunk(dateFormat.format(previsioneMeteo.getFineValidita()), fontText));
				
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Tendenza: ", fontTextBold));
				previsioniParagraph.add(new Chunk(previsioneMeteo.getTendenza().getNome(), fontText));
				
				if(previsioneMeteo.getDescrizione()!=null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Descrizione: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getDescrizione(), fontText));
				}
				
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Zona di pianura", fontTextBoldTitolo));
				
				if(Float.valueOf(previsioneMeteo.getPianuraVento()) != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Intensità del vento: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPianuraVento()+"", fontText));
				}
				if(previsioneMeteo.getPianuraDirezioneVento() != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Direzione del vento: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPianuraDirezioneVento().getNome(), fontText));
				}
				if(Float.valueOf(previsioneMeteo.getPianuraVentoMax()) != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Intensità massima: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPianuraVentoMax()+"", fontText));
				}
				
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Zona pedemontana", fontTextBoldTitolo));
				
				if(Float.valueOf(previsioneMeteo.getPedemontanaVento()) != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Intensità del vento: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPedemontanaVento()+"", fontText));
				}
				if(previsioneMeteo.getPedemontanaDirezioneVento() != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Direzione del vento: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPedemontanaDirezioneVento().getNome(), fontText));
				}
				if(Float.valueOf(previsioneMeteo.getPedemontanaVentoMax()) != null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Intensità massima: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getPedemontanaVentoMax()+"", fontText));
				}
				
				
				if(previsioneMeteo.getAltezzaStimataOnda()!=null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Stato del mare (aperto)", fontTextBoldTitolo));

					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Altezza stimata dell'onda (metri): ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneMeteo.getAltezzaStimataOnda().getNome(), fontText));
				}
				
				
				
				
				// QUESTO NON FUNZIONA:
				
				/*previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add("Eventi costieri ("+previsioneMeteo.getEventiCostieri().size()+")");
				
				
				for (EventoCostiero eventoCostiero : previsioneMeteo.getEventiCostieri()){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(eventoCostiero.getFenomeno().getNome());
				
				}
				*/	
				
				
			}
			
			
				
		}			
		// FINE AVVISI METEO	
			
		// VALUTAZIONI

		if(selectedItem.getPrevisioniImpatti().size()>0){
			previsioniParagraph.add(Chunk.NEWLINE);
			previsioniParagraph.add(Chunk.NEWLINE);
			previsioniParagraph.add(Chunk.TABBING);
			previsioniParagraph.add(new Chunk("Impatti ("+selectedItem.getPrevisioniImpatti().size()+")", fontTextBold));
			for (PrevisioneImpatto previsioneImpatto : selectedItem.getPrevisioniImpatti()){
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Impatto: ", fontTextBold));
				previsioniParagraph.add(new Chunk(previsioneImpatto.getCodice(), fontTextBold));
				
				previsioniParagraph.add(Chunk.NEWLINE);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(Chunk.TABBING);
				previsioniParagraph.add(new Chunk("Validità: dal ", fontTextBold));
				previsioniParagraph.add(new Chunk(dateFormat.format(previsioneImpatto.getInizioValidita()), fontText));
				previsioniParagraph.add(new Chunk(" al ", fontTextBold));
				previsioniParagraph.add(new Chunk(dateFormat.format(previsioneImpatto.getFineValidita()), fontText));

				if(previsioneImpatto.getDescrizione()!=null){
					previsioniParagraph.add(Chunk.NEWLINE);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(Chunk.TABBING);
					previsioniParagraph.add(new Chunk("Descrizione: ", fontTextBold));
					previsioniParagraph.add(new Chunk(previsioneImpatto.getDescrizione(), fontText));
				}				
				
				
			}
		}
		

		
		
		document.add(previsioniParagraph);
		// FINE PREVISIONI
			
			

		// REVISIONI
		Paragraph revisioniParagraph = new Paragraph();
		revisioniParagraph.setSpacingAfter(23);
		revisioniParagraph.setAlignment(Element.ALIGN_LEFT);
		revisioniParagraph.setTabSettings(new TabSettings(10f));
		
		revisioniParagraph.add(Chunk.NEWLINE);
		revisioniParagraph.add(new Chunk("REVISIONI", fontTextBold));	
		
		if(selectedItem.getCondizioniMeteo().size()>0){
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.TABBING);
			revisioniParagraph.add(new Chunk("Condizioni meteo marine ("+selectedItem.getCondizioniMeteo().size()+")", fontTextBold));
			
			for (CondizioneMeteo condizioneMeteo : selectedItem.getCondizioniMeteo()) {
				
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Validità: dal ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(condizioneMeteo.getInizioValidita()), fontText));
				revisioniParagraph.add(new Chunk(" al ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(condizioneMeteo.getFineValidita()), fontText));
				
				if(condizioneMeteo.getDescrizione()!=null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Descrizione: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getDescrizione(), fontText));
				}
	
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Estensione territoriale: ", fontTextBold));
				revisioniParagraph.add(new Chunk(condizioneMeteo.getEstensioneTerritoriale().getNome(), fontText));

				if(condizioneMeteo.getOnda()!=null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Provenienza onda: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getOnda().getNome(), fontText));
				}					
				if(condizioneMeteo.getDirezioneVentoPrevalente()!=null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Direzione vento prevalente: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getDirezioneVentoPrevalente().getNome(), fontText));
				}
				if(Float.valueOf(condizioneMeteo.getMaxIntensitaVentoPrevalente()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Massima intensità del vento: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getMaxIntensitaVentoPrevalente()+"", fontText));
				}
				if(Float.valueOf(condizioneMeteo.getMaxIntensitaVentoRaffica()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Massima intensità del vento (raffica): ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getMaxIntensitaVentoRaffica()+"", fontText));
				}
				if(Float.valueOf(condizioneMeteo.getMaxAltezzaMarea()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Massima altezza marea: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getMaxAltezzaMarea()+"", fontText));
				}
				if(Float.valueOf(condizioneMeteo.getMaxAltezzaOnda()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Massima altezza onda: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getMaxAltezzaOnda()+"", fontText));
				}
				if(Float.valueOf(condizioneMeteo.getMaxAltezzaOndaSignificativa()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Massima altezza onda significativa: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getMaxAltezzaOndaSignificativa()+"", fontText));
				}
				if(Float.valueOf(condizioneMeteo.getDurataSopraSoglia()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Durata sopra soglia critica: ", fontTextBold));
					revisioniParagraph.add(new Chunk(condizioneMeteo.getDurataSopraSoglia()+"", fontText));
				}		
			}
		
		}
		
		
		if(selectedItem.getImpattiReali().size()>0){
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.TABBING);
			revisioniParagraph.add(new Chunk("Impatti generali e locali ("+selectedItem.getImpattiReali().size()+")", fontTextBold));
			
			for (ImpattoReale impattoReale : selectedItem.getImpattiReali()){
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Impatto: ", fontTextBold));
				revisioniParagraph.add(new Chunk(impattoReale.getCodice(), fontTextBold));
	
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Validità: dal ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(impattoReale.getInizioValidita()), fontText));
				revisioniParagraph.add(new Chunk(" al ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(impattoReale.getFineValidita()), fontText));

				if(impattoReale.getDescrizione()!=null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Descrizione: ", fontTextBold));
					revisioniParagraph.add(new Chunk(impattoReale.getDescrizione(), fontText));
				}
				/*
				List<Danno> danni = impattoReale.getDanni();
				if(danni.size()>0){
					revisioniParagraph.add(Chunk.NEWLINE);
					
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(new Chunk("Tipologie di danni ("+danni.size()+")", fontText));					
					
					for (Danno danno : danni){
						revisioniParagraph.add(Chunk.NEWLINE);
						revisioniParagraph.add(Chunk.NEWLINE);
						revisioniParagraph.add(new Chunk("Macro area: ", fontTextBold));						
						revisioniParagraph.add(new Chunk(danno.getLocalita().getNome(), fontText));
						
					}
								
				}*/
				
			}
		
		}		
		
		if(selectedItem.getRelazioniSTB().size()>0){
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.NEWLINE);
			revisioniParagraph.add(Chunk.TABBING);
			revisioniParagraph.add(new Chunk("Relazioni tecniche STB ("+selectedItem.getRelazioniSTB().size()+")", fontTextBold));
			
			for (RelazioneSTB relazioneSTB : selectedItem.getRelazioniSTB()) {
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Validità: dal ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(relazioneSTB.getInizioValidita()), fontText));
				revisioniParagraph.add(new Chunk(" al ", fontTextBold));
				revisioniParagraph.add(new Chunk(dateFormat.format(relazioneSTB.getFineValidita()), fontText));				
				
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				if(relazioneSTB.getDescrizione()!=null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Descrizione: ", fontTextBold));
					revisioniParagraph.add(new Chunk(relazioneSTB.getDescrizione(), fontText));
				}
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Stato: ", fontTextBold));
				revisioniParagraph.add(new Chunk(relazioneSTB.getStato().getNome(), fontText));				

				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Stato: ", fontTextBold));
				revisioniParagraph.add(new Chunk(relazioneSTB.getStb().getNome(), fontText));
				
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Erosioni: ", fontTextBold));
				/*if(relazioneSTB.isErosioni())
					revisioniParagraph.add(new Chunk("SI", fontText));
				else
					revisioniParagraph.add(new Chunk("NO", fontText));*/

				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				/*revisioniParagraph.add(new Chunk("Tracimazioni: ", fontTextBold));
				if(relazioneSTB.isTracimazioni())
					revisioniParagraph.add(new Chunk("SI", fontText));
				else
					revisioniParagraph.add(new Chunk("NO", fontText));*/

				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				/*revisioniParagraph.add(new Chunk("Inondazioni: ", fontTextBold));
				if(relazioneSTB.isInondazioni())
					revisioniParagraph.add(new Chunk("SI", fontText));
				else
					revisioniParagraph.add(new Chunk("NO", fontText));*/
				
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Danni difese: ", fontTextBold));
				/*if(relazioneSTB.isDanniDifese())
					revisioniParagraph.add(new Chunk("SI", fontText));
				else
					revisioniParagraph.add(new Chunk("NO", fontText));*/
				
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Danni infrastrutture: ", fontTextBold));
				/*if(relazioneSTB.isDanniInfrastrutture())
					revisioniParagraph.add(new Chunk("SI", fontText));
				else
					revisioniParagraph.add(new Chunk("NO", fontText));*/
				
				/*if(Float.valueOf(relazioneSTB.getVolumiErosi()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Volumi erosi (m3): ", fontTextBold));
					revisioniParagraph.add(new Chunk(relazioneSTB.getVolumiErosi()+"", fontText));
				}*/
				revisioniParagraph.add(Chunk.NEWLINE);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(Chunk.TABBING);
				revisioniParagraph.add(new Chunk("Stima dei costi", fontTextBoldTitolo));

				/*if(Float.valueOf(relazioneSTB.getStimeCostiRipascimenti()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Stima costi ripascimenti: ", fontTextBold));
					revisioniParagraph.add(new Chunk(currency.format(relazioneSTB.getStimeCostiRipascimenti())+"", fontText));
				}*/
				/*if(Float.valueOf(relazioneSTB.getStimeCostiOpere()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Stima costi danni opere rigide: ", fontTextBold));
					revisioniParagraph.add(new Chunk(currency.format(relazioneSTB.getStimeCostiOpere())+"", fontText));
				}*/
				/*if(Float.valueOf(relazioneSTB.getStimeCostiOpere()) != null && Float.valueOf(relazioneSTB.getStimeCostiRipascimenti()) != null){
					revisioniParagraph.add(Chunk.NEWLINE);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(Chunk.TABBING);
					revisioniParagraph.add(new Chunk("Stima costi totali: ", fontTextBold));
					revisioniParagraph.add(new Chunk(currency.format(relazioneSTB.getStimeCostiRipascimenti()+relazioneSTB.getStimeCostiOpere()), fontText));
				}*/				
				
				
			}
		}
		
		document.add(revisioniParagraph);	
		// FINE REVISIONI


		// ALLEGATI ALTRE FOTO
		
		List<Allegato> allegatiAll = selectedItem.getAllegati();
		int ii=0;
		for (Allegato allegato : allegatiAll) {
			if(allegato.getTipo().getNome().equalsIgnoreCase("immagine")){
				if(ii>1){
			
					Image img;
					if(allegato.getFile() != null){
						img = Image.getInstance(allegato.getFile());
					} else {
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
				ii++;
				
			}
		}
		// FINE ALLEGATI ALTRE FOTO		
		
		
		
		
		document.close();
		
		AttachmentUtils.downloadFile(temp);


		return "";
		
	}
	
	
	public String nuovaMareggiata() {
		cleanForm();
		mareggiataBeanEdit = new MareggiataBean();
		step = MareggiateWizardStep.STEP1;
		return "nuovaMareggiata";
	}

	public String deleteItem()
	{
		MareggiataManager mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerName, JNDIUtils.MareggiataManagerName);
		try {
			mareggiataManager.remove(itemToDelete);
		} catch (Exception e) {
			e.printStackTrace();
			//newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			//newItemFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		updateAllDataModel();
		return "delete";
	}
	
	public String nuovaSchedaFra() {
		
		this.selectedItem = (Mareggiata) dataModel.getRowData();
		
		MareggiataManager mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerName, JNDIUtils.MareggiataManagerName);
		
		//this.selectedItem = (Mareggiata) dataModel.getRowData();

		//CondizioneMeteoManager condizioneMeteoManager = (CondizioneMeteoManager) JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerName, JNDIUtils.CondizioneMeteoManagerName);
		//CondizioneMeteo condizioneMeteo = condizioneMeteoManager.findItemById(itemToAct);
		
		//this.selectedItem.setPrevisioniMeteo(mareggiataManager.getPrevisioniMeteo(this.selectedItem.getId()));
		
		try{
			
			this.selectedItem.setCondizioniMeteo(mareggiataManager.getCondizioniMeteo(this.selectedItem.getId()));

			List<CondizioneMeteo> listaCondizioniMeteo = mareggiataManager.getCondizioniMeteo(this.selectedItem.getId());
			
			
			for (CondizioneMeteo condizioneMeteo : listaCondizioniMeteo){
				
				logger.info("POLLO"+condizioneMeteo.getRilevazioni().size());
				
			}
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
		}
		return "";
		
	}		

	public String viewItemDetails() {
		this.selectedItem = (Mareggiata) dataModel.getRowData();

		MareggiataManager mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerName, JNDIUtils.MareggiataManagerName);

		try {
			this.selectedItem.setAllegati(mareggiataManager.getAllegati(this.selectedItem.getId()));
			this.selectedItem.setCondizioniMeteo(mareggiataManager.getCondizioniMeteo(this.selectedItem.getId()));
			this.selectedItem.setImpattiReali(mareggiataManager.getImpattiReali(this.selectedItem.getId()));
			this.selectedItem.setPrevisioniImpatti(mareggiataManager.getPrevisioneImpatto(this.selectedItem.getId()));
			this.selectedItem.setPrevisioniMeteo(mareggiataManager.getPrevisioniMeteo(this.selectedItem.getId()));
			this.selectedItem.setRelazioniSTB(mareggiataManager.getRelazioniSTB(this.selectedItem.getId()));

		} catch (EntityNotFoundException e) {
			//TODO gestione errore
			e.printStackTrace();
			updateDataModel();
			return "delete";
		}


		return "mareggiataDetails";
	}

	public String salvaMareggiata() {


		if(!checkMareggiataFormFields()){
		
			/* Modifico messaggio di file non valido perchè il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (mareggiataFormMessageRendered == true) {
				if (mareggiataFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					mareggiataFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
		
			return "";
		}

		MareggiateControllerService mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerServiceName, JNDIUtils.MareggiataManagerServiceName);

		Mareggiata temp = new Mareggiata();
		temp.setCodice(this.mareggiataBeanEdit.getCodice());
		temp.setDescrizione(this.mareggiataBeanEdit.getDescrizione());
		temp.setInizioValidita(this.mareggiataBeanEdit.getInizioValidita());
		temp.setFineValidita(this.mareggiataBeanEdit.getFineValidita());

		try {
			mareggiataManager.salvaMareggiata(temp, allegati, tipiAllegato, filtered_previsioniMeteo, filtered_previsioniImpatti, filtered_impattiReali, filtered_condizioniMeteo, filtered_relazioniSTB, checkedRelazioniSTB, checkedPrevisioniMeteo, checkedImpattiReali, checkedCondizioniMeteo, checkedPrevisioniImpatti);


		} catch (Exception e) {
			// TODO Gestire render errori
			e.printStackTrace();
			
			this.mareggiataFormMessageRendered = true;
			this.mareggiataFormMessage = Messages.FORM_ERROR_PERSIST;

			return "";
		}

		updateDataModel();
		updateAllDataModel();


		return "mareggiataSalvata";
	}


	public String annullaCreazione() {

		cleanForm();
		this.editMode = false;
		return "annullaCreazioneMareggiata";
	}


	public String previous()
	{
		this.saveEnabled = false;

		switch (step) {
		case STEP1:

			break;

		case STEP2:
			step = MareggiateWizardStep.STEP1;
			break;

		case STEP3:
			step = MareggiateWizardStep.STEP2;
			break;

		case STEP4:
			step = MareggiateWizardStep.STEP3;
			break;

		default:
			break;
		}
		return "previous";
	}

	public String next() {

		switch (step) {
		case STEP1:
		{

			logger.info("Go To Step2");
			List<Criterion> criterions = new ArrayList<Criterion>();

			if(!checkMareggiataFormFields()){
				return "";
			}



			//aggiungo evento alla mareggiate se:
			// 	1. L'evento ha uno dei due estremo che si verifica dentro l'arco  della mareggiata
			//	2. L'evento si verifica dentro l'arco temporale della mareggiata
			//  3. L'evento  e' temporalmente più esteso della mareggiata
			criterions.add(
					Restrictions.or(
							Restrictions.or(
									Restrictions.or(
											Restrictions.between("inizioValidita", mareggiataBeanEdit.getInizioValidita(), mareggiataBeanEdit.getFineValidita()),
											Restrictions.between("fineValidita", mareggiataBeanEdit.getInizioValidita(), mareggiataBeanEdit.getFineValidita())),
											Restrictions.and(
													Restrictions.ge("inizioValidita", mareggiataBeanEdit.getInizioValidita()),
													Restrictions.le("fineValidita", mareggiataBeanEdit.getFineValidita())
													)
									),
									Restrictions.and(
											Restrictions.le("inizioValidita", mareggiataBeanEdit.getInizioValidita()),
											Restrictions.ge("fineValidita", mareggiataBeanEdit.getFineValidita())
											)
							)
					);

			NestedCriterion relazioneSTBstato = new NestedCriterion("stato", Restrictions.or(Restrictions.eq("nome", "Definitivo"),Restrictions.eq("nome", "Protocollato")));


			Criterion check = Restrictions.isNull("mareggiata");
			criterions.add(check);


			int countPrevisioneMeteo = pagingManager.countByCriteria(PrevisioneMeteo.class, criterions);
			filtered_previsioniMeteo = pagingManager.findByCriteria(PrevisioneMeteo.class, 0, countPrevisioneMeteo, criterions, null);

			int countCondizioneMeteo = pagingManager.countByCriteria(CondizioneMeteo.class, criterions);
			filtered_condizioniMeteo = pagingManager.findByCriteria(CondizioneMeteo.class, 0, countCondizioneMeteo, criterions, null);


			criterions.remove(check);
			criterions.add(relazioneSTBstato);
			int countRelazioneSTB = pagingManager.countByCriteria(RelazioneSTB.class, criterions);
			filtered_relazioniSTB = pagingManager.findByCriteria(RelazioneSTB.class, 0, countRelazioneSTB, criterions, null);
			criterions.remove(relazioneSTBstato);

			criterions.remove(check);
			check = Restrictions.isNull("mareggiataPrevisioneImpatto");
			criterions.add(check);

			int countPrevisioneImpatto= pagingManager.countByCriteria(PrevisioneImpatto.class, criterions);
			filtered_previsioniImpatti = pagingManager.findByCriteria(PrevisioneImpatto.class, 0, countPrevisioneImpatto, criterions, null);

			criterions.remove(check);
			check = Restrictions.isNull("mareggiataImpattoReale");
			criterions.add(check);

			int countImpattoReale = pagingManager.countByCriteria(ImpattoReale.class, criterions);
			filtered_impattiReali = pagingManager.findByCriteria(ImpattoReale.class, 0, countImpattoReale, criterions, null);


			step = MareggiateWizardStep.STEP2;

		}
		break;

		case STEP2:
		{

			logger.info("Go To Step3");
			step = MareggiateWizardStep.STEP3;
		}
		break;

		case STEP3:
		{

			logger.info("Go To Step4");
			step = MareggiateWizardStep.STEP4;
			saveEnabled = true;
		}
		break;

		default:
			break;
		}


		this.mareggiataFormMessageRendered = false;

		return "next";
	}

	/** Gestione visualizzazione dettagli mareggiata **/

	public String viewPrevisioneMeteoDetail()
	{
		logger.info("viewPrevisioneMeteoDetail "+idDetail);

		PrevisioneMeteoManager previsioneMeteoManager = JNDIUtils.getPrevisioneMeteoManager();
		PrevisioneMeteo temp = previsioneMeteoManager.findItemById(idDetail);
		try {
			temp.setAllegati(previsioneMeteoManager.getAllegati(idDetail));
			temp.setEventiCostieri(previsioneMeteoManager.getEventiCostieri(idDetail));

			AvvisiMeteoEventiCostieriController controller = (AvvisiMeteoEventiCostieriController)JSFUtils.getManagedBean("AvvisiMeteoEventiCostieriController");
			controller.setSelectedItem(temp);


		} catch (EntityNotFoundException e) {

			//TODO GEstione visualizzazione errori
			logger.error(e.getMessage());
			return "";
		}

		return "toAvvisoMeteoDetails";
	}

	public String viewPrevisioneImpattoDetail()
	{
		logger.info("viewPrevisioneImpattoDetail "+idDetail);

		PrevisioneImpattoManager previsioneImpattoManager = JNDIUtils.getPrevisioneImpattoManager();
		DannoManager dannoManager = JNDIUtils.getDannoManager();

		PrevisioneImpatto temp = previsioneImpattoManager.findItemById(idDetail);

		try {
			temp.setDanni(previsioneImpattoManager.getDanni(idDetail));

			ValutazioneImpattiController controller = (ValutazioneImpattiController)JSFUtils.getManagedBean("ValutazioneImpattiController");
			controller.setSelectedItem(temp);

		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			//TODO gestione errori
			logger.error(e.getMessage());
			return "";
		}
		return "toPrevisioneImpattoDetails";
	}

	public String viewImpattoRealeDetails()
	{
		logger.info("viewImpattoRealeDetails "+idDetail);

		ImpattoRealeManager impattoRealeManager = JNDIUtils.getImpattoRealeManager();
		DannoManager dannoManager = JNDIUtils.getDannoManager();

		ImpattoReale temp = impattoRealeManager.findItemById(idDetail);

		try {
			temp.setDanni(impattoRealeManager.getDanni(idDetail));
			for (Danno d : temp.getDanni() ) {
				d.setAllegati(dannoManager.getAllegati(d.getId()));
			}

			ImpattiRealiController controller = (ImpattiRealiController)JSFUtils.getManagedBean("ImpattiRealiController");
			controller.setSelectedItem(temp);

		} catch (EntityNotFoundException e) {
			//TODO gestione errori
			logger.error(e.getMessage());
			return "";
		}
		return "toImpattoRealeDetails";
	}


	public String viewRelazioneSTBDetails()
	{
		logger.info("viewRelazioneSTBDetails "+idDetail);

		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		DannoSTBManager dannoSTBManager = (DannoSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.DannoSTBManagerName, JNDIUtils.DannoSTBManagerName);
		RelazioneSTB temp = relazioneSTBManager.findItemById(idDetail);


		try {
			temp.setDanni(relazioneSTBManager.getDanniSTB(idDetail));
			for (DannoSTB d : temp.getDanni() ) {
				d.setAllegati(dannoSTBManager.getAllegati(d.getId()));
			}

			RelazioniTecnicheSTBController stbController = (RelazioniTecnicheSTBController)JSFUtils.getManagedBean("RelazioniTecnicheSTBController");
			stbController.setSelectedItem(temp);


		} catch (EntityNotFoundException e) {

			//TODO gestione errori
			logger.error(e.getMessage());

			return "";
		}

		return "toRelazioneSTBDetails";
	}


	public String viewCondizioneMeteoDetails()
	{
		logger.info("viewCondizioneMeteoDetails "+idDetail);

		CondizioneMeteoManager meteoCRUD = JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerName, JNDIUtils.CondizioneMeteoManagerName);

		CondizioneMeteo temp = meteoCRUD.findItemById(idDetail);

		CondizioniMeteoMarineController stbController = (CondizioniMeteoMarineController)JSFUtils.getManagedBean("CondizioniMeteoMarineController");
		stbController.setSelectedItem(temp);


		try {
			temp.setRilevazioni(meteoCRUD.getRilevazioni(temp.getId()));
		} catch (EntityNotFoundException e) {

			e.printStackTrace();
			//TODO GEstione visualizzazione errori
			logger.error(e.getMessage());

			return "";
		}

		return "toCondizioneMeteoDetails";

	}

	/** Gestione Upload File**/

	public int getSize() {
		if (this.allegati.getUploadedFiles().size() > 0) {
			return this.allegati.getUploadedFiles().size();
		} else {
			return 0;
		}
	}

	public void fileUploadListener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		if(item.getFileSize() == 0)
		{
			mareggiataFormMessage = Messages.FORM_ERROR_IOFILE_EMPTY;
			mareggiataFormMessageRendered = true;
			return;
		}
		
		FileValidator f= new FileValidator();	
		if (!f.Validate(item.getFile(), item.getFileName())) {
			mareggiataFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
			mareggiataFormMessageRendered = true;
			return;					
		}  else {
			mareggiataFormMessageRendered = false;
			allegati.addAllegato(item);
		}
	}	
			

	public void clearFileUpload(ActionEvent event) {
		mareggiataFormMessageRendered = false;
		if(this.allegati.getUploadedFiles() == null || this.allegati.getUploadedFiles().isEmpty())
			return;

		if (getFileToClear() != null && !"".equals(getFileToClear())) {
			if (mareggiataFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
				//logger.info("posso cancellare messagio - si riferisce a questo file" + mareggiataFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				mareggiataFormMessageRendered = false; 
			} else {
				//logger.info("non posso cancellare messagio - non si riferisce a questo file" + mareggiataFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				mareggiataFormMessageRendered = true; 
			}
		
			logger.info("filetoclear "
					+ this.getFileToClear().replace(FAKE_PATH, ""));
			
			allegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
		} else {
			logger.info("clearing all files");
			
			allegati.clear();
		}

	}

	public void removeExistingAttachment() {

		if ( editMode && attachmentToRemove.getId() >= 1  ) {
			attachmentsToRemoveList.add(attachmentToRemove);
		}
		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		boolean removed = allegatiTemp.remove(attachmentToRemove);
		System.out.println("removeExistingAttachment "+attachmentToRemove.getId()+" "+removed);

	}

	private void updateTipiAllegato() {
		tipiAllegato = new LinkedHashMap<String, TipoAllegato>();
		tipiAllegatoSelect = new ArrayList<SelectItem>();
		List<TipoAllegato> tipiAllegatoFromDB = JNDIUtils
				.getTipoAllegatoManager().selectAll();
		tipiAllegatoSelect.add(new SelectItem(null, "Tipo di allegato"));
		for (TipoAllegato ta : tipiAllegatoFromDB) {
			tipiAllegato.put(ta.getNome(), ta);
			tipiAllegatoSelect.add(new SelectItem(ta.getNome()));
		}
	}


	public void downloadAttachment() {
		AttachmentUtils.downloadAttachment(this.getAttachmentToDownload());
	}

	private boolean checkMareggiataFormFields() {

		if(mareggiataBeanEdit.getInizioValidita() == null || mareggiataBeanEdit.getFineValidita()==null || mareggiataBeanEdit.getCodice()==null )
		{
			this.mareggiataFormMessage = Messages.FORM_NOT_VALID;
			this.mareggiataFormMessageRendered = true;

			return false;
		}

		if(this.allegati.checkTipiAllegatiPresenti() == false)
		{
			this.mareggiataFormMessageRendered = true;
			this.mareggiataFormMessage  = Messages.FORM_ATTACHMENTTYPE_MISSING;
			return false;
		}

		if((mareggiataBeanEdit.getInizioValidita().after(mareggiataBeanEdit.getFineValidita())))
		{
			mareggiataFormMessageRendered = true;
			mareggiataFormMessage = Messages.FORM_ERROR_DATE;
			return false;
		}
		
		/* File caricato non valido */
		if (mareggiataFormMessageRendered == true) {
			if (mareggiataFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}

		mareggiataFormMessageRendered = false;
		return true;

	}

	public void updateAllDataModel()
	{
		CondizioniMeteoMarineController cmController = (CondizioniMeteoMarineController)JSFUtils.getManagedBean("CondizioniMeteoMarineController");
		cmController.updateDataModel();


		AvvisiMeteoEventiCostieriController amController = (AvvisiMeteoEventiCostieriController)JSFUtils.getManagedBean("AvvisiMeteoEventiCostieriController");
		amController.updateDataModel();


		ImpattiRealiController irController = (ImpattiRealiController)JSFUtils.getManagedBean("ImpattiRealiController");
		irController.updateDataModel();

		ValutazioneImpattiController viController = (ValutazioneImpattiController)JSFUtils.getManagedBean("ValutazioneImpattiController");
		viController.updateDataModel();


	}


	/** FilterableHandler methods**/

	@Override
	public void resetSearchParameters() {

	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(filtro_inizioValiditaGiorno != null && filtro_fineValiditaGiorno!=null){
			criterions.add(Restrictions.and(
					Restrictions.ge("inizioValidita", filtro_inizioValiditaGiorno),
					Restrictions.le("fineValidita", filtro_fineValiditaGiorno)));
		}
		else if(filtro_inizioValiditaGiorno != null)
		{
			criterions.add(
					Restrictions.ge("inizioValidita", filtro_inizioValiditaGiorno));
		} 
		else if(filtro_fineValiditaGiorno!=null)
		{
			criterions.add(
					Restrictions.le("fineValidita", filtro_fineValiditaGiorno));
		}
		return criterions;
	}

	protected List<Order> determineOrder()
	{
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add( Order.desc("inizioValidita") );
		orders.add( Order.desc("fineValidita") );
		return orders;
	}


	/** Getter and Setter **/

	public Date getFiltro_inizioValiditaGiorno() {
		return filtro_inizioValiditaGiorno;
	}


	public void setFiltro_inizioValiditaGiorno(Date filtro_inizioValiditaGiorno) {
		this.filtro_inizioValiditaGiorno = filtro_inizioValiditaGiorno;
	}


	public Date getFiltro_fineValiditaGiorno() {
		return filtro_fineValiditaGiorno;
	}


	public void setFiltro_fineValiditaGiorno(Date filtro_fineValiditaGiorno) {
		this.filtro_fineValiditaGiorno = filtro_fineValiditaGiorno;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public boolean isSaveEnabled() {
		return saveEnabled;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void setSaveEnabled(boolean saveEnabled) {
		this.saveEnabled = saveEnabled;
	}

	public MareggiataBean getMareggiataBeanEdit() {
		return mareggiataBeanEdit;
	}

	public List<PrevisioneMeteo> getFiltered_previsioniMeteo() {
		return filtered_previsioniMeteo;
	}

	public List<PrevisioneImpatto> getFiltered_previsioniImpatti() {
		return filtered_previsioniImpatti;
	}

	public List<ImpattoReale> getFiltered_impattiReali() {
		return filtered_impattiReali;
	}

	public List<CondizioneMeteo> getFiltered_condizioniMeteo() {
		return filtered_condizioniMeteo;
	}

	public List<RelazioneSTB> getFiltered_relazioniSTB() {
		return filtered_relazioniSTB;
	}

	public Map<Long, Boolean> getCheckedRelazioniSTB() {
		return checkedRelazioniSTB;
	}

	public Map<Long, Boolean> getCheckedPrevisioniMeteo() {
		return checkedPrevisioniMeteo;
	}

	public Map<Long, Boolean> getCheckedImpattiReali() {
		return checkedImpattiReali;
	}

	public Map<Long, Boolean> getCheckedCondizioniMeteo() {
		return checkedCondizioniMeteo;
	}

	public Map<Long, Boolean> getCheckedPrevisioniImpatti() {
		return checkedPrevisioniImpatti;
	}

	public boolean isMareggiataFormMessageRendered() {
		return mareggiataFormMessageRendered;
	}

	public String getMareggiataFormMessage() {
		return mareggiataFormMessage;
	}


	public List<Allegato> getAttachmentsToRemoveList() {
		return attachmentsToRemoveList;
	}

	public void setAttachmentsToRemoveList(List<Allegato> attachmentsToRemoveList) {
		this.attachmentsToRemoveList = attachmentsToRemoveList;
	}

	public List<Allegato> getAllegatiTemp() {
		if (allegatiTemp == null) {
			allegatiTemp = new ArrayList<Allegato>();
		}
		return allegatiTemp;
	}

	public void setAllegatiTemp(List<Allegato> allegatiTemp) {
		this.allegatiTemp = allegatiTemp;
	}


	public List<SelectItem> getTipiAllegatoSelect() {
		if (null == tipiAllegatoSelect || tipiAllegatoSelect.isEmpty())
			updateTipiAllegato();
		return tipiAllegatoSelect;
	}

	public void setTipiAllegatoSelect(List<SelectItem> tipiAllegatoSelect) {
		this.tipiAllegatoSelect = tipiAllegatoSelect;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public Mareggiata getSelectedItem() {
		return selectedItem;
	}

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}

	public long getIdDetail() {
		return idDetail;
	}

	public void setIdDetail(long idDetail) {
		this.idDetail = idDetail;
	}

	public void setSelectedItem(Mareggiata selectedItem) {
		this.selectedItem = selectedItem;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
	}

	public long getItemToAct() {
		return itemToAct;
	}

	public void setItemToAct(long itemToAct) {
		this.itemToAct = itemToAct;
	}

}
