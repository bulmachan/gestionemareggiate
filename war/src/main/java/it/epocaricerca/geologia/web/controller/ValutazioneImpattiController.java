package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.ValutazioneImpattiService;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.LivelloCriticita;
import it.epocaricerca.geologia.model.MacroArea;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.model.TipoDanno;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.GenericUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;
import it.epocaricerca.geologia.web.validator.FileValidator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModelEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

public class ValutazioneImpattiController extends FilterableHandler<PrevisioneImpatto> {

	private static final long serialVersionUID = -731953462219833120L;

	private static Logger logger = Logger.getLogger(ValutazioneImpattiController.class);

	private static final String DATIGENERALI_TAB = "datigenerali_tab";
	private static final String TIPOLOGIEDANNO_TAB = "tipologiedanno_tab";

	private static final int MAX_UPLOADS = 10;
	private static final String FAKE_PATH = "C:\\fakepath\\";

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;
	private String filtro_localita;

	private boolean newItemFormMessageRendered = false;
	private String newItemFormMessage;

	private String selectedTab = DATIGENERALI_TAB;

	// FORM NUOVA VALUTAZIONE
	private String codiceValutazione;
	private String numeroAllerta;
	private Date dataAllerta;
	private Date inizioValidita;
	private Date fineValidita;
	private String descrizioneValutazione;
	private int tipoDannoTempRow;
	private List<PrevisioneDanno> previsioniDanni;
	private String valutazioneImpattiFormMessage;
	private boolean valutazioneImpattiFormMessageRendered = false;

	// FORM NUOVO DANNO
	private Map<TipoDanno, Boolean> checkedTipoDanno = new HashMap<TipoDanno, Boolean>();
	private List<TipoDanno> tipiDanno;
	private String nuovoDannoTipoDanno;

	//MacroArea
	private List<SelectItem> macroAreaSelect;
	private Map<String, MacroArea> macroArea;
	private String currentMacroArea;

	//Criticita'
	private List<SelectItem> livelloCriticitaSelect;
	private Map<String, LivelloCriticita> livelloCriticita;
	private String currentLivelloCriticita;



	//Allegati

	private String allegato_type;
	private FileUploadBean allegati;
	private FileUploadBean dannoAllegati;

	private Map<String, TipoAllegato> tipiAllegato;
	private List<SelectItem> tipiAllegatoSelect;
	private String fileToClear;


	private List<Allegato> allegatiTemp;

	private Allegato attachmentToRemove;
	private List<Allegato> attachmentsToRemoveList;


	private String nuovoDannoLocalitaDanno;


	private String tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
	private boolean tipologiaDannoFormMessageRendered = false;

	private Map<Long, String> valutazioneDannoPrevalente = new HashMap<Long, String>();

	// VIEW DETAILS
	private PrevisioneImpatto selectedItem;
	private int tipologieDanniRow;
	private Allegato attachmentToDownload;
	private String selectedDannoGeometries;
	private Long itemToDelete;

	// FORM EDIT
	private List<PrevisioneDanno> tipologieDannoToRemoveList;


	private boolean editMode = false;

