package it.epocaricerca.geologia.web.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModelEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.ImpattoRealeService;
import it.epocaricerca.geologia.ejb.tdo.FileAllegatoBean;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.TipologiaDannoBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.model.TipoDanno;
import it.epocaricerca.geologia.model.Fonte;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.converter.LocalitaConverter;
import it.epocaricerca.geologia.web.util.AttachmentUtils;
import it.epocaricerca.geologia.web.util.BeanUtils;
import it.epocaricerca.geologia.web.util.GenericUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;
import it.epocaricerca.geologia.web.validator.FileValidator;

public class ImpattiRealiController extends FilterableHandler<ImpattoReale> {

	private static final long serialVersionUID = 1L;

	private static final String IMPATTIGENERALI_TAB = "impattigenerali_tab";
	private static final String IMPATTILOCALI_TAB = "impattilocali_tab";

	private static final int MAX_UPLOADS = 10;
	private static final String FAKE_PATH = "C:\\fakepath\\";

	private static Logger logger = Logger.getLogger(ImpattiRealiController.class);

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;
	private Localita filtro_localita;

	private boolean newItemFormMessageRendered = false;
	private String newItemFormMessage;


	private Long itemToDelete;
	private String selectedTab = IMPATTIGENERALI_TAB;

	private Map<Long, String> valutazioneDannoPrevalente = new HashMap<Long, String>();

	// TAB IMPATTI GENERALI
	private String impattiGeneraliCodiceValutazione;
	private Date impattiGeneraliInizioValidita;
	private Date impattiGeneraliFineValidita;
	private String impattiGeneraliDescrizioneValutazione;
	private String valutazioneImpattiFormMessage;
	private boolean valutazioneImpattiFormMessageRendered = false;
	private Map<String, Fonte> fonti;
	private String fonte;
	private List<SelectItem> fontiSelect;
	
	// TAB IMPATTI LOCALI
	private int impattoLocaleTempRow;
	private List<TipologiaDannoBean> impattiLocaliTemp;


	// FORM NUOVO IMPATTO LOCALE
	TipologiaDannoBean currentTipologiaDanno;
	private List<SelectItem> tipiDannoSelect;
	private Map<String, TipoDanno> tipiDanno;
	private String nuovoDannoTipoDanno;
	private List<SelectItem> localitaSelect;
	private Map<String, Localita> localita;
	private String nuovoDannoLocalitaDanno;
	private int uploadsAvailable = MAX_UPLOADS;
	private boolean tipoAllegatiBoxRendered;
	private Map<String, File> uploadedFiles = new LinkedHashMap<String, File>();
	private Map<String, String> tipiAllegatiUploadedFiles;
	private List<String> uploadedFilesKeys;
	private String tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
	private Map<String, TipoAllegato> tipiAllegato;
	private List<SelectItem> tipiAllegatoSelect;
	private boolean tipologiaDannoFormMessageRendered = false;
	private String fileToClear;
	private String geometryText;

	private FileUploadBean allegati;
	private String allegato_type;
	
	private List<Allegato> allegatiTemp;

	private Allegato attachmentToRemove;
	private List<Allegato> attachmentsToRemoveList;

	//VIEW DATAILS
	private String selectedDannoGeometries;
	public String getAllegato_type() {
		return allegato_type;
	}



	public void setAllegato_type(String allegato_type) {
		this.allegato_type = allegato_type;
	}



	private int tipologieDanniRow;

	private boolean editMode = false;

	private Allegato attachmentToDownload;

	//EDIT MODE

	private ImpattoReale selectedItem;
	private List<TipologiaDannoBean> tipologieDannoToRemoveList;



	public String editItemDetails() {

		this.selectedItem = (ImpattoReale) dataModel.getRowData();
		this.editMode = true;
		
		if (null != attachmentsToRemoveList) {
			attachmentsToRemoveList.clear();
		}
		else {
			attachmentsToRemoveList = new ArrayList<Allegato>();
		}
		
		cleanForm();
		try {
			fillForm();
		} catch (EntityNotFoundException e) {
			e.printStackTrace();
			valutazioneImpattiFormMessage = Messages.FORM_ERROR_PERSIST;
			valutazioneImpattiFormMessageRendered = true;
			return "";
		}
		return "editItemDetails";
	}



