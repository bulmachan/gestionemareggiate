package it.epocaricerca.geologia.web.controller;


import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.STBManager;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.ejb.dao.StatoRelazioneManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.RelazioniTecnicheSTBService;
import it.epocaricerca.geologia.ejb.tdo.DannoSTBBean;
import it.epocaricerca.geologia.ejb.tdo.FileAllegatoBean;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.STB;
import it.epocaricerca.geologia.model.Fonte;
import it.epocaricerca.geologia.model.StatoRelazione;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.BeanUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;
import it.epocaricerca.geologia.web.validator.FileValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public class RelazioniTecnicheSTBController extends FilterableHandler<RelazioneSTB> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2786199993443284426L;

	private static final int MAX_UPLOADS = 10;
	private static final String FAKE_PATH = "C:\\fakepath\\";

	private static Logger logger = Logger.getLogger(RelazioniTecnicheSTBController.class);


	private static final String IMPATTIGENERALI_TAB = "impattigenerali_tab";
	private static final String IMPATTILOCALI_TAB = "impattilocali_tab";

	private String selectedTab = IMPATTIGENERALI_TAB;

	private List<SelectItem> stbSelect;
	private List<SelectItem> localitaSelect;
	private List<SelectItem> fontiSelect;

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;
	private String filtro_stb;
	private Localita filtro_localita;

	private String formMessage;
	private boolean formMessageRendered = false;

	// TAB IMPATTI GENERALI
	private String impattiGeneraliCodiceValutazione;
	private Date impattiGeneraliInizioValidita;
	private Date impattiGeneraliFineValidita;
	private String impattiGeneraliDescrizione;
	private List<SelectItem> statiRelazioneSelect;
	private Map<String, StatoRelazione> statiRelazione;
	private String statoRelazione;
	private Map<String, STB> stbs;
	private String stb;
	private Map<String, Fonte> fonti;
	private String fonte;
	
	// TAB IMPATTI LOCALI
	private int tipoDannoTempRow;
	private List<DannoSTBBean> tipiDannoTemp;

	//TAB ALLEGATI GENERICI
	private FileUploadBean allegati;
	private String allegato_type;

	private List<Allegato> allegatiTemp;

	private Allegato attachmentToRemove;
	private List<Allegato> attachmentsToRemoveList;

	// FORM NUOVO DANNO STB
	private DannoSTBBean currentTipologiaDanno;
	private int uploadsAvailable = MAX_UPLOADS;
	private Map<String, File> uploadedFiles = new LinkedHashMap<String, File>();
	private boolean tipoAllegatiBoxRendered;
	private Map<String, String> tipiAllegatiUploadedFiles;
	private List<String> uploadedFilesKeys;
	private String fileToClear;
	private Map<String, TipoAllegato> tipiAllegato;
	private List<SelectItem> tipiAllegatoSelect;
	private String tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
	private boolean tipologiaDannoFormMessageRendered = false;


	// VIEW DETAILS
	private RelazioneSTB selectedItem;
	private boolean newItemFormMessageRendered = false;
	private String newItemFormMessage;
	private Allegato attachmentToDownload;

	private String selectedDannoGeometries;
	private int tipologieDanniRow;
	private Long itemToAct;


	//EDIT MODE
	private boolean editMode = false;

	private boolean geometryOptional = true;
	
	private List<DannoSTBBean> tipologieDannoToRemoveList;


	public String editItemDetails() {
		cleanImpattiGeneraliForm();
		cleanImpattiLocaliForm();
		
		selectedTab = IMPATTIGENERALI_TAB;
		this.selectedItem = (RelazioneSTB) dataModel.getRowData();
		
		this.allegatiTemp = new ArrayList<Allegato>(this.selectedItem.getAllegati());

		tipologieDannoToRemoveList = new ArrayList<DannoSTBBean>();

		if (null != attachmentsToRemoveList) {
			attachmentsToRemoveList.clear();
		}
		else {
			attachmentsToRemoveList = new ArrayList<Allegato>();
		}


		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		DannoSTBManager dannoSTBManager = (DannoSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.DannoSTBManagerName, JNDIUtils.DannoSTBManagerName);

		try {
			this.selectedItem.setDanni(relazioneSTBManager.getDanniSTB(this.selectedItem.getId()));
			this.tipiDannoTemp = BeanUtils.convert(this.selectedItem.getDanni(),dannoSTBManager );
		} catch (Exception e) {
			logger.error(e.getMessage());
			updateDataModel();
			return "";
		}

		return editRelazioneTecnica();
	}


	public String editRelazioneTecnica() {
		this.editMode = true;
		
		this.impattiGeneraliCodiceValutazione = this.selectedItem.getCodice();
		this.impattiGeneraliInizioValidita = this.selectedItem.getInizioValidita();
		this.impattiGeneraliFineValidita = this.selectedItem.getFineValidita();
		this.stb = this.selectedItem.getStb().getNome();
		
		if (null != this.selectedItem.getFonte()) {
			this.fonte = this.selectedItem.getFonte().getNome();
		} else {
			this.fonte = "-";
		}
				
		this.impattiGeneraliDescrizione = this.selectedItem.getDescrizione();
		

		return "editRelazioneTecnica";
	}


	public void filtraRelazioniTecniche() {
		logger.info("filtraRelazioniTecniche "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		updateDataModel();
	}


	private void cleanImpattiGeneraliForm() {
		impattiGeneraliCodiceValutazione = null;
		impattiGeneraliInizioValidita = null;
		impattiGeneraliFineValidita = null;
		fonte = null;
		statoRelazione = "";
		stb = "";
		impattiGeneraliDescrizione="";
		
		if( null!=tipiDannoTemp && !tipiDannoTemp.isEmpty() )
			tipiDannoTemp.clear();

		if(null!=this.allegatiTemp && !this.allegatiTemp.isEmpty())
			allegatiTemp.clear();
		
		this.allegati = new FileUploadBean(MAX_UPLOADS);
	}

	private void cleanImpattiLocaliForm() {
		currentTipologiaDanno = new DannoSTBBean();
	}

	public String nuovaRelazioneTecnica() {
		this.editMode = false;
		selectedTab = IMPATTIGENERALI_TAB;
		cleanImpattiGeneraliForm();
		cleanImpattiLocaliForm();
		return "nuovaRelazioneTecnica";
	}

	public String annullaCreazioneRelazioneTecnica() {
		cleanImpattiGeneraliForm();
		cleanImpattiLocaliForm();
		return "annullaCreazioneNuovoRelazione";
	}

	public String nuovoImpattoLocale() {
		cleanImpattiLocaliForm();
		setCurrentTipologiaDanno(new DannoSTBBean());
		getTipiAllegatiUploadedFiles().clear();
		tipologiaDannoFormMessageRendered = false;
		return "nuovoImpattoLocale";
	}

	public String annullaCreazioneNuovoImpatto() {
		cleanImpattiLocaliForm();
		return "annullaCreazioneNuovoImpatto";
	}

	public void deleteTipoDannoTemp() {
		DannoSTBBean tdbToRemove = tipiDannoTemp.get(tipoDannoTempRow);
		if ( editMode && null!=tdbToRemove.getId() ) {
			// � un danno da DB, lo aggiungo alla lista di danni da rimuovere mediante API
			getTipologieDannoToRemoveList().add(tdbToRemove);
		}


		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		tipiDannoTemp.remove(tipoDannoTempRow);
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
			
			if(this.allegato_type.equals("dannoAllegati")){
				tipologiaDannoFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
				tipologiaDannoFormMessageRendered = true;
			} else {
				formMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
				formMessageRendered = true;
			}
			return;			
		}  else {
			if(this.allegato_type.equals("dannoAllegati")){
				//uploadedFiles.put(item.getFileName(), item.getFile());
				currentTipologiaDanno.getFileAllegati().add(new FileAllegatoBean(item.getFile(), null, item.getFileName()));
				getTipiAllegatiUploadedFiles().put(item.getFileName(), null);
				uploadsAvailable--;
			}else {		
				allegati.addAllegato(item);
			}
		}
	}

	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		filtro_stb = null;
		filtro_localita = null;
		updateDataModel();
	}

	public void clearFileUpload(ActionEvent event) {
		tipologiaDannoFormMessageRendered = false;
		formMessageRendered = false;
		
		/*if(uploadedFiles == null || uploadedFiles.isEmpty())
			return;
		}*/
		
		if(this.allegato_type.equals("dannoAllegati")){
			if(uploadedFiles == null || uploadedFiles.isEmpty())
				return;
		}
		else {
			if(allegati.getUploadedFiles() == null || allegati.getUploadedFiles().isEmpty())
				return;
		}
		
		if (getFileToClear() != null && !"".equals(getFileToClear())) {
			logger.info(this.getFileToClear().replace(FAKE_PATH, ""));
			if(this.allegato_type.equals("dannoAllegati")){

				if(tipologiaDannoFormMessageRendered) {
					if (tipologiaDannoFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
						logger.info("posso cancellare messagio - si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
						tipologiaDannoFormMessageRendered = false; 
					} else {
						logger.info("non posso cancellare messagio - non si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
						tipologiaDannoFormMessageRendered = true; 
					}
				}
			
				uploadedFiles.remove(this.getFileToClear().replace(FAKE_PATH, ""));
				tipiAllegatiUploadedFiles.remove(this.getFileToClear().replace(
						FAKE_PATH, ""));
				uploadsAvailable++;
			} else {
				if (formMessageRendered) {
					if (formMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
						logger.info("posso cancellare messagio - si riferisce a questo file" + formMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
						formMessageRendered = false; 
					} else {
						logger.info("non posso cancellare messagio - non si riferisce a questo file" + formMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
						formMessageRendered = true; 
					}
				}
			
				logger.info("filetoclear "+ this.getFileToClear().replace(FAKE_PATH, ""));
				allegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
			}
		} else {
		
			if(this.allegato_type.equals("dannoAllegati")){
				logger.info("clearing all files");
				uploadedFiles.clear();
				tipiAllegatiUploadedFiles.clear();
				uploadsAvailable = MAX_UPLOADS;
			}
			else {
				logger.info("E");
				allegati.clear();
			}
		}
	}
	
	public void removeExistingAttachment() {

		if ( editMode && attachmentToRemove.getId() >= 1  ) {
			attachmentsToRemoveList.add(attachmentToRemove);
		}
		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		boolean removed = allegatiTemp.remove(attachmentToRemove);
		logger.info("removeExistingAttachment "+attachmentToRemove.getId()+" "+removed);

	}

	public String salvaImpatto() {
		if ( !checkTipiAllegatiPresenti() ) {
			tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			tipologiaDannoFormMessageRendered = true;
			return "";
		}
		else if(currentTipologiaDanno.getLocalita() == null)
		{
			tipologiaDannoFormMessage = Messages.FORM_NOT_VALID;
			tipologiaDannoFormMessageRendered = true;
			return "";
		}
		else {
			if(!geometryOptional) {
				if(currentTipologiaDanno.getGeometryText() == null || !StringUtils.notEmpty(currentTipologiaDanno.getGeometryText()))
				{
					tipologiaDannoFormMessage = Messages.FORM_ERROR_GEOMETRY;
					tipologiaDannoFormMessageRendered = true;
					return "";
				}
			}
		
			tipologiaDannoFormMessageRendered = false;
			for (FileAllegatoBean fab : currentTipologiaDanno.getFileAllegati()) {
				fab.setTipoAllegato(tipiAllegatiUploadedFiles.get(fab.getFileName()));
			}
			tipiDannoTemp.add(currentTipologiaDanno);
			selectedTab = IMPATTILOCALI_TAB;
			return "impattoLocaleSalvato";
		}
	}
	
	
	public String salvaRelazioneTecnica() {
		if (checkImpattiGeneraliFormField()) {

			formMessageRendered = false;

			try {

				
				RelazioniTecnicheSTBService relazioniTecnicheSTBService =  JNDIUtils.retrieveEJB(JNDIUtils.RelazioniTecnicheSTBServiceName, JNDIUtils.RelazioniTecnicheSTBServiceName);

				Long idRelazioneTecnica = null;

				// 1. insert RelazioneSTB
				if(!editMode){
					RelazioneSTB relazioneSTB = new RelazioneSTB();
					relazioneSTB.setCodice(impattiGeneraliCodiceValutazione);
					relazioneSTB.setInizioValidita(impattiGeneraliInizioValidita);
					relazioneSTB.setFineValidita(getImpattiGeneraliFineValidita());
					
					relazioneSTB.setDescrizione(impattiGeneraliDescrizione);

					relazioniTecnicheSTBService.salvaRelazioneTecnica(editMode, relazioneSTB, this.stb, this.fonte, tipologieDannoToRemoveList, tipiDannoTemp,tipiAllegato, allegati, attachmentsToRemoveList);
				}else
				{
					this.selectedItem.setCodice(impattiGeneraliCodiceValutazione);
					this.selectedItem.setInizioValidita(impattiGeneraliInizioValidita);
					this.selectedItem.setFineValidita(getImpattiGeneraliFineValidita());
					
					this.selectedItem.setDescrizione(impattiGeneraliDescrizione);
			
					relazioniTecnicheSTBService.salvaRelazioneTecnica(editMode, this.selectedItem, this.stb, this.fonte, tipologieDannoToRemoveList, tipiDannoTemp,tipiAllegato, allegati, attachmentsToRemoveList);
				}


			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				formMessage = Messages.FORM_ERROR_PERSIST;
				formMessageRendered = true;
				return "";
			} catch (ParseException e) {
				e.printStackTrace();
				formMessage = Messages.FORM_ERROR_PERSIST;
				formMessageRendered = true;
				return "";
			} catch (Exception e) {
				e.printStackTrace();
				formMessage = Messages.FORM_ERROR_PERSIST;
				formMessageRendered = true;
				return "";
			}

		}
		// la form non � completa
		else {
		
			/* Modifico messaggio di file non valido perch� il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (formMessageRendered == true) {
				if (formMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					formMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
			
			return "";
		}

		pulisciFiltri();
		return "relazioneTecnicaSalvata";

	}

	public void openItemMap() {
		DannoSTB danno = selectedItem.getDanni().get(tipologieDanniRow);
		DannoSTBManager dannoManager = JNDIUtils.getDannoSTBManager();
		try {
			List<Geometry> geometries = dannoManager.getGeometry(danno.getId());
			selectedDannoGeometries = BeanUtils.getGeometryString(geometries);

			logger.info("selectedDannoGeometries "+selectedDannoGeometries);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private boolean checkImpattoLocaleFormFields() {
		// prende la localit� di default e i float vengono lasciati tutti a 0.0.
		// nessun controllo su descrizione vuota
		return true;
	}

	private boolean checkTipiAllegatiPresenti() {
		for (Map.Entry<String, String> entry : getTipiAllegatiUploadedFiles().entrySet()) {
			if (null==entry.getValue()) {
				return false;
			}	
		}
		return true;

	}

	

	public String deleteItem() {
		logger.info("delete item "+this.itemToAct);
		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		try {
			Long result = relazioneSTBManager.remove(this.itemToAct);
			if(result ==  -1L)
				throw new Exception();

		} catch (Exception e) {
			e.printStackTrace();
			newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			newItemFormMessageRendered = true;

			//return "";

		}
		pulisciFiltri();
		return "delete";
	}

	public String approvaItem() {
		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		try {
			relazioneSTBManager.updateStato(itemToAct, "Definitivo");
		} catch (Exception e) {
			newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			newItemFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "";
	}

	public String protocollaItem() {
		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		try {
			relazioneSTBManager.updateStato(itemToAct, "Protocollato");
		} catch (Exception e) {
			newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			newItemFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "";
	}
	
	

	public void downloadAttachment() {
		AttachmentUtils.downloadAttachment(this.getAttachmentToDownload());
	}

	public String viewItemDetails() {
		this.selectedItem = (RelazioneSTB) dataModel.getRowData();
		RelazioneSTBManager relazioneSTBManager = (RelazioneSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.RelazioneSTBManagerName, JNDIUtils.RelazioneSTBManagerName);
		DannoSTBManager dannoSTBManager = (DannoSTBManager) JNDIUtils.retrieveEJB(JNDIUtils.DannoSTBManagerName, JNDIUtils.DannoSTBManagerName);
		try {
			this.selectedItem.setDanni(relazioneSTBManager.getDanniSTB(this.selectedItem.getId()));
			for (DannoSTB d : selectedItem.getDanni() ) {
				d.setAllegati(dannoSTBManager.getAllegati(d.getId()));
			}
		} catch (EntityNotFoundException e) {
			//TODO visualizzare errore
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}
		return "toItemDetails";
	}

	public String toRelazioniTecnicheHome() {
		return "toRelazioniTecnicheHome";
	}

	private boolean checkImpattiGeneraliFormField() {

		if(this.impattiGeneraliInizioValidita == null || this.impattiGeneraliFineValidita==null || !StringUtils.notEmpty(impattiGeneraliCodiceValutazione) || this.stb == null){

			formMessage = Messages.FORM_NOT_VALID;
			formMessageRendered = true;

			return false;
		}

		if((this.impattiGeneraliInizioValidita.after(this.impattiGeneraliFineValidita)))
		{
			formMessage = Messages.FORM_ERROR_DATE;
			formMessageRendered = true;
			return false;
		}

		if ( !allegati.checkTipiAllegatiPresenti() ) {
			formMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			formMessageRendered = true;
			return false;
		}

		/* File caricato non valido */
		if (formMessageRendered == true) {
			if (formMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID))
				return false;
		}
		
		return true;

	}

	/** Private Method **/

	private void updateEstTerritoriali()
	{
		stbSelect = new ArrayList<SelectItem>();
		stbs = new LinkedHashMap<String, STB>();
		STBManager stbm = (STBManager) JNDIUtils.retrieveEJB(JNDIUtils.STBManagerName, JNDIUtils.STBManagerName);
		List<STB> stbFromDB = stbm.selectAll();
		stbSelect.add(new SelectItem(null,"-"));
		for (STB t : stbFromDB) {
			stbs.put(t.getNome(), t);
			stbSelect.add(new SelectItem(t.getNome()));
		}
	}
	
	private void updateFonti()
	{
		fontiSelect = new ArrayList<SelectItem>();
		fonti = new LinkedHashMap<String, Fonte>();
		FonteManager fontem = (FonteManager) JNDIUtils.retrieveEJB(JNDIUtils.FonteManagerName, JNDIUtils.FonteManagerName);
		List<Fonte> fonteFromDB = fontem.selectAll();
		fontiSelect.add(new SelectItem(null,"-"));
		for (Fonte t : fonteFromDB) {
			fonti.put(t.getNome(), t);
			fontiSelect.add(new SelectItem(t.getNome()));
		}
	}

	private void updateLocalita()
	{
		localitaSelect = new ArrayList<SelectItem>();
		List<Localita> localitaFromDB = JNDIUtils.getLocalitaManager().selectAll();
		localitaSelect.add(new SelectItem(null,"-"));
		for (Localita t : localitaFromDB) {
			localitaSelect.add(new SelectItem(t,t.getNome()));
		}
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
					new NestedCriterion("localita",Restrictions.eq("nome", filtro_localita.getNome() )));
			criterions.add(celle);
		}

		if(filtro_stb != null){
			NestedCriterion stb = new NestedCriterion("stb", Restrictions.eq("nome", filtro_stb));
			criterions.add(stb);
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
		orders.add( Order.desc("inizioValidita") );
		orders.add( Order.desc("fineValidita") );
		orders.add( Order.desc("id") );
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

	public List<SelectItem> getStbSelect() {
		if (null == stbSelect || stbSelect.isEmpty())
			updateEstTerritoriali();
		return stbSelect;
	}

	public void setStbSelect(List<SelectItem> stbSelect) {
		this.stbSelect = stbSelect;
	}

	public List<SelectItem> getFontiSelect() {
		if (null == fontiSelect || fontiSelect.isEmpty())
			updateFonti();
		return fontiSelect;
	}

	public void setFontiSelect(List<SelectItem> fontiSelect) {
		this.fontiSelect = fontiSelect;
	}
	
	public String getFiltro_stb() {
		return filtro_stb;
	}

	public void setFiltro_stb(String filtro_stb) {
		this.filtro_stb = filtro_stb;
	}

	public List<SelectItem> getLocalitaSelect() {
		if (null == localitaSelect || localitaSelect.isEmpty())
			updateLocalita();
		return localitaSelect;
	}

	public void setLocalitaSelect(List<SelectItem> localitaSelect) {
		this.localitaSelect = localitaSelect;
	}

	public Localita getFiltro_localita() {
		return filtro_localita;
	}

	public void setFiltro_localita(Localita filtro_localita) {
		this.filtro_localita = filtro_localita;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public String getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public String getFormMessage() {
		return formMessage;
	}

	public void setFormMessage(String formMessage) {
		this.formMessage = formMessage;
	}

	public boolean isFormMessageRendered() {
		return formMessageRendered;
	}

	public void setFormMessageRendered(boolean formMessageRendered) {
		this.formMessageRendered = formMessageRendered;
	}

	public String getImpattiGeneraliCodiceValutazione() {
		return impattiGeneraliCodiceValutazione;
	}

	public void setImpattiGeneraliCodiceValutazione(String impattiGeneraliCodiceValutazione) {
		this.impattiGeneraliCodiceValutazione = impattiGeneraliCodiceValutazione;
	}

	public Date getImpattiGeneraliInizioValidita() {
		return impattiGeneraliInizioValidita;
	}

	public void setImpattiGeneraliInizioValidita(Date impattiGeneraliInizioValidita) {
		this.impattiGeneraliInizioValidita = impattiGeneraliInizioValidita;
	}

	public Date getImpattiGeneraliFineValidita() {
		return impattiGeneraliFineValidita;
	}

	public void setImpattiGeneraliFineValidita(Date impattiGeneraliFineValidita) {
		this.impattiGeneraliFineValidita = impattiGeneraliFineValidita;
	}

	public String getImpattiGeneraliDescrizione() {
		return impattiGeneraliDescrizione;
	}

	public void setImpattiGeneraliDescrizione(String impattiGeneraliDescrizione) {
		this.impattiGeneraliDescrizione = impattiGeneraliDescrizione;
	}

	public List<SelectItem> getStatiRelazioneSelect() {
		if (null == statiRelazioneSelect || statiRelazioneSelect.isEmpty() )
			updateStatiRelazione();
		return statiRelazioneSelect;
	}

	private void updateStatiRelazione() {
		statiRelazione = new LinkedHashMap<String, StatoRelazione>();
		statiRelazioneSelect = new ArrayList<SelectItem>();
		StatoRelazioneManager srm = (StatoRelazioneManager) JNDIUtils.retrieveEJB(JNDIUtils.StatoRelazioneManagerName, JNDIUtils.StatoRelazioneManagerName);
		List<StatoRelazione> statiRelazioneFromDB = srm.selectAll();
		for (StatoRelazione sr : statiRelazioneFromDB) {
			if (sr.getNome().equals("Protocollato"))
				continue;
			statiRelazione.put(sr.getNome(), sr);
			statiRelazioneSelect.add(new SelectItem(sr.getNome()));
		}
	}

	public void setStatiRelazioneSelect(List<SelectItem> statiRelazioneSelect) {
		this.statiRelazioneSelect = statiRelazioneSelect;
	}

	public Map<String, StatoRelazione> getStatiRelazione() {
		return statiRelazione;
	}

	public void setStatiRelazione(Map<String, StatoRelazione> statiRelazione) {
		this.statiRelazione = statiRelazione;
	}

	public String getStatoRelazione() {
		return statoRelazione;
	}

	public void setStatoRelazione(String statoRelazione) {
		this.statoRelazione = statoRelazione;
	}

	public List<DannoSTBBean> getTipiDannoTemp() {
		if (null == tipiDannoTemp) {
			tipiDannoTemp = new ArrayList<DannoSTBBean>();
		}
		return tipiDannoTemp;
	}

	public void setTipiDannoTemp(List<DannoSTBBean> tipiDannoTemp) {
		this.tipiDannoTemp = tipiDannoTemp;
	}

	public int getTipoDannoTempRow() {
		return tipoDannoTempRow;
	}

	public void setTipoDannoTempRow(int tipoDannoTempRow) {
		this.tipoDannoTempRow = tipoDannoTempRow;
	}

	public DannoSTBBean getCurrentTipologiaDanno() {
		return currentTipologiaDanno;
	}

	public void setCurrentTipologiaDanno(DannoSTBBean currentTipologiaDanno) {
		this.currentTipologiaDanno = currentTipologiaDanno;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public boolean isTipoAllegatiBoxRendered() {
		if (uploadsAvailable < MAX_UPLOADS)
			return true;
		else
			return false;
	}

	public void setTipoAllegatiBoxRendered(boolean tipoAllegatiBoxRendered) {
		this.tipoAllegatiBoxRendered = tipoAllegatiBoxRendered;
	}

	public Map<String, File> getUploadedFiles() {
		if (null == uploadedFiles)
			uploadedFiles = new LinkedHashMap<String, File>();
		return uploadedFiles;
	}

	public void setUploadedFiles(Map<String, File> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	public Map<String, String> getTipiAllegatiUploadedFiles() {
		if (null == tipiAllegatiUploadedFiles )
			tipiAllegatiUploadedFiles = new LinkedHashMap<String, String>();
		return tipiAllegatiUploadedFiles;
	}

	public void setTipiAllegatiUploadedFiles(
			Map<String, String> tipiAllegatiUploadedFiles) {
		this.tipiAllegatiUploadedFiles = tipiAllegatiUploadedFiles;
	}

	public List<String> getUploadedFilesKeys() {
		List<String> uploadedFilesKeys = new ArrayList<String>();
		uploadedFilesKeys.addAll(getTipiAllegatiUploadedFiles().keySet());
		return uploadedFilesKeys;
	}

	public void setUploadedFilesKeys(List<String> uploadedFilesKeys) {
		this.uploadedFilesKeys = uploadedFilesKeys;
	}

	public List<SelectItem> getTipiAllegatoSelect() {
		if (null == tipiAllegatoSelect || tipiAllegatoSelect.isEmpty())
			updateTipiAllegato();
		return tipiAllegatoSelect;
	}

	private void updateTipiAllegato() {
		tipiAllegato = new LinkedHashMap<String, TipoAllegato>();
		tipiAllegatoSelect = new ArrayList<SelectItem>();
		TipoAllegatoManager tipoAllegatoManager = (TipoAllegatoManager) JNDIUtils.retrieveEJB(JNDIUtils.TipoAllegatoManagerName, JNDIUtils.TipoAllegatoManagerName);
		List<TipoAllegato> tipiAllegatoFromDB = tipoAllegatoManager.selectAll();
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

	public Map<String, STB> getStbs() {
		return stbs;
	}

	public void setStbs(Map<String, STB> stbs) {
		this.stbs = stbs;
	}

	public String getStb() {
		return stb;
	}

	public void setStb(String stb) {
		this.stb = stb;
	}

	public Map<String, Fonte> getFonti() {
		return fonti;
	}

	public void setFonti(Map<String, Fonte> fonti) {
		this.fonti = fonti;
	}

	public String getFonte() {
		return fonte;
	}

	public void setFonte(String fonte) {
		this.fonte = fonte;
	}

	public RelazioneSTB getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(RelazioneSTB selectedItem) {
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

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}


	public String getSelectedDannoGeometries() {
		return selectedDannoGeometries;
	}


	public int getTipologieDanniRow() {
		return tipologieDanniRow;
	}


	public void setSelectedDannoGeometries(String selectedDannoGeometries) {
		this.selectedDannoGeometries = selectedDannoGeometries;
	}


	public void setTipologieDanniRow(int tipologieDanniRow) {
		this.tipologieDanniRow = tipologieDanniRow;
	}


	public Long getItemToAct() {
		return itemToAct;
	}


	public void setItemToAct(Long itemToAct) {
		this.itemToAct = itemToAct;
	}


	public List<DannoSTBBean> getTipologieDannoToRemoveList() {
		return tipologieDannoToRemoveList;
	}

	public void setTipologieDannoToRemoveList(
			List<DannoSTBBean> tipologieDannoToRemoveList) {
		this.tipologieDannoToRemoveList = tipologieDannoToRemoveList;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
	}

	public String getAllegato_type() {
		return allegato_type;
	}

	public void setAllegato_type(String allegato_type) {
		this.allegato_type = allegato_type;
	}

	public List<Allegato> getAllegatiTemp() {
		return allegatiTemp;
	}

	public void setAllegatiTemp(List<Allegato> allegatiTemp) {
		this.allegatiTemp = allegatiTemp;
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



}
