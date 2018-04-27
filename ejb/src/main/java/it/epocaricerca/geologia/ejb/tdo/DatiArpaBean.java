package it.epocaricerca.geologia.ejb.tdo;

import it.epocaricerca.geologia.model.DatoSensore;

import java.io.Serializable;
import java.util.List;

public class DatiArpaBean implements Serializable{

	
	private long id;
	
	private List<DatoSensore> datiSensore;
	
	private double maxValue;
	
	private double minValue;
	
	private long stazioneId;
	
	private long variabileId;
	
	private String stazione;
	
	private String variabile;

	public List<DatoSensore> getDatiSensore() {
		return datiSensore;
	}

	public long getStazioneId() {
		return stazioneId;
	}

	public long getVariabileId() {
		return variabileId;
	}

	public String getStazione() {
		return stazione;
	}

	public String getVariabile() {
		return variabile;
	}

	public void setDatiSensore(List<DatoSensore> datiSensore) {
		this.datiSensore = datiSensore;
	}

	public void setStazioneId(long stazioneId) {
		this.stazioneId = stazioneId;
	}

	public void setVariabileId(long variabileId) {
		this.variabileId = variabileId;
	}

	public void setStazione(String stazione) {
		this.stazione = stazione;
	}

	public void setVariabile(String variabile) {
		this.variabile = variabile;
	}

	public double getMaxValue() {
		return maxValue;
	}

	public double getMinValue() {
		return minValue;
	}

	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}

	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
}