	public void fillForm() throws EntityNotFoundException {
		// tab generale
		impattiGeneraliCodiceValutazione = this.selectedItem.getCodice();
		impattiGeneraliInizioValidita = this.selectedItem.getInizioValidita();
		impattiGeneraliFineValidita = this.selectedItem.getFineValidita();
		impattiGeneraliDescrizioneValutazione = this.selectedItem.getDescrizione();
		
		if (null != this.selectedItem.getFonte()) {
			fonte = this.selectedItem.getFonte().getNome();
		} else {
			fonte = "-";
		}

		allegatiTemp = new ArrayList<Allegato>(this.selectedItem.getAllegati());
		
		ImpattoRealeManager impattoRealeManager = JNDIUtils.getImpattoRealeManager();

		DannoManager dannoManager = JNDIUtils.getDannoManager();

		// tab danni
		// table di danni inseriti: conversione Danno -> TipologiaDannoBean
		TipologiaDannoBean tdb;
		for (Danno d : impattoRealeManager.getDanni( this.selectedItem.getId())) {
			tdb = new TipologiaDannoBean();
			tdb.setId(d.getId());
			tdb.setLocalita(d.getLocalita());
			tdb.setTipoDanno(d.getTipoDanno().getNome());

			try {
				List<Geometry> geometries = dannoManager.getGeometry(d.getId());
				selectedDannoGeometries = BeanUtils.getGeometryString(geometries);

				tdb.setGeometryText(selectedDannoGeometries);

			} catch (Exception e) {
				e.printStackTrace();
				logger.error(e.getMessage());
			}

			FileAllegatoBean fab;
			for (Allegato a : dannoManager.getAllegati(d.getId())) {
				fab = new FileAllegatoBean(a.getFile(), a.getTipo().getNome(), a.getNome());
				tdb.getFileAllegati().add(fab);
			}
			impattiLocaliTemp.add(tdb);
		}
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
		ImpattoReale impattoReale = (ImpattoReale) event.getRowData();
		ImpattoRealeManager manager = JNDIUtils.getImpattoRealeManager();
		try {
			List<Danno> danni = manager.getDanni(impattoReale.getId());

			Map<String, Integer > counter = new HashMap<String, Integer>();


			if(!danni.isEmpty()){

				for (Danno danno : danni) {
					String nomeDanno = danno.getTipoDanno().getNome();
					if(counter.containsKey(nomeDanno))
					{
						counter.put(nomeDanno, counter.get(nomeDanno) +1 );
					}
					else
						counter.put(nomeDanno, 1);
				}

				Map<String, Integer>  sorted = GenericUtils.sortByValues(counter);


				valutazioneDannoPrevalente.put(impattoReale.getId(), (String) sorted.keySet().toArray()[sorted.keySet().size() -1]);
			}
			else
				valutazioneDannoPrevalente.put(impattoReale.getId(), "");


		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			valutazioneImpattiFormMessage = Messages.FORM_ERROR_PERSIST;
			valutazioneImpattiFormMessageRendered = true;
		}
	}

	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		filtro_localita = null;
		updateDataModel();
	}

	public void filtraImpattiReali() {
		logger.info("filtraImpattiReali "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		updateDataModel();
	}

	private void cleanForm() {
		this.valutazioneImpattiFormMessageRendered = false;
		this.impattiGeneraliInizioValidita = null;
		this.impattiGeneraliFineValidita = null;
		this.impattiGeneraliCodiceValutazione = null;
		this.fonte = null;
		this.impattiGeneraliDescrizioneValutazione = null;
		this.tipologieDannoToRemoveList = new ArrayList<TipologiaDannoBean>();
		if (null != this.getImpattiLocaliTemp() )
			this.getImpattiLocaliTemp().clear();
		this.allegati = new FileUploadBean(MAX_UPLOADS);
	}

	public String nuovoImpattoReale() {
		cleanForm();
		return "nuovoImpattoReale";
	}

	public String annullaCreazioneImpattoReale() {
		cleanForm();
		this.editMode = false;
		return "annullaCreazioneImpatto";
	}

	public String viewItemDetails() {
		this.selectedItem = (ImpattoReale) dataModel.getRowData();
		ImpattoRealeManager impattoRealeManager = JNDIUtils.getImpattoRealeManager();
		DannoManager dannoManager = JNDIUtils.getDannoManager();
		try {
			this.selectedItem.setDanni(impattoRealeManager.getDanni(this.selectedItem.getId()));
			for (Danno d : selectedItem.getDanni() ) {
				d.setAllegati(dannoManager.getAllegati(d.getId()));
			}
		} catch (EntityNotFoundException e) {
			//TODO gestione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}
		return "toItemDetails";
	}

	public String toImpattiRealiHome() {
		return "toImpattiRealiHome";
	}

	public String salvaImpattoReale() {
		if (checkValutazioneImpattiFormField()) {

			valutazioneImpattiFormMessageRendered = false;

			try {

				ImpattoRealeService impattoRealeService = JNDIUtils.retrieveEJB(JNDIUtils.ImpattoRealeServiceName, JNDIUtils.ImpattoRealeServiceName);

				if (editMode) {

					selectedItem.setCodice(impattiGeneraliCodiceValutazione);
					selectedItem.setInizioValidita(impattiGeneraliInizioValidita);
					selectedItem.setFineValidita(impattiGeneraliFineValidita);
					selectedItem.setDescrizione(impattiGeneraliDescrizioneValutazione);
					impattoRealeService.salvaImpattoReale(editMode, selectedItem, fonte, impattiLocaliTemp, tipologieDannoToRemoveList,tipiAllegato, allegati, attachmentsToRemoveList);
				} 
				else {

					ImpattoReale impattoReale = new ImpattoReale();
					impattoReale.setCodice(impattiGeneraliCodiceValutazione);
					impattoReale.setInizioValidita(impattiGeneraliInizioValidita);
					impattoReale.setFineValidita(impattiGeneraliFineValidita);
					impattoReale.setDescrizione(impattiGeneraliDescrizioneValutazione);

					impattoRealeService.salvaImpattoReale(editMode, impattoReale, fonte, impattiLocaliTemp, tipologieDannoToRemoveList,tipiAllegato, allegati, attachmentsToRemoveList);
				}


			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				valutazioneImpattiFormMessage = Messages.FORM_ERROR_PERSIST;
				valutazioneImpattiFormMessageRendered = true;
				return "";
			} catch (ParseException e) {
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
			return "impattoRealeSalvato";
		}
		// la form non � completa
		else {
			/* Modifico messaggio di file non valido perch� il click del tasto Salva ricarica e pulisce in automatico la sezione inserisci allegato */
			if (valutazioneImpattiFormMessageRendered == true) {
				if (valutazioneImpattiFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
					valutazioneImpattiFormMessage = "File non valido rimosso. Cliccare su Salva o caricare un nuovo file per proseguire.";
				}
			}
		
			return "";
		}

	}


	public void test(){
		logger.info("test");
	}

	public void openItemMap() {
		Danno danno = selectedItem.getDanni().get(tipologieDanniRow);
		DannoManager dannoManager = JNDIUtils.getDannoManager();
		try {
			List<Geometry> geometries = dannoManager.getGeometry(danno.getId());
			selectedDannoGeometries = BeanUtils.getGeometryString(geometries);

			logger.info("selectedDannoGeometries "+selectedDannoGeometries);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}

	}

	private boolean checkValutazioneImpattiFormField() {

		if(this.impattiGeneraliInizioValidita == null || this.impattiGeneraliFineValidita==null || !StringUtils.notEmpty(impattiGeneraliCodiceValutazione)){

			valutazioneImpattiFormMessage = Messages.FORM_NOT_VALID;
			valutazioneImpattiFormMessageRendered = true;

			return false;
		}

		if((this.impattiGeneraliInizioValidita.after(this.impattiGeneraliFineValidita)))
		{
			valutazioneImpattiFormMessage = Messages.FORM_ERROR_DATE;
			valutazioneImpattiFormMessageRendered = true;
			return false;
		}

		if ( allegati != null && !allegati.checkTipiAllegatiPresenti() ) {
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

	public void downloadAttachment() {
		AttachmentUtils.downloadAttachment(this.getAttachmentToDownload());
	}

	public String deleteItem() {
		ImpattoRealeManager impattoRealeManager = JNDIUtils.getImpattoRealeManager();
		try {
			impattoRealeManager.remove(this.getItemToDelete());
		} catch (Exception e) {
			e.printStackTrace();

			//TODO mostrare messaggio
			newItemFormMessage = Messages.FORM_ERROR_PERSIST;
			newItemFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "delete";
	}

	public String nuovoImpattoLocale() {
		selectedTab = IMPATTIGENERALI_TAB;
		currentTipologiaDanno = new TipologiaDannoBean();
		getTipiAllegatiUploadedFiles().clear();
		tipologiaDannoFormMessageRendered = false;
		return "nuovoImpattoLocale";
	}

	public void deleteImpattoRealeTemp() {
		TipologiaDannoBean tdbToRemove = impattiLocaliTemp.get(impattoLocaleTempRow);
		if ( editMode && null!=tdbToRemove.getId() ) {
			// � un danno da DB, lo aggiungo alla lista di danni da rimuovere mediante API
			getTipologieDannoToRemoveList().add(tdbToRemove);
		}
		// in ogni caso rimuovo dalla lista utilizzata per il rendering della tabella
		impattiLocaliTemp.remove(impattoLocaleTempRow);
	}

	public String salvaImpattoLocale() {
		if ( !checkTipiAllegatiPresenti() ) {
			tipologiaDannoFormMessage = Messages.FORM_ATTACHMENTTYPE_MISSING;
			tipologiaDannoFormMessageRendered = true;
			return "";
		} 
		else if(tipologiaDannoFormMessageRendered == true)
		{
			if (tipologiaDannoFormMessage.contains(Messages.FORM_ERROR_IOFILE_NOTVALID)) {
				return "";
			} else {
				return "?";
			}
		}
		
		else if(currentTipologiaDanno.getGeometryText() == null || !StringUtils.notEmpty(currentTipologiaDanno.getGeometryText()))
		{
			tipologiaDannoFormMessage = Messages.FORM_ERROR_GEOMETRY;
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
			tipologiaDannoFormMessageRendered = false;
			for (FileAllegatoBean fab : currentTipologiaDanno.getFileAllegati()) {
				fab.setTipoAllegato(tipiAllegatiUploadedFiles.get(fab.getFileName()));
			}
			impattiLocaliTemp.add(currentTipologiaDanno);
			logger.info("Salvo Temporaneamente Danno "+currentTipologiaDanno.getGeometryText()+" "+currentTipologiaDanno.getLocalita());


			selectedTab = IMPATTILOCALI_TAB;
			return "impattoLocaleSalvato";
		}

	}

	private boolean checkTipiAllegatiPresenti() {
		for (Map.Entry<String, String> entry : getTipiAllegatiUploadedFiles().entrySet()) {
			if (null==entry.getValue()) {
				return false;
			}	
		}
		return true;

	}

	public String annullaCreazioneNuovoImpattoLocale() {
		return "annullaCreazioneNuovoImpattoLocale";
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
				valutazioneImpattiFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; 
				valutazioneImpattiFormMessageRendered = true;
			}
			return;					
		}  else {
			logger.info("QUUUUOOOOOOOO");
			
			
			if(this.allegato_type.equals("dannoAllegati")){
			
				//uploadedFiles.put(item.getFileName(), item.getFile()); ??
			
				currentTipologiaDanno.getFileAllegati().add(new FileAllegatoBean(item.getFile(), null, item.getFileName()));
				getTipiAllegatiUploadedFiles().put(item.getFileName(), null);
				uploadsAvailable--;
			}
			else
			{
				allegati.addAllegato(item);
			}
		}
		

		

	}

	public void clearFileUpload(ActionEvent event) {
		tipologiaDannoFormMessageRendered = false;
				
		/*if(uploadedFiles == null || uploadedFiles.isEmpty()) {
			return;
		}*/

		if (getFileToClear() != null && !"".equals(getFileToClear())) {
		
			logger.info("clearFileUpload filetoclear "+ this.getFileToClear().replace(FAKE_PATH, ""));
			if(this.allegato_type.equals("dannoAllegati")){
			
				if (tipologiaDannoFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
					logger.info("posso cancellare messagio - si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					tipologiaDannoFormMessageRendered = false; 
				} else {
					logger.info("non posso cancellare messagio - non si riferisce a questo file" + tipologiaDannoFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					tipologiaDannoFormMessageRendered = true; 
				}
			
				uploadedFiles.remove(this.getFileToClear().replace(FAKE_PATH, ""));
				tipiAllegatiUploadedFiles.remove(this.getFileToClear().replace(
						FAKE_PATH, ""));
				uploadsAvailable++;
			} else {
			
				if (valutazioneImpattiFormMessage.contains(this.getFileToClear().replace(FAKE_PATH, ""))) {
					logger.info("posso cancellare messagio - si riferisce a questo file" + valutazioneImpattiFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					valutazioneImpattiFormMessageRendered = false; 
				} else {
					logger.info("non posso cancellare messagio - non si riferisce a questo file" + valutazioneImpattiFormMessage + " " + this.getFileToClear().replace(FAKE_PATH, ""));
					valutazioneImpattiFormMessageRendered = true; 
				}				
			
				allegati.removeAllegato(this.getFileToClear().replace(FAKE_PATH, ""));
			}
		} else {
			if(this.allegato_type.equals("dannoAllegati")){
				logger.info("clearing all files");
				uploadedFiles.clear();
				tipiAllegatiUploadedFiles.clear();
				uploadsAvailable = MAX_UPLOADS;
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

	private void updateFonti()	{
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
	/** FilterableHandler methods**/

	@Override
	public void resetSearchParameters() {
		// TODO Auto-generated method stub

	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();

		if(filtro_localita != null){
			NestedCriterion celle = new NestedCriterion("danni", 
					new NestedCriterion("localita",Restrictions.eq("nome", filtro_localita.getNome() )));
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

	public ImpattoReale getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(ImpattoReale selectedItem) {
		this.selectedItem = selectedItem;
	}

	public int getImpattoLocaleTempRow() {
		return impattoLocaleTempRow;
	}

	public void setImpattoLocaleTempRow(int impattoLocaleTempRow) {
		this.impattoLocaleTempRow = impattoLocaleTempRow;
	}

	public List<TipologiaDannoBean> getImpattiLocaliTemp() {
		if (null == impattiLocaliTemp) {
			impattiLocaliTemp = new ArrayList<TipologiaDannoBean>();
		}
		return impattiLocaliTemp;
	}

	public void setImpattiLocaliTemp(List<TipologiaDannoBean> impattiLocaliTemp) {
		this.impattiLocaliTemp = impattiLocaliTemp;
	}

	public String getImpattiGeneraliCodiceValutazione() {
		return impattiGeneraliCodiceValutazione;
	}

	public void setImpattiGeneraliCodiceValutazione(
			String impattiGeneraliCodiceValutazione) {
		this.impattiGeneraliCodiceValutazione = impattiGeneraliCodiceValutazione;
	}

	public String getImpattiGeneraliDescrizioneValutazione() {
		return impattiGeneraliDescrizioneValutazione;
	}

	public void setImpattiGeneraliDescrizioneValutazione(
			String impattiGeneraliDescrizioneValutazione) {
		this.impattiGeneraliDescrizioneValutazione = impattiGeneraliDescrizioneValutazione;
	}

	public TipologiaDannoBean getCurrentTipologiaDanno() {
		return currentTipologiaDanno;
	}

	public void setCurrentTipologiaDanno(TipologiaDannoBean currentTipologiaDanno) {
		this.currentTipologiaDanno = currentTipologiaDanno;
	}

	public FileUploadBean getAllegati() {
		return allegati;
	}

	public void setAllegati(FileUploadBean allegati) {
		this.allegati = allegati;
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

	public List<SelectItem> getTipiDannoSelect() {
		if (null == tipiDannoSelect || tipiDannoSelect.isEmpty() )
			updateTipiDanno();
		return tipiDannoSelect;
	}

	private void updateTipiDanno() {
		tipiDanno = new LinkedHashMap<String, TipoDanno>();
		tipiDannoSelect = new ArrayList<SelectItem>();
		tipiDannoSelect.add(new SelectItem(null,"-"));
		List<TipoDanno> tipiDannoFromDB = JNDIUtils.getTipoDannoManager().selectAll();
		for (TipoDanno td : tipiDannoFromDB) {
			tipiDanno.put(td.getNome(), td);
			tipiDannoSelect.add(new SelectItem(td.getNome()));
		}		
	}

	public void setTipiDannoSelect(List<SelectItem> tipiDannoSelect) {
		this.tipiDannoSelect = tipiDannoSelect;
	}

	public String getNuovoDannoTipoDanno() {
		return nuovoDannoTipoDanno;
	}

	public void setNuovoDannoTipoDanno(String nuovoDannoTipoDanno) {
		this.nuovoDannoTipoDanno = nuovoDannoTipoDanno;
	}

	public List<SelectItem> getLocalitaSelect() {
		if ( null == localitaSelect || localitaSelect.isEmpty() )
			updateLocalita();
		return localitaSelect;
	}

	private void updateLocalita() {
		localita = new LinkedHashMap<String, Localita>();
		localitaSelect = new ArrayList<SelectItem>();
		localitaSelect.add(new SelectItem(null,"-"));
		List<Localita> localitaFromDB = JNDIUtils.getLocalitaManager().selectAll();
		for (Localita l : localitaFromDB) {
			localita.put(l.getNome(), l);
			localitaSelect.add(new SelectItem(l,l.getNome()));
		}
	}

	public void setLocalitaSelect(List<SelectItem> localitaSelect) {
		this.localitaSelect = localitaSelect;
	}

	public String getNuovoDannoLocalitaDanno() {
		return nuovoDannoLocalitaDanno;
	}

	public void setNuovoDannoLocalitaDanno(String nuovoDannoLocalitaDanno) {
		this.nuovoDannoLocalitaDanno = nuovoDannoLocalitaDanno;
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

	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public Map<String, File> getUploadedFiles() {
		if (null == uploadedFiles)
			uploadedFiles = new LinkedHashMap<String, File>();
		return uploadedFiles;
	}

	public void setUploadedFiles(Map<String, File> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
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

	public Map<Long, String> getValutazioneDannoPrevalente() {
		return valutazioneDannoPrevalente;
	}

	public void setValutazioneDannoPrevalente(
			Map<Long, String> valutazioneDannoPrevalente) {
		this.valutazioneDannoPrevalente = valutazioneDannoPrevalente;
	}

	public Allegato getAttachmentToDownload() {
		return attachmentToDownload;
	}

	public void setAttachmentToDownload(Allegato attachmentToDownload) {
		this.attachmentToDownload = attachmentToDownload;
	}

	public Localita getFiltro_localita() {
		return filtro_localita;
	}

	public void setFiltro_localita(Localita filtro_localita) {
		this.filtro_localita = filtro_localita;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public String getGeometryText() {
		return geometryText;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public void setGeometryText(String geometryText) {
		this.geometryText = geometryText;
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

	public List<TipologiaDannoBean> getTipologieDannoToRemoveList() {
		return tipologieDannoToRemoveList;
	}

	public void setTipologieDannoToRemoveList(
			List<TipologiaDannoBean> tipologieDannoToRemoveList) {
		this.tipologieDannoToRemoveList = tipologieDannoToRemoveList;
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

	public List<SelectItem> getFontiSelect() {
		if (null == fontiSelect || fontiSelect.isEmpty())
			updateFonti();
		return fontiSelect;
	}

	public void setFontiSelect(List<SelectItem> fontiSelect) {
		this.fontiSelect = fontiSelect;
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
	
}