	private PrevisioneDanno currentTipologiaDanno;


	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		filtro_localita = null;
		updateDataModel();
	}

	public void filtraValutazioneImpatti() {
		logger.info("filtraValutazioneImpatti "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		updateDataModel();
	}

	@Override
	public void updateDataModel()
	{
		super.updateDataModel();
		valutazioneDannoPrevalente.clear();

	}

	@Override
	public void rowSelected(DataModelEvent event)
	{
		PrevisioneImpatto previsoneImpatto = (PrevisioneImpatto) event.getRowData();
		PrevisioneImpattoManager manager = JNDIUtils.getPrevisioneImpattoManager();
		try {
			List<PrevisioneDanno> danni = manager.getDanni(previsoneImpatto.getId());

			Map<String, Integer > counter = new HashMap<String, Integer>();


			if(danni != null && danni.size() != 0){

				for (PrevisioneDanno danno : danni) {

					Set<TipoDanno> tipiDanno = danno.getTipiDanno();

					for (TipoDanno tipoDanno : tipiDanno) {

						String nomeDanno = tipoDanno.getNome();
						if(counter.containsKey(nomeDanno))
						{
							counter.put(nomeDanno, counter.get(nomeDanno) +1 );
						}
						else
							counter.put(nomeDanno, 1);
					}


				}

				Map<String, Integer>  sorted = GenericUtils.sortByValues(counter);

				if(sorted.size() != 0)
					valutazioneDannoPrevalente.put(previsoneImpatto.getId(), (String) sorted.keySet().toArray()[sorted.keySet().size() -1]);
			}
			else
				valutazioneDannoPrevalente.put(previsoneImpatto.getId(), "");


		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String nuovaValutazioneImpatti() {
		this.editMode = false;
		cleanForm();
		return "nuovaValutazioneImpatto";
	}

	public String nuovaTipologiaDanno() {
		dannoAllegati = new FileUploadBean(MAX_UPLOADS);

		currentTipologiaDanno = new PrevisioneDanno();
		tipologiaDannoFormMessageRendered = false;
		return "nuovaTipologiaDanno";
	}

	public String salvaTipologiaDanno() {
		if ( !checkFormFields() ) {
		
			/* Capire perchË salvaTipologiaDanno non ricarica la sezione upload e gli altri Salva sÏ, */
			/* Modifico messaggio di file non valido perchË il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato 
			if (tipologiaDannoFormMessageRendered == true) {
				if (tipologiaDannoFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					tipologiaDannoFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}*/
		
			return "";
		}
		else {
			tipologiaDannoFormMessageRendered = false;
			for (Allegato allegato : currentTipologiaDanno.getAllegati()) {

				allegato.setTipo(JNDIUtils.getTipoAllegatoManager().findItemByName(dannoAllegati.getTipiAllegatiUploadedFiles().get(allegato.getNome())) );
			}


			currentTipologiaDanno.setMacroArea(JNDIUtils.getMacroAreaManager().findItemByName(currentMacroArea));
			currentTipologiaDanno.setLivello_criticita(this.getLivelloCriticita().get(this.currentLivelloCriticita));

			Set<TipoDanno> toAdd = new HashSet<TipoDanno>();
			Set<TipoDanno> danni = this.checkedTipoDanno.keySet();
			for (TipoDanno tipoDanno : danni) {
				if(this.getCheckedTipoDanno().get(tipoDanno))
					toAdd.add(tipoDanno);
			}


			currentTipologiaDanno.setTipiDanno(toAdd);

			logger.info("Salvo Temporaneamente Danno "+currentTipologiaDanno.getMacroArea().getNome());

			getPrevisioniDanni().add(currentTipologiaDanno);

			selectedTab = TIPOLOGIEDANNO_TAB;
			return "tipologiaDannoSalvato";
		}


	}


	private boolean checkFormFields() {
		
		if ( !dannoAllegati.checkTipiAllegatiPresenti() ) {
			tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			tipologiaDannoFormMessageRendered = true;
			return false;
		}
		
		if(currentMacroArea == null || currentLivelloCriticita == null)
		{
			tipologiaDannoFormMessage = Messages.FORM_NOT_VALID;
			tipologiaDannoFormMessageRendered = true;
			return false;
		}
		
		/* File caricato non valido */
		if (tipologiaDannoFormMessageRendered == true) {
			if (tipologiaDannoFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}
				
		tipologiaDannoFormMessageRendered = false;
		return true;
	}

	public String annullaCreazioneNuovoDanno() {
		return "annullaCreazioneNuovoDanno";
	}

	public void deleteTipoDannoTemp() {
		PrevisioneDanno tdbToRemove = previsioniDanni.get(tipoDannoTempRow);
		if ( editMode && null!=tdbToRemove.getId() ) {
			// è un danno da DB, lo aggiungo alla lista di danni da rimuovere mediante API
			getTipologieDannoToRemoveList().add(tdbToRemove);
		}
		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		previsioniDanni.remove(tipoDannoTempRow);
	}

	public String annullaCreazioneValutazioneImpatto() {
		cleanForm();
		this.editMode = false;
		return "annullaCreazioneValutazioneImpatto";
	}

	public String salvaValutazioneImpatti() {
		if (checkValutazioneImpattiFormField()) {

			valutazioneImpattiFormMessageRendered = false;

			try {

				
				ValutazioneImpattiService valutazioneImpattiService = JNDIUtils.retrieveEJB(JNDIUtils.ValutazioneImpattiServiceName, JNDIUtils.ValutazioneImpattiServiceName);
				
				
				
				if (editMode) {
					selectedItem.setCodice(codiceValutazione);
					selectedItem.setNumeroAllerta(numeroAllerta);
					selectedItem.setDataAllerta(dataAllerta);
					selectedItem.setInizioValidita(inizioValidita);
					selectedItem.setFineValidita(fineValidita);
					selectedItem.setDescrizione(descrizioneValutazione);
					valutazioneImpattiService.salvaValutazioneImpatti(editMode, selectedItem, getTipologieDannoToRemoveList(), tipiAllegato, allegati, attachmentsToRemoveList);
	
				} 
				else {
					PrevisioneImpatto previsioneImpatto = new PrevisioneImpatto();
					previsioneImpatto.setCodice(codiceValutazione);
					previsioneImpatto.setNumeroAllerta(numeroAllerta);
					previsioneImpatto.setDataAllerta(dataAllerta);
					previsioneImpatto.setInizioValidita(inizioValidita);
					previsioneImpatto.setFineValidita(fineValidita);
					previsioneImpatto.setDescrizione(descrizioneValutazione);
					previsioneImpatto.setDanni(getPrevisioniDanni());					
					valutazioneImpattiService.salvaValutazioneImpatti(editMode, previsioneImpatto, getTipologieDannoToRemoveList(), tipiAllegato, allegati, attachmentsToRemoveList);
				}


			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				valutazioneImpattiFormMessage = Messages.FORM_ERROR_PERSIST;
				valutazioneImpattiFormMessageRendered = true;
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				valutazioneImpattiFormMessage = Messages.FORM_ERROR_PERSIST;
				valutazioneImpattiFormMessageRendered = true;
				return "";
			}
			//updateDataModel();
			pulisciFiltri();
			return "valutazioneImpattiSalvata";
		}
		// la form non ècompleta
		else {
			/* Modifico messaggio di file non valido perchË il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (valutazioneImpattiFormMessageRendered == true) {
				if (valutazioneImpattiFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					valutazioneImpattiFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
		
		
			return "";
		}

	}

	private boolean checkValutazioneImpattiFormField() {
		if(this.dataAllerta == null || this.inizioValidita == null || this.fineValidita==null || !StringUtils.notEmpty(codiceValutazione) || !StringUtils.notEmpty(numeroAllerta)){

			valutazioneImpattiFormMessage = Messages.FORM_NOT_VALID;
			valutazioneImpattiFormMessageRendered = true;

			return false;
		}


		if((this.inizioValidita.after(this.fineValidita)))
		{
			valutazioneImpattiFormMessage = Messages.FORM_ERROR_DATE;
			valutazioneImpattiFormMessageRendered = true;
			return false;
		}
		
		if ( !allegati.checkTipiAllegatiPresenti() ) {
			valutazioneImpattiFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			valutazioneImpattiFormMessageRendered = true;
			return false;
		}

		/* File caricato non valido */
		if (valutazioneImpattiFormMessageRendered == true) {
			if (valutazioneImpattiFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}
		
		return true;
	}

	public String editTipoDannoTemp() {
		return "";
	}

	public String deleteItem() {
		PrevisioneImpattoManager previsioneImpattoManager = JNDIUtils.getPrevisioneImpattoManager();
		try {
			previsioneImpattoManager.remove(this.getItemToDelete());
		} catch (Exception e) {
			newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			newItemFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "delete";
	}


	public String editItemDetails() {
		this.editMode = true;
		cleanForm();
		selectedTab = DATIGENERALI_TAB;
		this.selectedItem = (PrevisioneImpatto) dataModel.getRowData();
		PrevisioneImpattoManager previsioneImpattoManager = JNDIUtils.getPrevisioneImpattoManager();

		try {
			this.selectedItem.setDanni(previsioneImpattoManager.getDanni(this.selectedItem.getId()));
			this.previsioniDanni = this.selectedItem.getDanni();
			allegatiTemp = new ArrayList<Allegato>(this.selectedItem.getAllegati());
		} catch (EntityNotFoundException e) {
			//TODO gestione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}
		
		if (null != attachmentsToRemoveList) {
			attachmentsToRemoveList.clear();
		}
		else {
			attachmentsToRemoveList = new ArrayList<Allegato>();
		}
	
		fillForm();
		return "editItemDetails";
	}

	public void fillForm() {
		// tab generale
		codiceValutazione = this.selectedItem.getCodice();
		numeroAllerta = this.selectedItem.getNumeroAllerta();
		dataAllerta = this.selectedItem.getDataAllerta();
		inizioValidita = this.selectedItem.getInizioValidita();
		fineValidita = this.selectedItem.getFineValidita();
		descrizioneValutazione = this.selectedItem.getDescrizione();
	}

	public void cleanForm() {
		this.codiceValutazione = "";
		this.numeroAllerta = "";
		this.dataAllerta = null;
		this.inizioValidita = null;
		this.valutazioneImpattiFormMessageRendered = false;
		this.checkedTipoDanno.clear();
		this.fineValidita = null;
		this.descrizioneValutazione = "";
		this.currentMacroArea = null;
		this.currentTipologiaDanno = null;
		this.allegati = new FileUploadBean(MAX_UPLOADS);

		if (null != this.getPrevisioniDanni() )
			this.getPrevisioniDanni().clear();
		if (null != tipologieDannoToRemoveList)
			tipologieDannoToRemoveList.clear();
	}

	public String toValutazioniImpattiHome() {
		return "toValutazioniImpattiHome";
	}

	public String viewItemDetails() {
		this.selectedItem = (PrevisioneImpatto) dataModel.getRowData();
		PrevisioneImpattoManager previsioneImpattoManager = JNDIUtils.getPrevisioneImpattoManager();
		try {
			this.selectedItem.setDanni(previsioneImpattoManager.getDanni(this.selectedItem.getId()));
		} catch (EntityNotFoundException e) {
			//TODO gestione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}
		return "toItemDetails";
	}

	
	
	public void fileUploadListener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();

		if(item.getFileSize() == 0)
		{
			tipologiaDannoFormMessage = Messages.FORM_ERROR_IOFILE_EMPTY;
			tipologiaDannoFormMessageRendered = true;
			return;
		}
		
		FileValidator f= new FileValidator();	
		if (!f.Validate(item.getFile(), item.getFileName())) {
			if(this.allegato_type.equals("dannoAllegati")) {
				tipologiaDannoFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; //+ mimeType
				tipologiaDannoFormMessageRendered = true;
				
			} else {
				valutazioneImpattiFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; //+ mimeType
				valutazioneImpattiFormMessageRendered = true;
			}			
		}  else {
			Allegato allegato = new Allegato();
			allegato.setFile(FileUtils.readFileToByteArray(item.getFile()));
			allegato.setNome(item.getFileName());
			allegato.setContentType(item.getContentType());

			if(this.allegato_type.equals("dannoAllegati")){
				tipologiaDannoFormMessageRendered = false;
				currentTipologiaDanno.getAllegati().add(allegato);
				dannoAllegati.addAllegato(item);
				
			} else {
				valutazioneImpattiFormMessageRendered = false;
				allegati.addAllegato(item);
				
			}
		}
		
	}


	public void clearFileUpload(ActionEvent event) {
		//tipologiaDannoFormMessageRendered = false;
		if(this.allegato_type.equals("dannoAllegati")){
			if(dannoAllegati.getUploadedFiles() == null || dannoAllegati.getUploadedFiles().isEmpty())
				return;
		}
		else {
			if(allegati.getUploadedFiles() == null || allegati.getUploadedFiles().isEmpty())
				return;
		}
		if (getFileToClear() != null && !"".equals(getFileToClear())) {
	
			Allegato allegato = new Allegato();
			allegato.setNome(this.getFileToClear().replace(FAKE_PATH, ""));

			if(this.allegato_type.equals("dannoAllegati")) {
			
				if (tipologiaDannoFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
					//logger.info("posso cancellare messagio - si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					tipologiaDannoFormMessageRendered = false; 
				} else {
					//logger.info("non posso cancellare messagio - non si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					tipologiaDannoFormMessageRendered = true; 
				}
			
				currentTipologiaDanno.getAllegati().remove(allegato);
				dannoAllegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
			} else {
				if (valutazioneImpattiFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
					//logger.info("posso cancellare messagio - si riferisce a questo file" + valutazioneImpattiFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					valutazioneImpattiFormMessageRendered = false; 
				} else {
					//logger.info("non posso cancellare messagio - non si riferisce a questo file" + valutazioneImpattiFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					valutazioneImpattiFormMessageRendered = true; 
				}

				allegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
			}

		} else {
			logger.info("clearing all files "+this.allegato_type);
			if(this.allegato_type.equals("dannoAllegati")){
				currentTipologiaDanno.getAllegati().clear();
				dannoAllegati.clear();
			}
			else
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

	public void downloadAttachment() {
		AttachmentUtils.downloadAttachment(this.getAttachmentToDownload());
	}

	/** FilterableHandler methods**/

	@Override
	public void resetSearchParameters() {

	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();

		if(filtro_localita != null){
			NestedCriterion celle = new NestedCriterion("danni", 
					new NestedCriterion("macroArea",Restrictions.eq("nome", filtro_localita )));
			criterions.add(celle);
		}

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
		orders.add( Order.desc("dataAllerta") );
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

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public PrevisioneImpatto getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(PrevisioneImpatto selectedItem) {
		this.selectedItem = selectedItem;
	}

	public boolean isNewItemFormMessageRendered() {
		return newItemFormMessageRendered;
	}

	public void setNewItemFormMessageRendered(boolean newItemFormMessageRendered) {
		this.newItemFormMessageRendered = newItemFormMessageRendered;
	}

	public String getNewItemFormMessage() {
		return newItemFormMessage;
	}

	public void setNewItemFormMessage(String newItemFormMessage) {
		this.newItemFormMessage = newItemFormMessage;
	}

	public List<TipoDanno> getTipiDanno() {
		if (null == tipiDanno || tipiDanno.isEmpty() )
			updateTipiDanno();
		return tipiDanno;
	}

	private void updateTipiDanno() {
		tipiDanno =  JNDIUtils.getTipoDannoManager().selectAll();

	}

	public String getNuovoDannoTipoDanno() {
		return nuovoDannoTipoDanno;
	}

	public void setNuovoDannoTipoDanno(String nuovoDannoTipoDanno) {
		this.nuovoDannoTipoDanno = nuovoDannoTipoDanno;
	}

	public List<SelectItem> getLivelloCriticitaSelect() {
		if ( null == livelloCriticitaSelect || livelloCriticitaSelect.isEmpty() )
			updateLivelloCriticita();
		return livelloCriticitaSelect;
	}

	private void updateLivelloCriticita() {
		livelloCriticita = new LinkedHashMap<String, LivelloCriticita>();
		livelloCriticitaSelect = new ArrayList<SelectItem>();
		List<LivelloCriticita> livelli_criticita = JNDIUtils.getLivelloCriticitaManager().selectAll();
		
		livelloCriticitaSelect.add(new SelectItem(null,"-"));
		for (LivelloCriticita l : livelli_criticita) {
			livelloCriticita.put(l.getNome(), l);
			livelloCriticitaSelect.add(new SelectItem(l.getNome()));
		}
	}

	public List<SelectItem> getMacroAreaSelect() {
		if ( null == macroAreaSelect || macroAreaSelect.isEmpty() )
			updateMacroLocalita();
		return macroAreaSelect;
	}

	private void updateMacroLocalita() {
		macroArea = new LinkedHashMap<String, MacroArea>();
		macroAreaSelect = new ArrayList<SelectItem>();
		List<MacroArea> localitaFromDB = JNDIUtils.getMacroAreaManager().selectAll();
		
		macroAreaSelect.add(new SelectItem(null,"-"));
		for (MacroArea l : localitaFromDB) {
			macroArea.put(l.getNome(), l);
			macroAreaSelect.add(new SelectItem(l.getNome()));
		}
	}

	public void setMacroAreaSelect(List<SelectItem> macroAreaSelect) {
		this.macroAreaSelect = macroAreaSelect;
	}

	public String getNuovoDannoLocalitaDanno() {
		return nuovoDannoLocalitaDanno;
	}

	public void setNuovoDannoLocalitaDanno(String nuovoDannoLocalitaDanno) {
		this.nuovoDannoLocalitaDanno = nuovoDannoLocalitaDanno;
	}

	public Map<String, MacroArea> getMacroArea() {
		return macroArea;
	}

	public void setMacroArea(Map<String, MacroArea> macroArea) {
		this.macroArea = macroArea;
	}

	public int getTipoDannoTempRow() {
		return tipoDannoTempRow;
	}

	public void setTipoDannoTempRow(int tipoDannoTempRow) {
		this.tipoDannoTempRow = tipoDannoTempRow;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
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

	public String getCodiceValutazione() {
		return codiceValutazione;
	}

	public void setCodiceValutazione(String codiceValutazione) {
		this.codiceValutazione = codiceValutazione;
	}
	
	public String getNumeroAllerta() {
		return numeroAllerta;
	}

	public void setNumeroAllerta(String numeroAllerta) {
		this.numeroAllerta = numeroAllerta;
	}
	
	public Date getDataAllerta() {
		return dataAllerta;
	}

	public void setDataAllerta(Date dataAllerta) {
		this.dataAllerta = dataAllerta;
	}

	public Date getInizioValidita() {
		return inizioValidita;
	}

	public void setInizioValidita(Date inizioValidita) {
		this.inizioValidita = inizioValidita;
	}

	public Date getFineValidita() {
		return fineValidita;
	}

	public void setFineValidita(Date fineValidita) {
		this.fineValidita = fineValidita;
	}

	public String getDescrizioneValutazione() {
		return descrizioneValutazione;
	}

	public void setDescrizioneValutazione(String descrizioneValutazione) {
		this.descrizioneValutazione = descrizioneValutazione;
	}

	public String getTipologiaDannoFormMessage() {
		return tipologiaDannoFormMessage;
	}

	public void setTipologiaDannoFormMessage(String tipologiaDannoFormMessage) {
		this.tipologiaDannoFormMessage = tipologiaDannoFormMessage;
	}

	public boolean isTipologiaDannoFormMessageRendered() {
		return tipologiaDannoFormMessageRendered;
	}

	public void setTipologiaDannoFormMessageRendered(
			boolean tipologiaDannoFormMessageRendered) {
		this.tipologiaDannoFormMessageRendered = tipologiaDannoFormMessageRendered;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getValutazioneImpattiFormMessage() {
		return valutazioneImpattiFormMessage;
	}

	public void setValutazioneImpattiFormMessage(
			String valutazioneImpattiFormMessage) {
		this.valutazioneImpattiFormMessage = valutazioneImpattiFormMessage;
	}

	public boolean isValutazioneImpattiFormMessageRendered() {
		return valutazioneImpattiFormMessageRendered;
	}

	public void setValutazioneImpattiFormMessageRendered(
			boolean valutazioneImpattiFormMessageRendered) {
		this.valutazioneImpattiFormMessageRendered = valutazioneImpattiFormMessageRendered;
	}

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}

	public List<PrevisioneDanno> getTipologieDannoToRemoveList() {
		if (null == tipologieDannoToRemoveList)
			tipologieDannoToRemoveList = new ArrayList<PrevisioneDanno>();
		return tipologieDannoToRemoveList;
	}

	public void setTipologieDannoToRemoveList(
			List<PrevisioneDanno> tipologieDannoToRemoveList) {
		this.tipologieDannoToRemoveList = tipologieDannoToRemoveList;
	}

	public Map<Long, String> getValutazioneDannoPrevalente() {
		return valutazioneDannoPrevalente;
	}

	public void setValutazioneDannoPrevalente(
			Map<Long, String> valutazioneDannoPrevalente) {
		this.valutazioneDannoPrevalente = valutazioneDannoPrevalente;
	}

	public String getFiltro_localita() {
		return filtro_localita;
	}

	public void setFiltro_localita(String filtro_localita) {
		this.filtro_localita = filtro_localita;
	}

	public String getSelectedDannoGeometries() {
		return selectedDannoGeometries;
	}

	public void setSelectedDannoGeometries(String selectedDannoGeometries) {
		this.selectedDannoGeometries = selectedDannoGeometries;
	}

	public int getTipologieDanniRow() {
		return tipologieDanniRow;
	}

	public void setTipologieDanniRow(int tipologieDanniRow) {
		this.tipologieDanniRow = tipologieDanniRow;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public String getCurrentMacroArea() {
		return currentMacroArea;
	}

	public void setCurrentMacroArea(String currentMacroArea) {
		this.currentMacroArea = currentMacroArea;
	}

	public PrevisioneDanno getCurrentTipologiaDanno() {
		return currentTipologiaDanno;
	}

	public void setCurrentTipologiaDanno(PrevisioneDanno currentTipologiaDanno) {
		this.currentTipologiaDanno = currentTipologiaDanno;
	}

	public List<PrevisioneDanno> getPrevisioniDanni() {
		if (null == previsioniDanni) {
			previsioniDanni = new ArrayList<PrevisioneDanno>();
		}
		return previsioniDanni;
	}

	public void setPrevisioniDanni(List<PrevisioneDanno> previsioniDanni) {
		this.previsioniDanni = previsioniDanni;
	}

	public Map<TipoDanno, Boolean> getCheckedTipoDanno() {
		return checkedTipoDanno;
	}

	public String getCurrentLivelloCriticita() {
		return currentLivelloCriticita;
	}

	public void setCurrentLivelloCriticita(String currentLivelloCriticita) {
		this.currentLivelloCriticita = currentLivelloCriticita;
	}

	public Map<String, LivelloCriticita> getLivelloCriticita() {
		return livelloCriticita;
	}

	public FileUploadBean getDannoAllegati() {
		return dannoAllegati;
	}

	public void setDannoAllegati(FileUploadBean dannoAllegati) {
		this.dannoAllegati = dannoAllegati;
	}

	public String getAllegato_type() {
		return allegato_type;
	}

	public void setAllegato_type(String allegato_type) {
		this.allegato_type = allegato_type;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
	}

	public List<Allegato> getAllegatiTemp() {
		return allegatiTemp;
	}

	public Allegato getAttachmentToRemove() {
		return attachmentToRemove;
	}

	public void setAllegatiTemp(List<Allegato> allegatiTemp) {
		this.allegatiTemp = allegatiTemp;
	}

	public void setAttachmentToRemove(Allegato attachmentToRemove) {
		this.attachmentToRemove = attachmentToRemove;
	}

	public List<Allegato> getAttachmentsToRemoveList() {
		return attachmentsToRemoveList;
	}

}
