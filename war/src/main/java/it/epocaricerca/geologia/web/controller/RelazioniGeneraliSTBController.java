package it.epocaricerca.geologia.web.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.PagingManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.RelazioniGeneraliSTBService;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.RelazioneGeneraleSTBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.model.MareggiateWizardStep;
import it.epocaricerca.geologia.web.model.RelazioneGeneraleSTBWizardStep;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.JSFUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;
import it.epocaricerca.geologia.web.validator.FileValidator;

public class RelazioniGeneraliSTBController extends FilterableHandler<RelazioneGeneraleSTB> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7837140083822405923L;

	private static Logger logger = Logger.getLogger(RelazioniGeneraliSTBController.class);


	private PagingManager pagingManager = JNDIUtils.getPagingManager();



	private boolean editMode = false;

	private boolean saveEnabled = false;

	private RelazioneGeneraleSTB selectedItem;
	private Long itemToAct;


	//MESSAGGI DI ERRORE
	private boolean relazioneFormMessageRendered = false;
	private String relazioneFormMessage;


	//VISUALIZZAZIONE relazione tecniche STB
	private long relazioneSTBid; 


	//WIZARD STATO
	private RelazioneGeneraleSTBWizardStep step;

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


	// FORM EDIT
	private RelazioneGeneraleSTBean relazioneGeneraleEdit;
	private Date inizioValidita;
	private Date fineValidita;
	private List<RelazioneSTB> filtered_relazioniSTB;
	private Map<Long, Boolean> checkedRelazioni = new HashMap<Long, Boolean>();

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;





	public void filtraRelazioniGenerali() {
		logger.info("filtraRelazioniGenerali "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		updateDataModel();
	}

	private void cleanForm() {
		relazioneGeneraleEdit = null;
		inizioValidita = null;
		fineValidita = null;
		saveEnabled = false;
		relazioneFormMessageRendered = false;
		checkedRelazioni.clear();
		this.allegati = new FileUploadBean(MAX_UPLOADS);
	}

	public String nuovaRelazioneGenerale() {
		cleanForm();
		step = RelazioneGeneraleSTBWizardStep.STEP1;
		relazioneGeneraleEdit = new RelazioneGeneraleSTBean();
		relazioneGeneraleEdit.setData(new Date());
		return "nuovaRelazioneGeneraleSTB";
	}

	public String annullaCreazione() {
		cleanForm();
		this.editMode = false;
		return "annullaCreazioneRelazioneGeneraleSTB";
	}


	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		updateDataModel();
	}

	public String previous()
	{


		switch (step) {
		case STEP1:

			break;

		case STEP2:{
			this.saveEnabled = false;
			step = RelazioneGeneraleSTBWizardStep.STEP1;

		}
		break;

		case STEP3:{
			this.saveEnabled = false;
			step = RelazioneGeneraleSTBWizardStep.STEP2;

		}
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
			if(!checkFormFields()){
				return "";
			}

			List<Criterion> criterions = new ArrayList<Criterion>();


			if(inizioValidita != null && fineValidita!=null){
				criterions.add(Restrictions.and(
						Restrictions.ge("inizioValidita", inizioValidita),
						Restrictions.le("fineValidita", fineValidita)));
			}

			criterions.add(Restrictions.isNull("relazioneGeneraleSTB"));

			//NestedCriterion stato = new NestedCriterion("stato", Restrictions.or(Restrictions.eq("nome", "Definitivo"),Restrictions.eq("nome", "Protocollato")));
			//criterions.add(stato);


			int count = pagingManager.countByCriteria(RelazioneSTB.class, criterions);
			filtered_relazioniSTB = pagingManager.findByCriteria(RelazioneSTB.class, 0, count, criterions, null);


			step = RelazioneGeneraleSTBWizardStep.STEP2;


		}
		break;

		case STEP2:
		{

			logger.info("Go To Step3");
			this.saveEnabled = true;
			step = RelazioneGeneraleSTBWizardStep.STEP3;
		}
		break;

		default:
			break;
		}

		return "next";
	}


	public String deleteItem() {
		//this.selectedItem = (RelazioneGeneraleSTB) dataModel.getRowData();
		RelazioneGeneraleSTBManager stbGeneraleManager = JNDIUtils.getRelazioneGeneraleSTBManager();
		try {
			stbGeneraleManager.remove(this.itemToAct);
		} catch (EntityNotFoundException e) {
			relazioneFormMessage = Messages.FORM_ERROR_PERSIST;
			relazioneFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "delete";
	}


	public String approvaItem() {
		RelazioneGeneraleSTBManager stbGeneraleManager = JNDIUtils.getRelazioneGeneraleSTBManager();
		try {
			stbGeneraleManager.updateStato(this.itemToAct, "Definitivo");
		} catch (Exception e) {
			relazioneFormMessage = Messages.FORM_ERROR_PERSIST;
			relazioneFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "";
	}

	public String protocollaItem() {
		RelazioneGeneraleSTBManager stbGeneraleManager = JNDIUtils.getRelazioneGeneraleSTBManager();
		try {
			stbGeneraleManager.updateStato(this.itemToAct, "Protocollato");
		} catch (Exception e) {
			relazioneFormMessage = Messages.FORM_ERROR_PERSIST;
			relazioneFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "";
	}

	public String viewItemDetails() {
		this.selectedItem = (RelazioneGeneraleSTB) dataModel.getRowData();
		RelazioneGeneraleSTBManager stbGeneraleManager = JNDIUtils.getRelazioneGeneraleSTBManager();
		try {
			this.selectedItem.setRelazioniSTB(stbGeneraleManager.getRelazioniSTB(this.selectedItem.getId()));
		} catch (EntityNotFoundException e) {
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}

		return "toRelazioneGeneraleDetails";
	}

	public String salvaRelazioneGenerale()
	{
		if(checkFormFields()){

			RelazioniGeneraliSTBService relazioniGeneraliSTBService = JNDIUtils.retrieveEJB(JNDIUtils.RelazioniGeneraliSTBServiceName, JNDIUtils.RelazioniGeneraliSTBServiceName);
			
			try {
				
				relazioniGeneraliSTBService.salvaRelazioneGenerale(relazioneGeneraleEdit, filtered_relazioniSTB, checkedRelazioni, allegati, tipiAllegato);


			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				
				relazioneFormMessage = Messages.FORM_ERROR_PERSIST;
				relazioneFormMessageRendered = true;

				return "";
			} catch (IOException e) {
				e.printStackTrace();
				
				relazioneFormMessage = Messages.FORM_ERROR_PERSIST;
				relazioneFormMessageRendered = true;
				
				return "";
			}

			updateDataModel();

			return "relazioneGeneraleSTBSalvata";
		}
		else {
			
			/* Modifico messaggio di file non valido perchè il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (relazioneFormMessageRendered == true) {
				if (relazioneFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					relazioneFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
		
			return "";
		}
	}


	public String viewRelazioneSTBDetails()
	{
		logger.info("viewRelazioneSTBDetails "+relazioneSTBid);

		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		DannoSTBManager dannoSTBManager = (DannoSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.DannoSTBManagerName, JNDIUtils.DannoSTBManagerName);
		RelazioneSTB temp = relazioneSTBManager.findItemById(relazioneSTBid);


		try {
			temp.setDanni(relazioneSTBManager.getDanniSTB(relazioneSTBid));
			for (DannoSTB d : temp.getDanni() ) {
				d.setAllegati(dannoSTBManager.getAllegati(d.getId()));
			}

			RelazioniTecnicheSTBController stbController = (RelazioniTecnicheSTBController)JSFUtils.getManagedBean("RelazioniTecnicheSTBController");
			stbController.setSelectedItem(temp);

			return "toRelazioneSTBDetails";
		} catch (EntityNotFoundException e) {

			//TODO gestione errori
			logger.error(e.getMessage());
		}

		return "";
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
			relazioneFormMessage = Messages.FORM_ERROR_IOFILE_EMPTY;
			relazioneFormMessageRendered = true;

			return;
		}

		FileValidator f= new FileValidator();	
		if (!f.Validate(item.getFile(), item.getFileName())) {
			relazioneFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
			relazioneFormMessageRendered = true;
			return;					
		}  else {
			relazioneFormMessageRendered = false;
			allegati.addAllegato(item);
		}
	}

	public void clearFileUpload(ActionEvent event) {
		relazioneFormMessageRendered = false;
		if(this.allegati.getUploadedFiles() == null || this.allegati.getUploadedFiles().isEmpty())
			return;

		if (getFileToClear() != null && !"".equals(getFileToClear())) {
		
			if (relazioneFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
				//logger.info("posso cancellare messagio - si riferisce a questo file" + relazioneFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				relazioneFormMessageRendered = false; 
			} else {
				//logger.info("non posso cancellare messagio - non si riferisce a questo file" + relazioneFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
				relazioneFormMessageRendered = true; 
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


	/** Private methods**/

	private boolean checkFormFields()
	{

		if(this.inizioValidita == null || this.fineValidita==null 
				|| !StringUtils.notEmpty(relazioneGeneraleEdit.getInformazioniGenerali()) 
				|| !StringUtils.notEmpty(relazioneGeneraleEdit.getInformazioniMeteo())){

			this.relazioneFormMessage = Messages.FORM_NOT_VALID;
			this.relazioneFormMessageRendered = true;

			return false;
		}
		
		if(this.allegati.checkTipiAllegatiPresenti() == false)
		{
			this.relazioneFormMessageRendered = true;
			this.relazioneFormMessage  = Messages.FORM_ATTACHMENTTYPE_MISSING;
			return false;
		}


		if((this.inizioValidita.after(this.fineValidita)))
		{
			relazioneFormMessageRendered = true;
			relazioneFormMessage = Messages.FORM_ERROR_DATE;
			return false;
		}

		/* File caricato non valido */
		if (relazioneFormMessageRendered == true) {
			if (relazioneFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}
		
		relazioneFormMessageRendered = false;
		return true;
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
					Restrictions.ge("data", filtro_inizioValiditaGiorno),
					Restrictions.le("data", filtro_fineValiditaGiorno)));
		}
		else if(filtro_inizioValiditaGiorno != null)
		{
			criterions.add(
					Restrictions.ge("data", filtro_inizioValiditaGiorno));
		} 
		else if(filtro_fineValiditaGiorno!=null)
		{
			criterions.add(
					Restrictions.le("data", filtro_fineValiditaGiorno));
		}
		return criterions;
	}

	protected List<Order> determineOrder()
	{
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add( Order.desc("data") );
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

	public RelazioneGeneraleSTBean getRelazioneGeneraleEdit() {
		return relazioneGeneraleEdit;
	}

	public void setRelazioneGeneraleEdit(
			RelazioneGeneraleSTBean relazioneGeneraleEdit) {
		this.relazioneGeneraleEdit = relazioneGeneraleEdit;
	}

	public Date getInizioValidita() {
		return inizioValidita;
	}

	public Date getFineValidita() {
		return fineValidita;
	}

	public void setInizioValidita(Date inizioValidita) {
		this.inizioValidita = inizioValidita;
	}

	public void setFineValidita(Date fineValidita) {
		this.fineValidita = fineValidita;
	}

	public List<RelazioneSTB> getFiltered_relazioniSTB() {
		return filtered_relazioniSTB;
	}

	public boolean isSaveEnabled() {
		return saveEnabled;
	}

	public void setSaveEnabled(boolean saveEnabled) {
		this.saveEnabled = saveEnabled;
	}

	public Map<Long, Boolean> getCheckedRelazioni() {
		return checkedRelazioni;
	}

	public void setCheckedRelazioni(Map<Long, Boolean> checkedRelazioni) {
		this.checkedRelazioni = checkedRelazioni;
	}

	public RelazioneGeneraleSTB getSelectedItem() {
		return selectedItem;
	}


	public long getRelazioneSTBid() {
		return relazioneSTBid;
	}

	public void setRelazioneSTBid(long relazioneSTBid) {
		this.relazioneSTBid = relazioneSTBid;
	}

	public boolean isRelazioneFormMessageRendered() {
		return relazioneFormMessageRendered;
	}

	public String getRelazioneFormMessage() {
		return relazioneFormMessage;
	}

	public Long getItemToAct() {
		return itemToAct;
	}

	public void setItemToAct(Long itemToAct) {
		this.itemToAct = itemToAct;
	}


	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
	}
	
	public List<SelectItem> getTipiAllegatoSelect() {
		if (null == tipiAllegatoSelect || tipiAllegatoSelect.isEmpty())
			updateTipiAllegato();
		return tipiAllegatoSelect;
	}

	public void setTipiAllegatoSelect(List<SelectItem> tipiAllegatoSelect) {
		this.tipiAllegatoSelect = tipiAllegatoSelect;
	}

}
