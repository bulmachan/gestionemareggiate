package it.epocaricerca.geologia.web.controller;

import it.epocaricerca.geologia.ejb.dao.AnalysisManager;
import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.AggregationType;
import it.epocaricerca.geologia.ejb.tdo.DatiArpaBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.web.model.AnalysisBean;
import it.epocaricerca.geologia.web.util.Messages;
import it.epocaricerca.geologia.web.util.StringUtils;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;

import com.vividsolutions.jts.geom.Geometry;

public class AnalisiController {


	private static Logger logger = Logger.getLogger(AnalisiController.class);

	// Filtri di Ricerca
	private Date inizioValiditaGiorno;
	private Date fineValiditaGiorno;
	private AggregationType aggregationType;

	// Variabili json
	private AnalysisBean json_data;

	//MESSAGGI DI ERRORE
	private boolean analisiFormMessageRendered = false;
	private String analisiFormMessage;



	//Metodi 
	public void pulisciFiltri() {
		inizioValiditaGiorno = null;
		fineValiditaGiorno = null;
		aggregationType = null;
		analisiFormMessageRendered = false;
		json_data = null;
	}

	public void aggiornaAnalisi() {

		if(!checkFormFields())
		{
			json_data = null;
			return;
		}

		logger.info("aggiornaAnalisi "+inizioValiditaGiorno +" "+inizioValiditaGiorno+" "+aggregationType.name());

		
		StringWriter mareggiateWriter = new StringWriter();
		ObjectMapper mareggiateMapper = new ObjectMapper();
		
		StringWriter avvisiMeteoWriter = new StringWriter();
		ObjectMapper avvisiMeteoMapper = new ObjectMapper();
		
		StringWriter datiMeteoWriter = new StringWriter();
		ObjectMapper datiMeteoMapper = new ObjectMapper();
		
		StringWriter localitaDannoWriter = new StringWriter();
		ObjectMapper localitaDannoMapper = new ObjectMapper();
		
		StringWriter dannoTotaleWriter = new StringWriter();
		ObjectMapper dannoTotaleMapper = new ObjectMapper();

		AnalysisManager  analysisManager = JNDIUtils.retrieveEJB(JNDIUtils.AnalysisManagerName, JNDIUtils.AnalysisManagerName);

		List<Object[]> mareggiateObjects = analysisManager.getMareggiateConSenzaImpatto(inizioValiditaGiorno,fineValiditaGiorno,aggregationType);

		mareggiateObjects.add(0, new Object[]{"Data","Mareggiate Con Impatti","Mareggiate Senza Impatti"});
		
		List<Object[]> avvisiMeteoObjects = analysisManager.getAvvisiMeteoConSenzaImpatto(inizioValiditaGiorno,fineValiditaGiorno,aggregationType);

		avvisiMeteoObjects.add(0, new Object[]{"Data","Avvisi Meteo Mareggiata con Impatti","Avvisi Meteo Mareggiata Senza Impatti", "Avvisi Meteo Senza Mareggiate"});
		
		List<Object[]> datiMeteoObjects = analysisManager.getDatiMeteo(inizioValiditaGiorno,fineValiditaGiorno);

		datiMeteoObjects.add(0, new Object[]{"Mareggiate", "Massima Altezza Marea","Massima Altezza Onda","Soglia Livello Marea", "Soglia Altezza Onda"});
		
		List<Object[]> localitaDannoCountObjects = analysisManager.getNumDanniPerLocalita(inizioValiditaGiorno,fineValiditaGiorno);
		
		List<Object[]> danniTotaliObjects = analysisManager.getNumDanniTotali(inizioValiditaGiorno,fineValiditaGiorno);

		danniTotaliObjects.add(0, new Object[]{"Danno", "Totale"});
		
		List<Danno> danniByDataObjects = analysisManager.getDanniByData(inizioValiditaGiorno,fineValiditaGiorno);

		json_data = new AnalysisBean();
		
		json_data.setDanni_by_data_json(this.getGeoJSON(danniByDataObjects).toString());
		
		try {
			mareggiateMapper.writeValue(mareggiateWriter, mareggiateObjects);
			
			json_data.setMareggiate_json(mareggiateWriter.getBuffer().toString());

			avvisiMeteoMapper.writeValue(avvisiMeteoWriter, avvisiMeteoObjects);
			
			json_data.setAvvisi_meteo_json(avvisiMeteoWriter.getBuffer().toString());

			datiMeteoMapper.writeValue(datiMeteoWriter, datiMeteoObjects);
			
			json_data.setDati_meteo_json(datiMeteoWriter.getBuffer().toString());

			localitaDannoMapper.writeValue(localitaDannoWriter, localitaDannoCountObjects);
			
			json_data.setLocalita_danno_count_json(localitaDannoWriter.getBuffer().toString());

			dannoTotaleMapper.writeValue(dannoTotaleWriter, danniTotaliObjects);
			
			json_data.setDanni_totali_json(dannoTotaleWriter.getBuffer().toString());
			
			mareggiateWriter.close();
			avvisiMeteoWriter.close();
			datiMeteoWriter.close();
			localitaDannoWriter.close();
			dannoTotaleWriter.close();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public JSONObject getGeoJSON(List<Danno> danni) {
		
		DannoManager dannoManager = JNDIUtils.retrieveEJB(JNDIUtils.DannoManagerName, JNDIUtils.DannoManagerName);
		
		JSONObject geoJSON = new JSONObject();
		geoJSON.put("type", "FeatureCollection");
		
		JSONArray features = new JSONArray();
		
		try {
			for (Danno danno : danni) {
				List<Geometry> geometries = dannoManager.getGeometry(danno.getId());
				
				for (Geometry geometry : geometries) {
					
					JSONObject point = new JSONObject();
					point.put("type", "Feature");
					
					JSONObject geometryFeature = new JSONObject();
					geometryFeature.put("type", "Point");
					
					JSONArray coordinates = new JSONArray();
					coordinates.put(geometry.getCentroid().getX());
					coordinates.put(geometry.getCentroid().getY());
					geometryFeature.put("coordinates", coordinates);
					
					JSONObject propertiesFeature = new JSONObject();
					propertiesFeature.put("idDanno", danno.getId());
					propertiesFeature.put("tipoDanno", danno.getTipoDanno().getNome());
					
					point.put("geometry", geometryFeature);
					point.put("properties", propertiesFeature);
					
					features.put(point);
				}
			}
			
			geoJSON.put("features", features);
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return geoJSON;
	}
	
	public void setAggregation(String value)
	{
		aggregationType = AggregationType.valueOf(value);
	}
	
	public String getAggregation()
	{
		if(aggregationType == null)
			return AggregationType.MONTH.name();
		else
			return aggregationType.name();
	}


	// Private Methods

	private boolean checkFormFields() {

		if(inizioValiditaGiorno == null || fineValiditaGiorno == null || aggregationType == null  )
		{
			analisiFormMessageRendered = true;
			analisiFormMessage = Messages.FORM_NOT_VALID;
			return false;
		}


		if(inizioValiditaGiorno.after(fineValiditaGiorno))
		{
			analisiFormMessageRendered = true;
			analisiFormMessage = Messages.FORM_ERROR_DATE;
			return false;
		}

		analisiFormMessageRendered = false;
		return true;

	}



	// Getters and Setters

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

	public AnalysisBean getJson_data() {
		return json_data;
	}

	public void setJson_data(AnalysisBean json_data) {
		this.json_data = json_data;
	}

	public boolean isAnalisiFormMessageRendered() {
		return analisiFormMessageRendered;
	}

	public void setAnalisiFormMessageRendered(boolean analisiFormMessageRendered) {
		this.analisiFormMessageRendered = analisiFormMessageRendered;
	}

	public String getAnalisiFormMessage() {
		return analisiFormMessage;
	}

	public void setAnalisiFormMessage(String analisiFormMessage) {
		this.analisiFormMessage = analisiFormMessage;
	}

}
