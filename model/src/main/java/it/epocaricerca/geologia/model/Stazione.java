package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="MRG_STAZIONI")
public class Stazione implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7880980067941741330L;

	@Id
	@GeneratedValue
	private long id;
	
	private String nome;
	
	private String codice;
	
	//indica a quale rete la stazione appartiene, es. BOA, LOCALI
	//deve coincidere con i parametri ARPA per la chiamata all'endpoint REST/JSON
	private String rete;
	
	//indica se la sorgente dati e' ARPA oppoure qualche altra organizzazione
	private String sorgente_dati;
	
	private double latitude;
	
	private double longitude;
	
	@ManyToMany(fetch=FetchType.EAGER,cascade = CascadeType.PERSIST)
	@JoinTable(name="MRG_STAZIONE_VARIABILE")
	private List<VariabileStazione> variabili;

	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getCodice() {
		return codice;
	}

	public String getRete() {
		return rete;
	}

	public String getSorgente_dati() {
		return sorgente_dati;
	}

	public List<VariabileStazione> getVariabili() {
		return variabili;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public void setRete(String rete) {
		this.rete = rete;
	}

	public void setSorgente_dati(String sorgente_dati) {
		this.sorgente_dati = sorgente_dati;
	}

	public void setVariabili(List<VariabileStazione> variabili) {
		this.variabili = variabili;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void addVariabile(VariabileStazione variabile)
	{
		if(variabili == null)
			variabili = new ArrayList<VariabileStazione>();
		
		if(!variabili.contains(variabile))
			variabili.add(variabile);
	}
	
}
