package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.service.AvvisiMeteoEventiCostieriService;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.MacroArea;
import it.epocaricerca.geologia.model.Fenomeno;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.Altezza;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Tendenza;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.JSFUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;
import it.epocaricerca.geologia.web.validator.FileValidator;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.faces.component.html.HtmlDataTable;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModelEvent;
import javax.faces.model.SelectItem;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

public class AvvisiMeteoEventiCostieriController extends FilterableHandler<PrevisioneMeteo> {

	private static final long serialVersionUID = 1L;
	
	private static Logger logger = Logger
			.getLogger(AvvisiMeteoEventiCostieriController.class);

	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

	private static final String FAKE_PATH = "C:\\fakepath\\";
	private static final int MAX_UPLOADS = 10;

	// FORM DI INSERIMENTO NUOVO AVVISO COSTIERO (TRE TAB)
	private long id;
	private String idAvviso;
	private Date dataAvviso;
	private Date inizioValiditaGiorno;
	private Date fineValiditaGiorno;
	private Map<String, Tendenza> tendenze;
	private List<SelectItem> tendenzeSelect;
	private String tendenza;
	
	private Map<String, Provenienza> provenienzeOnde;
	private List<SelectItem> provenienzeOndeSelect;
		
	private float pianuraVento;
	private float pianuraVentoMax;
	private String pianuraDirezioneVento;
	
	private float pedemontanaVento;
	private float pedemontanaVentoMax;
	private String pedemontanaDirezioneVento;
	
	private Map<String, Altezza> altezzeStimaOnde;
	private List<SelectItem> altezzeStimaOndeSelect;
	private String altezzaStimataOnda;

	private Map<String, Provenienza> direzioneProvStimaOnde;
	private List<SelectItem> direzioneProvStimaOndeSelect;
	private String direzioneProvStimataOnda;
		
	private String descrizione;
	
	private Map<String, MacroArea> macroAree;
	private List<SelectItem> macroAreeSelect;
	private String macroArea;
	
	private Map<String, Fenomeno> fenomeni;
	private List<SelectItem> fenomeniSelect;
	private String fenomeno;
	
	private float eventoCostieroAltezzaOnda;
	private String eventoCostieroProvenienzaOnda;
	private float eventoCostieroLivelloMare;
	private Date eventoCostieroInizio;
	private Date eventoCostieroFine;
	
	private List<EventoCostiero> eventiCostieriTemp;
	
	
	private boolean avvisoMeteoFormMessageRendered = false;
	private boolean eventoCostieroFormMessageRendered = false;
	private String avvisoMeteoFormMessage;
	private String eventoCostieroFormMessage;
	private int eventoCostieroRow;
	
	
	//Allegati
	private List<Allegato> allegatiTemp;
	private Map<String, TipoAllegato> tipiAllegato;
	private List<SelectItem> tipiAllegatoSelect;
	private String fileToClear;
	private String selectedTipoAllegato;
	private Allegato attachmentToDownload;
	private Allegato attachmentToRemove;
	private List<Allegato> attachmentsToRemoveList;
	
	private FileUploadBean allegati;
	

	// FILTRO DI RICERCA
	private Date filtroEventiCostieri_inizioValiditaGiorno;
	private Date filtroEventiCostieri_fineValiditaGiorno;

	private PrevisioneMeteo selectedItem;
	
	private Long itemToDelete;

	private HtmlDataTable attachmentsDataTable;
	
	private EventoCostiero eventoCostieroToRemove;
	private List<EventoCostiero> eventiCostieriToRemoveList;
	private Map<Long, String> previsioneAreeCoinvolte = new HashMap<Long, String>();

	private boolean editMode = false;

	public void filtraEventiCostieri() {
		updateDataModel();
	}
	

	public void pulisciFiltriEventiCostieri() {
		filtroEventiCostieri_inizioValiditaGiorno = null;
		filtroEventiCostieri_fineValiditaGiorno = null;
		updateDataModel();
	}

	public String nuovoMeteoEventoCostiero() {
		cleanForm();
		updateTipiAllegato();
		return "nuovoMeteoEventoCostiero";
	}

	@Override
	public void updateDataModel()
	{
		super.updateDataModel();
		previsioneAreeCoinvolte.clear();

	}

