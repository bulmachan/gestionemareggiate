package it.epocaricerca.geologia.ejb.tdo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CondizioneMeteoBean implements Serializable {
	
	private long id;
	
	private Date inizioValidita;

	private Date fineValidita;
	
	private String estensione;

	private String descrizione;
	
	private String provenienzaOnda;
	
	private String direzioneVentoPrevalente;
	
	private float maxIntensitaVentoPrevalente;
	
	private float maxIntensitaVentoRaffica;
	
	private float maxAltezzaMarea;
	
	private float maxAltezzaOnda;
	
	private float durataSopraSoglia;
	
	private float maxAltezzaOndaSignificativa;

	
	private List<DatiArpaBean> datiArpa = new ArrayList<DatiArpaBean>();
	
	public Date getInizioValidita() {
		return inizioValidita;
	}

	public Date getFineValidita() {
		return fineValidita;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setInizioValidita(Date inizioValidita) {
		this.inizioValidita = inizioValidita;
	}

	public void setFineValidita(Date fineValidita) {
		this.fineValidita = fineValidita;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	
	public void addDatiArpa(DatiArpaBean dato)
	{
		datiArpa.add(dato);
	}

	public List<DatiArpaBean> getDatiArpa() {
		return datiArpa;
	}

	public String getEstensione() {
		return estensione;
	}

	public void setEstensione(String estensione) {
		this.estensione = estensione;
	}

	public String getProvenienzaOnda() {
		return provenienzaOnda;
	}

	public void setProvenienzaOnda(String provenienzaOnda) {
		this.provenienzaOnda = provenienzaOnda;
	}

	public String getDirezioneVentoPrevalente() {
		return direzioneVentoPrevalente;
	}

	public void setDirezioneVentoPrevalente(String direzioneVentoPrevalente) {
		this.direzioneVentoPrevalente = direzioneVentoPrevalente;
	}


	public float getMaxIntensitaVentoPrevalente() {
		return maxIntensitaVentoPrevalente;
	}

	public float getMaxIntensitaVentoRaffica() {
		return maxIntensitaVentoRaffica;
	}

	public float getMaxAltezzaMarea() {
		return maxAltezzaMarea;
	}

	public float getMaxAltezzaOnda() {
		return maxAltezzaOnda;
	}

	public float getDurataSopraSoglia() {
		return durataSopraSoglia;
	}


	public void setMaxIntensitaVentoPrevalente(float maxIntensitaVentoPrevalente) {
		this.maxIntensitaVentoPrevalente = maxIntensitaVentoPrevalente;
	}

	public void setMaxIntensitaVentoRaffica(float maxIntensitaVentoRaffica) {
		this.maxIntensitaVentoRaffica = maxIntensitaVentoRaffica;
	}

	public void setMaxAltezzaMarea(float maxAltezzaMarea) {
		this.maxAltezzaMarea = maxAltezzaMarea;
	}

	public void setMaxAltezzaOnda(float maxAltezzaOnda) {
		this.maxAltezzaOnda = maxAltezzaOnda;
	}

	public void setDurataSopraSoglia(float durataSopraSoglia) {
		this.durataSopraSoglia = durataSopraSoglia;
	}

	public float getMaxAltezzaOndaSignificativa() {
		return maxAltezzaOndaSignificativa;
	}

	public void setMaxAltezzaOndaSignificativa(float maxAltezzaOndaSignificativa) {
		this.maxAltezzaOndaSignificativa = maxAltezzaOndaSignificativa;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

}
