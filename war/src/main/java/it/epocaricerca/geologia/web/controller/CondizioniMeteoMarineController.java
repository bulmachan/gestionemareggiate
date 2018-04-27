package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.RilevazioneManager;
import it.epocaricerca.geologia.ejb.dao.SensorDataSourceManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.service.CondizioniMeteoMarineService;
import it.epocaricerca.geologia.ejb.tdo.CondizioneMeteoBean;
import it.epocaricerca.geologia.ejb.tdo.DatiArpaBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Estensione;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.model.CondizioneMeteoWizardStep;
import it.epocaricerca.geologia.web.util.BeanUtils;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.MinMaxHolder;
import it.epocaricerca.geologia.web.util.file.CSVMareografoReader;
import it.epocaricerca.geologia.web.validator.FileValidator;

import java.io.File;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

public class CondizioniMeteoMarineController extends FilterableHandler<CondizioneMeteo> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7158240595309422466L;

	private static Logger logger = Logger.getLogger(CondizioniMeteoMarineController.class);


	//FORMATTER
	private SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm ");


	//MESSAGGI DI ERRORE
	private boolean meteoFormMessageRendered = false;
	private String meteoFormMessage;

	//GESTIONE DELETE

	private Long itemToDelete;


	//GESTIONE VIEW
	private CondizioneMeteo selectedItem;

	//GESTIONE IN EDIT
	private boolean editMode = false;
	private boolean saveEnabled = false;
	private CondizioneMeteoBean meteoBeanEdit;
	private CondizioneMeteoWizardStep step;

	private Map<DatiArpaBean, Boolean> checkedDatiArpaBean = new HashMap<DatiArpaBean, Boolean>();

	private Map<DatiArpaBean, Boolean> datiMareografoBeanToRemove = new HashMap<DatiArpaBean, Boolean>();


	private List<SelectItem> estTerritorialiSelect;

	// FILTRO DI RICERCA
	private Date filtro_inizioValiditaGiorno;
	private Date filtro_fineValiditaGiorno;
	private String filtro_estTerritoriale;


	// GESTIONE CARIMENTO CSV DATI MAREA
	private Map<String, File> uploadedFiles = new LinkedHashMap<String, File>();
	private boolean datiCsvBoxRendered;
	private String fileToClear;
	private Map<String,DatiArpaBean> datiMareografo = new HashMap<String, DatiArpaBean>();
	private static final String FAKE_PATH = "C:\\fakepath\\";
	private ArrayList<SelectItem> tipiMareografoSelect;

	// JSON contenente i dati da graficare
	private String selectedCondizioneMeteoChartData;
	private int itemIndexRow;
	private String dataType;


	private Map<String, Provenienza> provenienzeOnde;
	private List<SelectItem> provenienzeOndeSelect;
	
	private double chartSelectedPointValue;
	private int chartSelectedPointId;
	
	public void saveChartNewValue() {
		logger.info("chartSelectedPointValue: "+chartSelectedPointValue);
		logger.info("chartSelectedPointId: "+chartSelectedPointId);
		
		DatiArpaBean datiArpa = null;

		if(dataType.equalsIgnoreCase("arpa"))
			datiArpa = meteoBeanEdit.getDatiArpa().get(itemIndexRow);
		else
			datiArpa = datiMareografo.get(fileToClear);

		List<DatoSensore> datiSensore = datiArpa.getDatiSensore();
		
		datiSensore.get(chartSelectedPointId).setValue(chartSelectedPointValue);
		
		MinMaxHolder holder = BeanUtils.computeMinMax(datiSensore);
		datiArpa.setMaxValue(holder.max);
		datiArpa.setMinValue(holder.min);
		
		processValueChange(null);
		
		openItemChart();
		
	}

	public void openItemChart() {

		List<Object[]> values = new ArrayList<Object[]>();

		DatiArpaBean datiArpa = null;

		if(dataType.equalsIgnoreCase("arpa") || dataType.equalsIgnoreCase("details"))
			datiArpa = meteoBeanEdit.getDatiArpa().get(itemIndexRow);
		else
			datiArpa = datiMareografo.get(fileToClear);


		logger.info("openItemChart "+datiArpa.getStazione() +" "+datiArpa.getVariabile());

		values.add(new Object[]{"data",datiArpa.getVariabile()});

		List<DatoSensore> datiSensore = datiArpa.getDatiSensore();

		for (DatoSensore datoSensore : datiSensore) {
			values.add(new Object[]{ dateFormat.format(datoSensore.getTimestamp()),datoSensore.getValue()});
		}


		StringWriter writer = new StringWriter();
		ObjectMapper mapper = new ObjectMapper();
		try {
			mapper.writeValue(writer, values);

			// this.setSelectedCondizioneMeteoChartData("[[\"'x\", \"vento direzione media oraria vettoriale\"],[\"04 09 2013\", 1],[\"05 09 2013\", 2]]");

			this.setSelectedCondizioneMeteoChartData(datiArpa.getStazione()+"|"+writer.getBuffer().toString() );
			writer.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

	}


	public void pulisciFiltri() {
		filtro_inizioValiditaGiorno = null;
		filtro_fineValiditaGiorno = null;
		filtro_estTerritoriale = null;
		updateDataModel();
	}

	public void filtraCondizioneMeteo() {
		logger.info("filtraCondizioneMeteo "+filtro_inizioValiditaGiorno+" "+filtro_fineValiditaGiorno);
		logger.info(filtro_estTerritoriale);
		updateDataModel();
	}

	private void cleanForm() {

		editMode = false;
		saveEnabled = false;
		meteoFormMessageRendered = false;
		meteoBeanEdit = null;
		checkedDatiArpaBean.clear();

		if (null != datiMareografoBeanToRemove)
			datiMareografoBeanToRemove.clear();

		if (null != uploadedFiles)
			uploadedFiles.clear();

		datiMareografo.clear();
	}

	public String deleteItem() {

		CondizioneMeteoManager meteoCRUD = JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerName, JNDIUtils.CondizioneMeteoManagerName);

		try {
			meteoCRUD.remove(itemToDelete);
		} catch (EntityNotFoundException e) {
			meteoFormMessage = Messages.FORM_ERROR_PERSIST;
			meteoFormMessageRendered = true;
			//return "";

		}
		pulisciFiltri();
		return "delete";
	}

	@Override
	public void updateDataModel()
	{
		super.updateDataModel();

	}


	public String nuovaCondizioneMeteoMarina() {
		cleanForm();
		meteoBeanEdit = new CondizioneMeteoBean();
		step = CondizioneMeteoWizardStep.STEP1;
		return "nuovaCondizioneMeteoMarina";
	}

	public String annullaCreazione() {

		cleanForm();
		this.editMode = false;
		return "annullaCreazioneMeteo";
	}


	/** Gestione EDIT **/


	public String editItemDetails() {

		checkedDatiArpaBean.clear();
		if (null != datiMareografo)
			datiMareografo.clear();

		if (null != datiMareografoBeanToRemove)
			datiMareografoBeanToRemove.clear();

		if (null != uploadedFiles)
			uploadedFiles.clear();

		this.editMode = true;
		this.saveEnabled = false;
		this.selectedItem = (CondizioneMeteo) dataModel.getRowData();

		step = CondizioneMeteoWizardStep.STEP1;

		CondizioneMeteoManager meteoCRUD = JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerName, JNDIUtils.CondizioneMeteoManagerName);

		try {
			this.selectedItem.setRilevazioni(meteoCRUD.getRilevazioni(this.selectedItem.getId()));

			this.meteoBeanEdit = new CondizioneMeteoBean();
			this.meteoBeanEdit.setDescrizione(this.selectedItem.getDescrizione());
			this.meteoBeanEdit.setEstensione(this.selectedItem.getEstensioneTerritoriale().getNome());
			this.meteoBeanEdit.setInizioValidita(this.selectedItem.getInizioValidita());
			this.meteoBeanEdit.setFineValidita(this.selectedItem.getFineValidita());


			// TENTATIVO DI GESTIRE I NULLI, MA POI IL SALVATAGGIO NON FUNZIONA:
			
			//this.meteoBeanEdit.setProvenienzaOnda(this.selectedItem.getOnda().getNome());
			//this.meteoBeanEdit.setDirezioneVentoPrevalente(this.selectedItem.getDirezioneVentoPrevalente().getNome());

			if(this.selectedItem.getOnda()!=null)
				this.meteoBeanEdit.setProvenienzaOnda(this.selectedItem.getOnda().getNome());
			else
				this.meteoBeanEdit.setProvenienzaOnda("-");
			
			if(this.selectedItem.getDirezioneVentoPrevalente()!=null)
				this.meteoBeanEdit.setDirezioneVentoPrevalente(this.selectedItem.getDirezioneVentoPrevalente().getNome());
			else
				this.meteoBeanEdit.setDirezioneVentoPrevalente("-");				
			
			
			

			this.meteoBeanEdit.setMaxAltezzaMarea(this.selectedItem.getMaxAltezzaMarea());
			this.meteoBeanEdit.setMaxAltezzaOnda(this.selectedItem.getMaxAltezzaOnda());
			this.meteoBeanEdit.setDurataSopraSoglia(this.selectedItem.getDurataSopraSoglia());
			this.meteoBeanEdit.setMaxAltezzaOndaSignificativa(this.selectedItem.getMaxAltezzaOndaSignificativa());
			this.meteoBeanEdit.setMaxIntensitaVentoPrevalente(this.selectedItem.getMaxIntensitaVentoPrevalente());
			this.meteoBeanEdit.setMaxIntensitaVentoRaffica(this.selectedItem.getMaxIntensitaVentoRaffica());

			List<Rilevazione> rilevazioni = this.selectedItem.getRilevazioni();
			for (Rilevazione rilevazione : rilevazioni) {

				DatiArpaBean arpaBean = new  DatiArpaBean();
				arpaBean.setId(rilevazione.getId());

				RilevazioneManager rilevazioneManager = JNDIUtils.retrieveEJB(JNDIUtils.RilevazioneManagerName, JNDIUtils.RilevazioneManagerName);


				arpaBean.setDatiSensore(rilevazioneManager.findDatiById(rilevazione.getId()));

				arpaBean.setStazione(rilevazione.getStazione().getCodice()+" - "+rilevazione.getStazione().getNome()+" - "+rilevazione.getStazione().getRete());
				arpaBean.setStazioneId(rilevazione.getStazione().getId());
				arpaBean.setVariabile(rilevazione.getVariabileStazione().getCodice()+" - "+rilevazione.getVariabileStazione().getDescrizione());
				arpaBean.setVariabileId(rilevazione.getVariabileStazione().getId());
				arpaBean.setMaxValue(rilevazione.getMaxValue());
				arpaBean.setMinValue(rilevazione.getMinValue());

				if(rilevazione.getStazione().getSorgente_dati().equalsIgnoreCase("ARPA")){
					this.meteoBeanEdit.addDatiArpa(arpaBean);
					checkedDatiArpaBean.put(arpaBean, true);
				}
				else{
					datiCsvBoxRendered = true;
					this.datiMareografo.put(arpaBean.getStazione(), arpaBean);
				}
			}

		} catch (EntityNotFoundException e) {

			//TODO GEstione visualizzazione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}

		return "toCondizioniMeteoMarineEdit";
	}


	public String viewItemDetails() {
		this.selectedItem = (CondizioneMeteo) dataModel.getRowData();
		CondizioneMeteoManager meteoCRUD = JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerName, JNDIUtils.CondizioneMeteoManagerName);

		try {
			this.selectedItem.setRilevazioni(meteoCRUD.getRilevazioni(this.selectedItem.getId()));

			this.meteoBeanEdit = new CondizioneMeteoBean();
			this.meteoBeanEdit.setDescrizione(this.selectedItem.getDescrizione());
			this.meteoBeanEdit.setEstensione(this.selectedItem.getEstensioneTerritoriale().getNome());
			this.meteoBeanEdit.setInizioValidita(this.selectedItem.getInizioValidita());
			this.meteoBeanEdit.setFineValidita(this.selectedItem.getFineValidita());
			
			if(this.selectedItem.getOnda()!=null)
				this.meteoBeanEdit.setProvenienzaOnda(this.selectedItem.getOnda().getNome());
			else
				this.meteoBeanEdit.setProvenienzaOnda("-");
			
			if(this.selectedItem.getDirezioneVentoPrevalente()!=null)
				this.meteoBeanEdit.setDirezioneVentoPrevalente(this.selectedItem.getDirezioneVentoPrevalente().getNome());
			else
				this.meteoBeanEdit.setDirezioneVentoPrevalente("-");

			this.meteoBeanEdit.setMaxAltezzaMarea(this.selectedItem.getMaxAltezzaMarea());
			this.meteoBeanEdit.setMaxAltezzaOnda(this.selectedItem.getMaxAltezzaOnda());
			this.meteoBeanEdit.setDurataSopraSoglia(this.selectedItem.getDurataSopraSoglia());
			this.meteoBeanEdit.setMaxAltezzaOndaSignificativa(this.selectedItem.getMaxAltezzaOndaSignificativa());
			this.meteoBeanEdit.setMaxIntensitaVentoPrevalente(this.selectedItem.getMaxIntensitaVentoPrevalente());
			this.meteoBeanEdit.setMaxIntensitaVentoRaffica(this.selectedItem.getMaxIntensitaVentoRaffica());


			List<Rilevazione> rilevazioni = this.selectedItem.getRilevazioni();
			for (Rilevazione rilevazione : rilevazioni) {

				DatiArpaBean arpaBean = new  DatiArpaBean();

				RilevazioneManager rilevazioneManager = JNDIUtils.retrieveEJB(JNDIUtils.RilevazioneManagerName, JNDIUtils.RilevazioneManagerName);


				arpaBean.setDatiSensore(rilevazioneManager.findDatiById(rilevazione.getId()));

				arpaBean.setStazione(rilevazione.getStazione().getCodice()+" - "+rilevazione.getStazione().getNome()+" - "+rilevazione.getStazione().getRete());
				arpaBean.setStazioneId(rilevazione.getStazione().getId());
				arpaBean.setVariabile(rilevazione.getVariabileStazione().getCodice()+" - "+rilevazione.getVariabileStazione().getDescrizione());
				arpaBean.setVariabileId(rilevazione.getVariabileStazione().getId());
				arpaBean.setMaxValue(rilevazione.getMaxValue());
				arpaBean.setMinValue(rilevazione.getMinValue());

				this.meteoBeanEdit.addDatiArpa(arpaBean);
			}

		} catch (EntityNotFoundException e) {

			//TODO GEstione visualizzazione errori
			logger.error(e.getMessage());
			updateDataModel();
			return "delete";
		}

		return "toCondizioniMeteoMarineDetails";
	}

	public String salvaCondizioneMeteo() 
	{



		CondizioniMeteoMarineService condizioniMeteoMarineService = JNDIUtils.retrieveEJB(JNDIUtils.CondizioneMeteoManagerServiceName, JNDIUtils.CondizioneMeteoManagerServiceName);
		
		Long idCondizioneMeteo = null;
		try {


			if(this.editMode){
				this.meteoBeanEdit.setId(this.selectedItem.getId());
				condizioniMeteoMarineService.salvaCondizioneMeteo(editMode, meteoBeanEdit, this.selectedItem.getRilevazioni(), checkedDatiArpaBean, datiMareografoBeanToRemove, datiMareografo);

			}
			else
			{
				condizioniMeteoMarineService.salvaCondizioneMeteo(editMode, meteoBeanEdit, null, checkedDatiArpaBean, datiMareografoBeanToRemove, datiMareografo);

			}


		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

			pulisciFiltri();

			this.meteoFormMessageRendered = true;
			this.meteoFormMessage = Messages.FORM_ERROR_PERSIST;


			return "";
		}

		pulisciFiltri();

		return "condizioniMeteoSalvata";
	}


	public String next() {

		switch (step) {
		case STEP1:
		{


			if(!checkMeteoFormFields()){
				logger.info("**** meteoFormMessageRendered "+meteoFormMessageRendered);
			
				return "";
			}

			logger.info("Go To Step2");

			step = CondizioneMeteoWizardStep.STEP2;

			if(editMode == false){
				StazioneManager stazioneManager = JNDIUtils.retrieveEJB(JNDIUtils.StazioneManagerName, JNDIUtils.StazioneManagerName);
				SensorDataSourceManager arpaDataSource = JNDIUtils.retrieveEJB(JNDIUtils.ArpaDataSourceName, JNDIUtils.ArpaDataSourceName);


				List<Stazione> stazioni = stazioneManager.findItemBySorgente("ARPA");

				logger.info("Numero di stazioni da interrogare "+stazioni.size());

				try {
					for (Stazione stazione : stazioni) {

						logger.info("Interrogo stazione "+stazione.getNome());
						List<VariabileStazione> variabili = stazione.getVariabili();
						for (VariabileStazione variabileStazione : variabili) {
							logger.info("variabile "+variabileStazione.getCodice());

							List<DatoSensore> datiSensore = arpaDataSource.getDataFromVariabileStazione(stazione.getId(), variabileStazione.getId(), meteoBeanEdit.getInizioValidita(), meteoBeanEdit.getFineValidita());
							logger.info("datiSensore "+datiSensore.size());

							DatiArpaBean datoArpa = new DatiArpaBean();
							datoArpa.setDatiSensore(datiSensore);
							datoArpa.setStazione(stazione.getCodice()+" - "+stazione.getNome()+" - "+stazione.getRete());
							datoArpa.setVariabile(variabileStazione.getCodice()+" - "+variabileStazione.getDescrizione());
							datoArpa.setStazioneId(stazione.getId());
							datoArpa.setVariabileId(variabileStazione.getId());

							if(datiSensore.size() > 0){

								MinMaxHolder holder = BeanUtils.computeMinMax(datiSensore);
								datoArpa.setMaxValue(holder.max);
								datoArpa.setMinValue(holder.min);
							}
							else
							{
								datoArpa.setMaxValue(Double.NaN);
								datoArpa.setMinValue(Double.NaN);
							}


							meteoBeanEdit.addDatiArpa(datoArpa);


						}
					}


				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();

					this.meteoFormMessageRendered = true;
					this.meteoFormMessage = Messages.FORM_ERROR_ARPA;
				}

			}

		}
		break;

		case STEP2:
		{

			logger.info("Go To Step3");
			step = CondizioneMeteoWizardStep.STEP3;
		}
		break;

		case STEP3:
		{

			this.meteoFormMessageRendered = false;

			logger.info("Go To Step4");
			step = CondizioneMeteoWizardStep.STEP4;
			saveEnabled = true;

			
			
			StazioneManager stazioneManager = JNDIUtils.retrieveEJB(JNDIUtils.StazioneManagerName, JNDIUtils.StazioneManagerName);
			VariabileManager variabileCRUD = JNDIUtils.retrieveEJB(JNDIUtils.VariabileManagerName, JNDIUtils.VariabileManagerName);

			Collection<DatiArpaBean> datiCsv =  datiMareografo.values();

			for (DatiArpaBean dato : datiCsv) {
				Stazione stazione = stazioneManager.findItemById(dato.getStazioneId());
				dato.setStazione(stazione.getNome());
				dato.setVariabile("var1 - "+variabileCRUD.findItemByCodice("var1").getDescrizione());
			
			}
			processValueChange(null);




		}
		break;


		default:
			break;
		}


		this.meteoFormMessageRendered = false;

		return "next";
	}

	public String previous()
	{
		this.saveEnabled = false;

		switch (step) {
		case STEP1:

			break;

		case STEP2:{
			step = CondizioneMeteoWizardStep.STEP1;
			checkedDatiArpaBean.clear();
			meteoBeanEdit.getDatiArpa().clear();
			this.meteoFormMessageRendered = false;
		}
		break;

		case STEP3:
			step = CondizioneMeteoWizardStep.STEP2;
			break;

		case STEP4:
			step = CondizioneMeteoWizardStep.STEP3;
			break;

		default:
			break;
		}
		return "previous";
	}

	private boolean checkMeteoFormFields() {
		if(meteoBeanEdit.getInizioValidita() == null || meteoBeanEdit.getFineValidita()==null || meteoBeanEdit.getEstensione() == null  )
		{
			this.meteoFormMessageRendered = true;
			this.meteoFormMessage = Messages.FORM_NOT_VALID;
			logger.info(this.meteoFormMessage);
			return false;
		}



		if((meteoBeanEdit.getInizioValidita().after(meteoBeanEdit.getFineValidita())))
		{
			meteoFormMessageRendered = true;
			meteoFormMessage = Messages.FORM_ERROR_DATE;
			return false;
		}

		meteoFormMessageRendered = false;
		return true;
	}


	/** Gestione aggiornamento valori dati sintetici **/

	public void processValueChange(ActionEvent event)throws AbortProcessingException {


		logger.info("processValueChange");
		
		Set<DatiArpaBean> keys = this.getCheckedDatiArpaBean().keySet();

		List<DatiArpaBean> selected = new ArrayList<DatiArpaBean>();
		for (DatiArpaBean datiArpaBean : keys) {
			if( this.getCheckedDatiArpaBean().get(datiArpaBean))
				selected.add(datiArpaBean);
		}


		VariabileManager variabileManager = JNDIUtils.retrieveEJB(JNDIUtils.VariabileManagerName, JNDIUtils.VariabileManagerName);

		Collection<DatiArpaBean> dati = datiMareografo.values();


		try{
			this.meteoBeanEdit.setMaxAltezzaOnda(BeanUtils.computeMax(selected, "684 - "+variabileManager.findItemByCodice("684").getDescrizione()));
		}catch(Exception e)
		{
			
			logger.error(e.getMessage()+" variable 684");
		}

		try{
			this.meteoBeanEdit.setMaxIntensitaVentoRaffica(BeanUtils.computeMax(selected, "351 - "+variabileManager.findItemByCodice("351").getDescrizione()));
		}catch(Exception e)
		{
			logger.error(e.getMessage()+" variable 351");
		}

		try{
			this.meteoBeanEdit.setMaxIntensitaVentoPrevalente(BeanUtils.computeMax(selected, "433 - "+variabileManager.findItemByCodice("433").getDescrizione()));
		}catch(Exception e)
		{
			logger.error(e.getMessage()+" variable 433");
		}
		
		try{
			this.meteoBeanEdit.setMaxAltezzaOndaSignificativa(BeanUtils.computeMax(selected, "629 - "+variabileManager.findItemByCodice("629").getDescrizione()));
		}catch(Exception e)
		{
			logger.error(e.getMessage()+" variable 629");
		}

		try{
			this.meteoBeanEdit.setMaxAltezzaMarea(BeanUtils.computeMax(new ArrayList(dati), "var1 - "+variabileManager.findItemByCodice("var1").getDescrizione()));
		}catch(Exception e)
		{
			logger.error(e.getMessage()+" variable var1");
		}



	}



	/** Gestione Upload Dati mareografo**/

	public void fileUploadListener(UploadEvent event) throws Exception {
		UploadItem item = event.getUploadItem();
		try{
			FileValidator f= new FileValidator();	
			if (f.Validate(item.getFile(), item.getFileName())) {
		
				List<DatoSensore> dati = CSVMareografoReader.readData(item.getFile(),this.meteoBeanEdit.getInizioValidita(),this.meteoBeanEdit.getFineValidita());
				logger.info("Dati recuperati "+dati.size());
				if(dati == null || dati.size() == 0)
					throw new Exception("File non conforme alla specifiche");
				uploadedFiles.put(item.getFileName(), item.getFile());
	
				DatiArpaBean datiArpa = new DatiArpaBean();
				datiArpa.setDatiSensore(dati);
				
				if(dati.size() > 0){

					MinMaxHolder holder = BeanUtils.computeMinMax(dati);
					datiArpa.setMaxValue(holder.max);
					datiArpa.setMinValue(holder.min);
				}
				else
				{
					datiArpa.setMaxValue(Double.NaN);
					datiArpa.setMinValue(Double.NaN);
				}

				datiMareografo.put(item.getFileName(), datiArpa);

				datiCsvBoxRendered = true;
				this.meteoFormMessageRendered = false;
			} else {
				this.meteoFormMessage = "File: " + item.getFileName() + " " + Messages.FORM_ERROR_IOFILE_NOTVALID; //+ mimeType
				this.meteoFormMessageRendered = true;				
			}
		}catch (Exception e) {
			e.printStackTrace();
			//TODO gestire messaggio di errore nel caso in cui il file non sia adeguato

			this.meteoFormMessage = Messages.FORM_ERROR_CSV;
			this.meteoFormMessageRendered = true;
		}


	}


	public void clearFileUpload()
	{
		logger.info("clearFileUpload "+getFileToClear() );
		clearFileUpload(null);
	}
	public void clearFileUpload(ActionEvent event) {

		if(!editMode){
			if (getFileToClear() != null && !"".equals(getFileToClear())) {
				logger.info("filetoclear "+ this.getFileToClear().replace(FAKE_PATH, ""));
				uploadedFiles.remove(this.getFileToClear().replace(FAKE_PATH, ""));
				datiMareografo.remove(this.getFileToClear().replace(FAKE_PATH, ""));

			} else {
				logger.info("clearing all files");
				uploadedFiles.clear();
				datiMareografo.clear();

			}
		}
		else
		{
			if (getFileToClear() != null && !"".equals(getFileToClear())) 
			{
				
				processValueChange(null);
				datiMareografoBeanToRemove.put(datiMareografo.get(this.getFileToClear().replace(FAKE_PATH, "")), true);
				uploadedFiles.remove(this.getFileToClear().replace(FAKE_PATH, ""));
				datiMareografo.remove(this.getFileToClear().replace(FAKE_PATH, ""));

			}
		}

	}

	public List<String> getDatiMareografoKeys(){
		List<String> ret = new ArrayList<String>();
		for (String s : datiMareografo.keySet())
			ret.add(s);
		return ret;
	}

	public List<File> getFiles()
	{
		List<File> files = new ArrayList<File>();

		for (File s : uploadedFiles.values())
			files.add(s);
		return files;
	}


	/** FilterableHandler methods**/

	@Override
	public void resetSearchParameters() {

	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();

		if(filtro_estTerritoriale != null)
		{
			NestedCriterion estTerritoriale = new NestedCriterion("estensioneTerritoriale", Restrictions.eq("nome", filtro_estTerritoriale));
			criterions.add(estTerritoriale);
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

	/** Private Method **/

	private void updateEstTerritoriali()
	{
		estTerritorialiSelect = new ArrayList<SelectItem>();
		List<Estensione> estensioniFromDB = JNDIUtils.getEstensioneManager().selectAll();
		estTerritorialiSelect.add(new SelectItem(null,"-"));
		for (Estensione t : estensioniFromDB) {
			estTerritorialiSelect.add(new SelectItem(t.getNome()));
		}
	}


	private void updateTipiMareografo() {

		tipiMareografoSelect = new ArrayList<SelectItem>();


		StazioneManager stazioneManager = JNDIUtils.retrieveEJB(JNDIUtils.StazioneManagerName, JNDIUtils.StazioneManagerName);

		List<Stazione> stazioni = stazioneManager.findItemBySorgente("MAREOGRAFO");

		for (Stazione stazione : stazioni) {
			tipiMareografoSelect.add(new SelectItem(stazione.getId(),stazione.getNome()));
		}

	}

	/** Getter and Setter **/

	public List<SelectItem> getTipiMareografoSelect()
	{
		if (null == tipiMareografoSelect || tipiMareografoSelect.isEmpty())
			updateTipiMareografo();
		return tipiMareografoSelect;
	}

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


	public String getFiltro_estTerritoriale() {
		return filtro_estTerritoriale;
	}

	public void setFiltro_estTerritoriale(String filtro_estTerritoriale) {
		this.filtro_estTerritoriale = filtro_estTerritoriale;
	}

	public void setEstTerritorialiSelect(List<SelectItem> estTerritorialiSelect) {
		this.estTerritorialiSelect = estTerritorialiSelect;
	}

	public List<SelectItem> getEstTerritorialiSelect() {
		if (null == estTerritorialiSelect || estTerritorialiSelect.isEmpty())
			updateEstTerritoriali();
		return estTerritorialiSelect;
	}

	public boolean isMeteoFormMessageRendered() {
		return meteoFormMessageRendered;
	}

	public String getMeteoFormMessage() {
		return meteoFormMessage;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public boolean isSaveEnabled() {
		return saveEnabled;
	}

	public void setMeteoFormMessageRendered(boolean meteoFormMessageRendered) {
		this.meteoFormMessageRendered = meteoFormMessageRendered;
	}

	public void setMeteoFormMessage(String meteoFormMessage) {
		this.meteoFormMessage = meteoFormMessage;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public void setSaveEnabled(boolean saveEnabled) {
		this.saveEnabled = saveEnabled;
	}

	public CondizioneMeteoBean getMeteoBeanEdit() {
		return meteoBeanEdit;
	}

	public void setMeteoBeanEdit(CondizioneMeteoBean meteoBeanEdit) {
		this.meteoBeanEdit = meteoBeanEdit;
	}

	public Map<DatiArpaBean, Boolean> getCheckedDatiArpaBean() {
		return checkedDatiArpaBean;
	}

	public void setCheckedDatiArpaBean(
			Map<DatiArpaBean, Boolean> checkedDatiArpaBean) {
		this.checkedDatiArpaBean = checkedDatiArpaBean;
	}

	public boolean isDatiCsvBoxRendered() {
		return datiCsvBoxRendered;
	}

	public void setDatiCsvBoxRendered(boolean datiCsvBoxRendered) {
		this.datiCsvBoxRendered = datiCsvBoxRendered;
	}

	public String getFileToClear() {
		return fileToClear;
	}

	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}

	public Map<String, DatiArpaBean> getDatiMareografo() {
		return datiMareografo;
	}

	public void setDatiMareografo(Map<String, DatiArpaBean> datiMareografo) {
		this.datiMareografo = datiMareografo;
	}

	public Map<String, File> getUploadedFiles() {
		return uploadedFiles;
	}

	public void setUploadedFiles(Map<String, File> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}

	public Long getItemToDelete() {
		return itemToDelete;
	}

	public void setItemToDelete(Long itemToDelete) {
		this.itemToDelete = itemToDelete;
	}

	public CondizioneMeteo getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(CondizioneMeteo selectedItem) {
		this.selectedItem = selectedItem;
	}


	public String getSelectedCondizioneMeteoChartData() {
		return selectedCondizioneMeteoChartData;
	}


	public void setSelectedCondizioneMeteoChartData(
			String selectedCondizioneMeteoChartData) {
		this.selectedCondizioneMeteoChartData = selectedCondizioneMeteoChartData;
	}


	public int getItemIndexRow() {
		return itemIndexRow;
	}


	public void setItemIndexRow(int itemIndexRow) {
		this.itemIndexRow = itemIndexRow;
	}


	public String getDataType() {
		return dataType;
	}


	public void setDataType(String dataType) {
		this.dataType = dataType;
	}


	public List<SelectItem> getProvenienzeOndeSelect() {
		if (provenienzeOndeSelect == null || provenienzeOndeSelect.isEmpty())
			updateProvenienzeOnde();
		return provenienzeOndeSelect;
	}

	public void setProvenienzeOndeSelect(List<SelectItem> provenienzeOndeSelect) {
		this.provenienzeOndeSelect = provenienzeOndeSelect;
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

	public double getChartSelectedPointValue() {
		return chartSelectedPointValue;
	}

	public void setChartSelectedPointValue(double chartSelectedPointValue) {
		this.chartSelectedPointValue = chartSelectedPointValue;
	}

	public int getChartSelectedPointId() {
		return chartSelectedPointId;
	}

	public void setChartSelectedPointId(int chartSelectedPointId) {
		this.chartSelectedPointId = chartSelectedPointId;
	}







}