	@Override
	public void rowSelected(DataModelEvent event)
	{

		PrevisioneMeteo previsioneMeteo = (PrevisioneMeteo) event.getRowData();
		PrevisioneMeteoManager previsioneMeteoManager = JNDIUtils.getPrevisioneMeteoManager();

		List<EventoCostiero> eventiCostieri;
		try {
			eventiCostieri = previsioneMeteoManager.getEventiCostieri(previsioneMeteo.getId());
			ArrayList<String> aree = new ArrayList<String>();
			for (EventoCostiero eventoCostiero : eventiCostieri) {
				aree.add(eventoCostiero.getMacroArea().getNome());
			}

			HashSet<String> hashSet = new HashSet<String>(aree);
			aree = new ArrayList<String>(hashSet);
			if(aree.size() != 0)
				previsioneAreeCoinvolte.put(previsioneMeteo.getId(), Arrays.toString(aree.toArray()));
			else
				previsioneAreeCoinvolte.put(previsioneMeteo.getId(),"");
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}


	public String deleteItem() {
		//	this.selectedItem = (PrevisioneMeteo) dataModel.getRowData();
		PrevisioneMeteoManager previsioneMeteoManager = JNDIUtils.getPrevisioneMeteoManager();
		try {
			previsioneMeteoManager.remove(this.getItemToDelete());
		} catch (EntityNotFoundException e) {
			avvisoMeteoFormMessage = Messages.FORM_ERROR_PERSIST;
			avvisoMeteoFormMessageRendered = true;
			//return "";

		}
		pulisciFiltriEventiCostieri();
		return "delete";
	}

	public String editItemDetails() {
		cleanForm();
		this.selectedItem = (PrevisioneMeteo) dataModel.getRowData();
		PrevisioneMeteoManager previsioneMeteoManager = JNDIUtils.getPrevisioneMeteoManager();
		try {
			this.selectedItem.setAllegati(previsioneMeteoManager.getAllegati(this.selectedItem.getId()));
			this.selectedItem.setEventiCostieri(previsioneMeteoManager.getEventiCostieri(this.selectedItem.getId()));
		} catch (EntityNotFoundException e) {
			logger.error(e.getMessage());
			updateDataModel();
			return "";
		}

		return editAvvisoMeteo();
	}

	private void cleanForm() {

		editMode = false;
		idAvviso = "";
		dataAvviso = null;
		inizioValiditaGiorno = null;
		fineValiditaGiorno = null;
		descrizione = null;
		
		pedemontanaDirezioneVento = "";
		pedemontanaVento = 0;
		pedemontanaVentoMax = 0;
		
		pianuraDirezioneVento = "";
		pianuraVento = 0;
		pianuraVentoMax = 0;
		
		altezzaStimataOnda = "";
		direzioneProvStimataOnda = "";
		
		if (null != eventiCostieriTemp)
			eventiCostieriTemp.clear();
		
		if( null != allegatiTemp)
			allegatiTemp.clear();
		
		tendenza = "";
		
		this.allegati = new FileUploadBean(MAX_UPLOADS);
		
		if (null != tipiAllegato)
			tipiAllegato.clear();
		if (null != tipiAllegatoSelect)
			tipiAllegatoSelect.clear();
		
		avvisoMeteoFormMessage = "";
		avvisoMeteoFormMessageRendered = false;
		
		cleanEventiCostieriForm();
	}

	private void cleanEventiCostieriForm() {
		macroArea = "";
		pedemontanaDirezioneVento = "";
		pianuraDirezioneVento = "";
		fenomeno = "";
		setEventoCostieroAltezzaOnda(0);
		setEventoCostieroLivelloMare(0);
		eventoCostieroInizio = null;
		eventoCostieroFine = null;
		
		eventoCostieroFormMessage = "";
		eventoCostieroFormMessageRendered = false;
		
	}

	public String editAvvisoMeteo() {
		this.editMode = true;
		// tab generale
		id = this.selectedItem.getId();
		idAvviso = this.selectedItem.getIdAvviso();
		dataAvviso = this.selectedItem.getDataAvviso();
		inizioValiditaGiorno = this.selectedItem.getInizioValidita();
		fineValiditaGiorno = this.selectedItem.getFineValidita();
		
		if(this.selectedItem.getPedemontanaDirezioneVento()!=null)
			pedemontanaDirezioneVento = this.selectedItem.getPedemontanaDirezioneVento().getNome();
		else 
			pedemontanaDirezioneVento="-";
		
		pedemontanaVento = this.selectedItem.getPedemontanaVento();
		pedemontanaVentoMax = this.selectedItem.getPedemontanaVentoMax();
		
		if(this.selectedItem.getPianuraDirezioneVento()!=null)
			pianuraDirezioneVento = this.selectedItem.getPianuraDirezioneVento().getNome();
		else
			pianuraDirezioneVento = "-";
		
		pianuraVento = this.selectedItem.getPianuraVento();
		pianuraVentoMax = this.selectedItem.getPianuraVentoMax();
		
		descrizione=this.selectedItem.getDescrizione();
		
		// tab eventi costieri
		if (null != eventiCostieriTemp)
			eventiCostieriTemp.clear();
		
		if( null != allegatiTemp)
			allegatiTemp.clear();
		
		eventiCostieriTemp = new ArrayList<EventoCostiero>(selectedItem.getEventiCostieri());
		
		allegatiTemp = new ArrayList<Allegato>(this.selectedItem.getAllegati());
		
		tendenza = this.selectedItem.getTendenza().getNome();
		
		if(this.selectedItem.getAltezzaStimataOnda()!=null)
			altezzaStimataOnda = this.selectedItem.getAltezzaStimataOnda().getNome();
		else
			altezzaStimataOnda = "-";
		
		if(this.selectedItem.getDirezioneProvStimataOnda()!=null)
			direzioneProvStimataOnda = this.selectedItem.getDirezioneProvStimataOnda().getNome();
		else
			direzioneProvStimataOnda = "-";
		
		if (null != eventiCostieriToRemoveList) {
			eventiCostieriToRemoveList.clear();
		}
		else {
			eventiCostieriToRemoveList = new ArrayList<EventoCostiero>();
		}
		
		this.allegati = new FileUploadBean(MAX_UPLOADS);
		
		
		if (null != attachmentsToRemoveList) {
			attachmentsToRemoveList.clear();
		}
		else {
			attachmentsToRemoveList = new ArrayList<Allegato>();
		}

		return "editAvvisoMeteo";
	}

	public String salvaAvvisoMeteo() {
		if (checkAvvisoMeteoFormFields()) {

			AvvisiMeteoEventiCostieriService avvisiMeteoEventiCostieriService = JNDIUtils.retrieveEJB(JNDIUtils.AvvisiMeteoEventiCostieriServiceName, JNDIUtils.AvvisiMeteoEventiCostieriServiceName);
			

			try {
				if(!editMode){
					PrevisioneMeteo pm = new PrevisioneMeteo();
					pm.setIdAvviso(idAvviso);
					pm.setDataAvviso(dataAvviso);
					pm.setInizioValidita(inizioValiditaGiorno);
					pm.setFineValidita(fineValiditaGiorno);
					
					pm.setPedemontanaVento(pedemontanaVento);
					pm.setPedemontanaVentoMax(pedemontanaVentoMax);
					
					pm.setPianuraVento(pianuraVento);
					pm.setPianuraVentoMax(pianuraVentoMax);
					pm.setDescrizione(descrizione);
					
					avvisiMeteoEventiCostieriService.salvaAvvisoMeteo(editMode, pm, pianuraDirezioneVento, pedemontanaDirezioneVento, tendenza, altezzaStimataOnda, direzioneProvStimataOnda, eventiCostieriTemp, eventiCostieriToRemoveList, tipiAllegato, allegati, attachmentsToRemoveList);
					
				}
				else{
					this.selectedItem.setIdAvviso(idAvviso);
					this.selectedItem.setDataAvviso(dataAvviso);
					this.selectedItem.setInizioValidita(inizioValiditaGiorno);
					this.selectedItem.setFineValidita(fineValiditaGiorno);
					
					this.selectedItem.setPedemontanaVento(pedemontanaVento);
					this.selectedItem.setPedemontanaVentoMax(pedemontanaVentoMax);
					
					this.selectedItem.setPianuraVento(pianuraVento);
					this.selectedItem.setPianuraVentoMax(pianuraVentoMax);
					this.selectedItem.setDescrizione(descrizione);
							
					avvisiMeteoEventiCostieriService.salvaAvvisoMeteo(editMode, this.selectedItem, pianuraDirezioneVento, pedemontanaDirezioneVento, tendenza, altezzaStimataOnda, direzioneProvStimataOnda, eventiCostieriTemp, eventiCostieriToRemoveList, tipiAllegato, allegati, attachmentsToRemoveList);
					
				}
				

			} catch (Exception e) {
				e.printStackTrace();
				avvisoMeteoFormMessage = Messages.FORM_ERROR_PERSIST + "QUIIIII";
				avvisoMeteoFormMessageRendered = true;
				return "";
			}

			updateDataModel();

			return "avvisoMeteoSalvato";

		} else {
			
			/* Modifico messaggio di file non valido perchè il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (avvisoMeteoFormMessageRendered == true) {
				if (avvisoMeteoFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					avvisoMeteoFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
		
			return "";
		}
	}

	public String annullaCreazioneAvviso() {
		cleanForm();
		updateTipiAllegato();
		this.editMode = false;
		return "annullaCreazioneAvviso";
	}

	public String toAvvisiMeteoHome() {
		return "toAvvisiMeteoHome";
	}

	public String viewItemDetails() {
		this.selectedItem = (PrevisioneMeteo) dataModel.getRowData();
		PrevisioneMeteoManager previsioneMeteoManager = JNDIUtils
				.getPrevisioneMeteoManager();
		try {
			this.selectedItem.setAllegati(previsioneMeteoManager.getAllegati(this.selectedItem.getId()));
			this.selectedItem.setEventiCostieri(previsioneMeteoManager.getEventiCostieri(this.selectedItem.getId()));
		} catch (EntityNotFoundException e) {
			
			//TODO GEstione visualizzazione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}

		return "toAvvisoMeteoDetails";
	}

	private boolean checkAvvisoMeteoFormFields() {

		if(StringUtils.notEmpty(idAvviso) == false || dataAvviso == null || inizioValiditaGiorno == null || fineValiditaGiorno == null  )
		{
			avvisoMeteoFormMessageRendered = true;
			avvisoMeteoFormMessage = Messages.FORM_NOT_VALID;
			return false;
		}
		
		if( !allegati.checkTipiAllegatiPresenti() )
		{
			avvisoMeteoFormMessageRendered = true;
			avvisoMeteoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			return false;
		}
				
		if(inizioValiditaGiorno.after(fineValiditaGiorno))
		{
			avvisoMeteoFormMessageRendered = true;
			avvisoMeteoFormMessage = Messages.FORM_ERROR_DATE;
			return false;
		}
		
		/* File caricato non valido */
		if (avvisoMeteoFormMessageRendered == true) {
			if (avvisoMeteoFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}
		
		avvisoMeteoFormMessageRendered = false;
		return true;
		
	}


	/** Gestione Eventi Costieri**/

	public void aggiungiEventoCostiero() {
		if (checkEventoCostieroFormFields()) {
			EventoCostiero ec = new EventoCostiero();
			ec.setMacroArea(macroAree.get(macroArea));
			ec.setFenomeno(fenomeni.get(fenomeno));
			
			//logger.info("aggiungiEventoCostiero: "+eventoCostieroProvenienzaOnda);
			
			if(eventoCostieroProvenienzaOnda!=null)
				ec.setDirezione(provenienzeOnde.get(eventoCostieroProvenienzaOnda));
			else
				ec.setDirezione(null);
			
			ec.setAltezzaOnda((getEventoCostieroAltezzaOnda() == 0) ? 0 : Float
					.valueOf(getEventoCostieroAltezzaOnda()));
			ec.setLivelloMare((getEventoCostieroLivelloMare() == 0) ? 0 : Float
					.valueOf(getEventoCostieroLivelloMare()));
					
					
			if 	(eventoCostieroInizio != null) {
				ec.setInizio(eventoCostieroInizio);
			}
			if (eventoCostieroFine != null) {
				ec.setFine(eventoCostieroFine);
			}
			eventiCostieriTemp.add(ec);
			cleanEventiCostieriForm();
		}
	}

	public void cancellaEventoCostiero() {
		EventoCostiero eventoCostieroToRemove = eventiCostieriTemp.get(eventoCostieroRow);
		if ( editMode && eventoCostieroToRemove.getId() >= 1  ) {
			// � un evento costiero da DB, lo aggiungo alla lista di eventi costieri da rimuovere
			eventiCostieriToRemoveList.add(eventoCostieroToRemove);
		}
		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		eventiCostieriTemp.remove(eventoCostieroRow);
	}

	private boolean checkEventoCostieroFormFields() {

		/*if(eventoCostieroInizio == null || eventoCostieroFine == null)
		{
			eventoCostieroFormMessage = Messages.FORM_NOT_VALID;
			eventoCostieroFormMessageRendered = true;
			return false;
		}*/
		
		if(eventoCostieroInizio != null || eventoCostieroFine != null) {
			if(eventoCostieroInizio.after(eventoCostieroFine))
			{
				eventoCostieroFormMessage = Messages.FORM_ERROR_DATE;
				eventoCostieroFormMessageRendered = true;
				return false;
			}
		}
		
		eventoCostieroFormMessageRendered = false;
		return true;
	}


	/** Gestione Upload File **/

	public int getSize() {
		if (allegati.getUploadedFiles().size() > 0) {
			return allegati.getUploadedFiles().size();
		} else {
			return 0;
		}
	}

	public void fileUploadListener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		
		if(item.getFileSize() == 0)
		{
			avvisoMeteoFormMessage = Messages.FORM_ERROR_IOFILE_EMPTY;
			avvisoMeteoFormMessageRendered = true;
			return;
		}
		
		FileValidator f= new FileValidator();	
		if (!f.Validate(item.getFile(), item.getFileName())) {
			avvisoMeteoFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
			avvisoMeteoFormMessageRendered = true;
			//this.allegati = new FileUploadBean(MAX_UPLOADS);
			return;					
		}  else {
			avvisoMeteoFormMessageRendered = false;
			allegati.addAllegato(item);
		}
		
	}

	public void clearFileUpload(ActionEvent event) {
		
		if(allegati.getUploadedFiles() == null ||allegati.getUploadedFiles().isEmpty()) {
			avvisoMeteoFormMessageRendered = false;
			return;
		}
		if (getFileToClear() != null && !"".equals(getFileToClear()) ) {
		
			if (avvisoMeteoFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
				//logger.info("posso cancellare messagio - si riferisce a questo file" + avvisoMeteoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				avvisoMeteoFormMessageRendered = false; 
			} else {
				//logger.info("non posso cancellare messagio - non si riferisce a questo file" + avvisoMeteoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				avvisoMeteoFormMessageRendered = true; 
			}
			logger.info("filetoclear "+ this.getFileToClear().replace(FAKE_PATH, ""));
			allegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
			
		} else {
			avvisoMeteoFormMessageRendered = false;
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

	private void updateTendenze() {
		tendenze = new LinkedHashMap<String, Tendenza>();
		tendenzeSelect = new ArrayList<SelectItem>();
		List<Tendenza> tendenzeFromDB = JNDIUtils.getTendenzaManager()
				.selectAll();
		for (Tendenza t : tendenzeFromDB) {
			tendenze.put(t.getNome(), t);
			tendenzeSelect.add(new SelectItem(t.getNome()));
		}
	}

	
	private void updateProvenienzeOnde() {
		provenienzeOnde = new LinkedHashMap<String, Provenienza>();
		provenienzeOndeSelect = new ArrayList<SelectItem>();
		provenienzeOndeSelect.add(new SelectItem(null,"-"));
		List<Provenienza> provenienzeFromDB = JNDIUtils.getProvenienzaManager()
				.selectAll();
		for (Provenienza p : provenienzeFromDB) {
			provenienzeOnde.put(p.getNome(), p);
			provenienzeOndeSelect.add(new SelectItem(p.getNome()));
		}
	}
	
	/******/
		
	private void updateAltezzeStimaOnde() {
		altezzeStimaOnde = new LinkedHashMap<String, Altezza>();
		altezzeStimaOndeSelect = new ArrayList<SelectItem>();
		altezzeStimaOndeSelect.add(new SelectItem(null,"-"));		
		List<Altezza> altezzeFromDB = JNDIUtils.getAltezzaManager()
				.selectAll();
		for (Altezza a : altezzeFromDB) {
			altezzeStimaOnde.put(a.getNome(), a);
			altezzeStimaOndeSelect.add(new SelectItem(a.getNome()));
		}
	}
	
	private void updateDirezioneProvStimaOnde() {
		direzioneProvStimaOnde = new LinkedHashMap<String, Provenienza>();
		direzioneProvStimaOndeSelect = new ArrayList<SelectItem>();
		direzioneProvStimaOndeSelect.add(new SelectItem(null,"-"));		
		List<Provenienza> provenienzeFromDB = JNDIUtils.getProvenienzaManager()
				.selectAll();
		for (Provenienza p : provenienzeFromDB) {
			direzioneProvStimaOnde.put(p.getNome(), p);
			direzioneProvStimaOndeSelect.add(new SelectItem(p.getNome()));
		}
	}
	
	/******/
	

	private void updateMacroAree() {
		macroAree = new LinkedHashMap<String, MacroArea>();
		macroAreeSelect = new ArrayList<SelectItem>();
		List<MacroArea> macroareeFromDB = JNDIUtils.getMacroAreaManager()
				.selectAll();
		for (MacroArea ma : macroareeFromDB) {
			macroAree.put(ma.getNome(), ma);
			macroAreeSelect.add(new SelectItem(ma.getNome()));
		}
	}
	
	private void updateFenomeni() {
		fenomeni = new LinkedHashMap<String, Fenomeno>();
		fenomeniSelect = new ArrayList<SelectItem>();
		List<Fenomeno> fenomenoFromDB = JNDIUtils.getFenomenoManager()
				.selectAll();
		for (Fenomeno f : fenomenoFromDB) {
			fenomeni.put(f.getNome(), f);
			fenomeniSelect.add(new SelectItem(f.getNome()));
		}
	}

	public void downloadAttachment() {
		AttachmentUtils.downloadAttachment(this.getAttachmentToDownload());
	}


	/* 
	 *  GETTERS AND SETTERS
	 */

	public String getIdAvviso() {
		return idAvviso;
	}

	public void setIdAvviso(String idAvviso) {
		this.idAvviso = idAvviso;
	}

	public String getTendenza() {
		return tendenza;
	}

	public void setTendenza(String tendenza) {
		this.tendenza = tendenza;
	}

	/**/
	public String getAltezzaStimataOnda() {
		return altezzaStimataOnda;
	}

	public void setAltezzaStimataOnda(String altezzaStimataOnda) {
		this.altezzaStimataOnda = altezzaStimataOnda;
	}
	public String getDirezioneProvStimataOnda() {
		return direzioneProvStimataOnda;
	}

	public void setDirezioneProvStimataOnda(String direzioneProvStimataOnda) {
		this.direzioneProvStimataOnda = direzioneProvStimataOnda;
	}
	/**/
	
	public Date getDataAvviso() {
		return dataAvviso;
	}

	public void setDataAvviso(Date dataAvviso) {
		this.dataAvviso = dataAvviso;
	}
	
	public Date getInizioValiditaGiorno() {
		return inizioValiditaGiorno;
	}

	public void setInizioValiditaGiorno(Date inizioValiditaGiorno) {
		this.inizioValiditaGiorno = inizioValiditaGiorno;
	}

	public Date getFineValiditaGiorno() {
		return fineValiditaGiorno;
	}

	public void setFineValiditaGiorno(Date fineValiditaGiorno) {
		this.fineValiditaGiorno = fineValiditaGiorno;
	}

	public List<EventoCostiero> getEventiCostieriTemp() {
		if (eventiCostieriTemp == null) {
			eventiCostieriTemp = new ArrayList<EventoCostiero>();
		}
		return eventiCostieriTemp;
	}

	public void setEventiCostieriTemp(List<EventoCostiero> eventiCostieriTemp) {
		this.eventiCostieriTemp = eventiCostieriTemp;
	}

	public String getMacroArea() {
		return macroArea;
	}

	public void setMacroArea(String macroArea) {
		this.macroArea = macroArea;
	}

	public Map<String, MacroArea> getMacroAree() {
		return macroAree;
	}

	public void setMacroAree(Map<String, MacroArea> macroAree) {
		this.macroAree = macroAree;
	}

	public List<SelectItem> getMacroAreeSelect() {
		if (macroAreeSelect == null || macroAreeSelect.isEmpty())
			updateMacroAree();
		return macroAreeSelect;
	}

	public void setMacroAreeSelect(List<SelectItem> macroAreeSelect) {
		this.macroAreeSelect = macroAreeSelect;
	}
	
	public String getFenomeno() {
		return fenomeno;
	}

	public void setFenomeno(String fenomeno) {
		this.fenomeno = fenomeno;
	}

	public Map<String, Fenomeno> getFenomeni() {
		return fenomeni;
	}

	public void setFenomeni(Map<String, Fenomeno> fenomeni) {
		this.fenomeni = fenomeni;
	}

	public List<SelectItem> getFenomeniSelect() {
		if (fenomeniSelect == null ||fenomeniSelect.isEmpty())
			updateFenomeni();
		return fenomeniSelect;
	}

	public void setFenomeniSelect(List<SelectItem> fenomeniSelect) {
		this.fenomeniSelect = fenomeniSelect;
	}

	public List<SelectItem> getProvenienzeOndeSelect() {
		if (provenienzeOndeSelect == null || provenienzeOndeSelect.isEmpty())
			updateProvenienzeOnde();
		return provenienzeOndeSelect;
	}

	public void setProvenienzeOndeSelect(List<SelectItem> provenienzeOndeSelect) {
		this.provenienzeOndeSelect = provenienzeOndeSelect;
	}

	public Map<String, Provenienza> getProvenienzeOnde() {
		return provenienzeOnde;
	}

	public void setProvenienzeOnde(Map<String, Provenienza> provenienzeOnde) {
		this.provenienzeOnde = provenienzeOnde;
	}

	public Date getEventoCostieroInizio() {
		return eventoCostieroInizio;
	}

	public void setEventoCostieroInizio(Date eventoCostieroInizio) {
		this.eventoCostieroInizio = eventoCostieroInizio;
	}

	public Date getEventoCostieroFine() {
		return eventoCostieroFine;
	}

	public void setEventoCostieroFine(Date eventoCostieroFine) {
		this.eventoCostieroFine = eventoCostieroFine;
	}

	public int getEventoCostieroRow() {
		return eventoCostieroRow;
	}

	public void setEventoCostieroRow(int eventoCostieroRow) {
		this.eventoCostieroRow = eventoCostieroRow;
	}

	public Map<String, Tendenza> getTendenze() {
		return tendenze;
	}

	public void setTendenze(Map<String, Tendenza> tendenze) {
		this.tendenze = tendenze;
	}

	public List<SelectItem> getTendenzeSelect() {
		if (null == tendenzeSelect || tendenzeSelect.isEmpty())
			updateTendenze();
		return tendenzeSelect;
	}

	public void setTendenzeSelect(List<SelectItem> tendenzeSelect) {
		this.tendenzeSelect = tendenzeSelect;
	}

	/******/
	
	public Map<String, Altezza> getAltezzeStimaOnde() {
		return altezzeStimaOnde;
	}

	public void setAltezzeStimaOnde(Map<String, Altezza> altezzeStimaOnde) {
		this.altezzeStimaOnde = altezzeStimaOnde;
	}

	public List<SelectItem> getAltezzeStimaOndeSelect() {
		if (null == altezzeStimaOndeSelect || altezzeStimaOndeSelect.isEmpty())
			updateAltezzeStimaOnde();
		return altezzeStimaOndeSelect;
	}

	public void setAltezzeStimaOndeSelect(List<SelectItem> altezzeStimaOndeSelect) {
		this.altezzeStimaOndeSelect = altezzeStimaOndeSelect;
	}
	
	public Map<String, Provenienza> getDirezioneProvStimaOnde() {
		return direzioneProvStimaOnde;
	}

	public void setDirezioneProvStimaOnde(Map<String, Provenienza> direzioneProvStimaOnde) {
		this.direzioneProvStimaOnde = direzioneProvStimaOnde;
	}

	public List<SelectItem> getDirezioneProvStimaOndeSelect() {
		if (null == direzioneProvStimaOndeSelect || direzioneProvStimaOndeSelect.isEmpty())
			updateDirezioneProvStimaOnde();
		return direzioneProvStimaOndeSelect;
	}

	public void setAltezzeStimaOndeSelectt(List<SelectItem> direzioneProvStimaOndeSelect) {
		this.direzioneProvStimaOndeSelect = direzioneProvStimaOndeSelect;
	}
			
	/******/
	
	public boolean isEventoCostieroFormMessageRendered() {
		return eventoCostieroFormMessageRendered;
	}

	public void setEventoCostieroFormMessageRendered(
			boolean eventoCostieroFormMessageRendered) {
		this.eventoCostieroFormMessageRendered = eventoCostieroFormMessageRendered;
	}

	public boolean isAvvisoMeteoFormMessageRendered() {
		return avvisoMeteoFormMessageRendered;
	}

	public void setAvvisoMeteoFormMessageRendered(
			boolean avvisoMeteoFormMessageRendered) {
		this.avvisoMeteoFormMessageRendered = avvisoMeteoFormMessageRendered;
	}

	public String getAvvisoMeteoFormMessage() {
		return avvisoMeteoFormMessage;
	}

	public void setAvvisoMeteoFormMessage(String avvisoMeteoFormMessage) {
		this.avvisoMeteoFormMessage = avvisoMeteoFormMessage;
	}

	public String getEventoCostieroFormMessage() {
		return eventoCostieroFormMessage;
	}

	public void setEventoCostieroFormMessage(String eventoCostieroFormMessage) {
		this.eventoCostieroFormMessage = eventoCostieroFormMessage;
	}


	public List<SelectItem> getTipiAllegatoSelect() {
		if (null == tipiAllegatoSelect || tipiAllegatoSelect.isEmpty())
			updateTipiAllegato();
		return tipiAllegatoSelect;
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

	public void setTipiAllegatoSelect(List<SelectItem> tipiAllegatoSelect) {
		this.tipiAllegatoSelect = tipiAllegatoSelect;
	}

	public Map<String, TipoAllegato> getTipiAllegato() {
		return tipiAllegato;
	}

	public void setTipiAllegato(Map<String, TipoAllegato> tipiAllegato) {
		this.tipiAllegato = tipiAllegato;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public String getSelectedTipoAllegato() {
		return selectedTipoAllegato;
	}

	public void setSelectedTipoAllegato(String selectedTipoAllegato) {
		this.selectedTipoAllegato = selectedTipoAllegato;
	}


	@Override
	public void resetSearchParameters() {
	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(filtroEventiCostieri_inizioValiditaGiorno != null && filtroEventiCostieri_fineValiditaGiorno!=null){
			criterions.add(Restrictions.and(
					Restrictions.ge("inizioValidita", filtroEventiCostieri_inizioValiditaGiorno),
					Restrictions.le("fineValidita", filtroEventiCostieri_fineValiditaGiorno)));
		}
		else if(filtroEventiCostieri_inizioValiditaGiorno != null)
		{
			criterions.add(
					Restrictions.ge("inizioValidita", filtroEventiCostieri_inizioValiditaGiorno));
		} 
		else if(filtroEventiCostieri_fineValiditaGiorno!=null)
		{
			criterions.add(
					Restrictions.le("fineValidita", filtroEventiCostieri_fineValiditaGiorno));
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

	public Date getFiltroEventiCostieri_inizioValiditaGiorno() {
		return filtroEventiCostieri_inizioValiditaGiorno;
	}

	public void setFiltroEventiCostieri_inizioValiditaGiorno(
			Date filtroEventiCostieri_inizioValiditaGiorno) {
		this.filtroEventiCostieri_inizioValiditaGiorno = filtroEventiCostieri_inizioValiditaGiorno;
	}

	public Date getFiltroEventiCostieri_fineValiditaGiorno() {
		return filtroEventiCostieri_fineValiditaGiorno;
	}

	public void setFiltroEventiCostieri_fineValiditaGiorno(
			Date filtroEventiCostieri_fineValiditaGiorno) {
		this.filtroEventiCostieri_fineValiditaGiorno = filtroEventiCostieri_fineValiditaGiorno;
	}

	public PrevisioneMeteo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(PrevisioneMeteo selectedItem) {
		this.selectedItem = selectedItem;
	}

	public HtmlDataTable getAttachmentsDataTable() {
		return attachmentsDataTable;
	}

	public void setAttachmentsDataTable(HtmlDataTable attachmentsDataTable) {
		this.attachmentsDataTable = attachmentsDataTable;
	}

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public Allegato getAttachmentToRemove() {
		return attachmentToRemove;
	}

	public void setAttachmentToRemove(Allegato attachmentToRemove) {
		this.attachmentToRemove = attachmentToRemove;
	}

	public List<Allegato> getAttachmentsToRemoveList() {
		return attachmentsToRemoveList;
	}

	public void setAttachmentsToRemoveList(List<Allegato> attachmentsToRemoveList) {
		this.attachmentsToRemoveList = attachmentsToRemoveList;
	}

	public List<EventoCostiero> getEventiCostieriToRemoveList() {
		return eventiCostieriToRemoveList;
	}

	public void setEventiCostieriToRemoveList(
			List<EventoCostiero> eventiCostieriToRemoveList) {
		this.eventiCostieriToRemoveList = eventiCostieriToRemoveList;
	}

	public EventoCostiero getEventoCostieroToRemove() {
		return eventoCostieroToRemove;
	}

	public void setEventoCostieroToRemove(EventoCostiero eventoCostieroToRemove) {
		this.eventoCostieroToRemove = eventoCostieroToRemove;
	}


	public Map<Long, String> getPrevisioneAreeCoinvolte() {
		return previsioneAreeCoinvolte;
	}

	public void setPrevisioneAreeCoinvolte(Map<Long, String> previsioneAreeCoinvolte) {
		this.previsioneAreeCoinvolte = previsioneAreeCoinvolte;
	}

	public String getEventoCostieroProvenienzaOnda() {
		return eventoCostieroProvenienzaOnda;
	}

	public void setEventoCostieroProvenienzaOnda(String eventoCostieroProvenienzaOnda) {
		this.eventoCostieroProvenienzaOnda = eventoCostieroProvenienzaOnda;
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

	public float getEventoCostieroAltezzaOnda() {
		return eventoCostieroAltezzaOnda;
	}

	public void setEventoCostieroAltezzaOnda(float eventoCostieroAltezzaOnda) {
		this.eventoCostieroAltezzaOnda = eventoCostieroAltezzaOnda;
	}

	public float getEventoCostieroLivelloMare() {
		return eventoCostieroLivelloMare;
	}

	public void setEventoCostieroLivelloMare(float eventoCostieroLivelloMare) {
		this.eventoCostieroLivelloMare = eventoCostieroLivelloMare;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public float getPianuraVento() {
		return pianuraVento;
	}

	public float getPianuraVentoMax() {
		return pianuraVentoMax;
	}

	public String getPianuraDirezioneVento() {
		return pianuraDirezioneVento;
	}

	public float getPedemontanaVento() {
		return pedemontanaVento;
	}

	public float getPedemontanaVentoMax() {
		return pedemontanaVentoMax;
	}

	public String getPedemontanaDirezioneVento() {
		return pedemontanaDirezioneVento;
	}

	public void setPianuraVento(float pianuraVento) {
		this.pianuraVento = pianuraVento;
	}

	public void setPianuraVentoMax(float pianuraVentoMax) {
		this.pianuraVentoMax = pianuraVentoMax;
	}

	public void setPianuraDirezioneVento(String pianuraDirezioneVento) {
		this.pianuraDirezioneVento = pianuraDirezioneVento;
	}

	public void setPedemontanaVento(float pedemontanaVento) {
		this.pedemontanaVento = pedemontanaVento;
	}

	public void setPedemontanaVentoMax(float pedemontanaVentoMax) {
		this.pedemontanaVentoMax = pedemontanaVentoMax;
	}

	public void setPedemontanaDirezioneVento(String pedemontanaDirezioneVento) {
		this.pedemontanaDirezioneVento = pedemontanaDirezioneVento;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
	}



}
