package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.JoinTable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_CONDIZIONI_METEO")
public class CondizioneMeteo implements Serializable {

	private static final long serialVersionUID = 4313472984641677118L;

	@Id
	@GeneratedValue
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date inizioValidita;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date fineValidita;
	
	@ManyToOne(optional=false)
	private Estensione estensioneTerritoriale;
	
	
	
	@Lob
	private String descrizione;
	
	@OneToMany(/*cascade={CascadeType.ALL}*/)
	@JoinTable( name="MRG_METEO_RILEVAZIONE" )
	private List<Rilevazione> rilevazioni;
	
	
	@ManyToOne
	private Mareggiata mareggiata;

	@ManyToOne
	private Provenienza onda;
	
	@Basic(optional=false)
	private float maxAltezzaOndaSignificativa;
	
	@Basic(optional=false)
	private float maxIntensitaVentoPrevalente;
	
	@Basic(optional=false)
	private float maxIntensitaVentoRaffica;
	
	@Basic(optional=false)
	private float maxAltezzaMarea;
	
	@Basic(optional=false)
	private float maxAltezzaOnda;
	
	@Basic(optional=false)
	private float durataSopraSoglia;
	
	
	@ManyToOne
	@Basic
	private Provenienza direzioneVentoPrevalente;
		
	/** Getter and Setter **/
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public Estensione getEstensioneTerritoriale() {
		return estensioneTerritoriale;
	}

	public void setEstensioneTerritoriale(Estensione estensioneTerritoriale) {
		this.estensioneTerritoriale = estensioneTerritoriale;
	}

	public Provenienza getOnda() {
		return onda;
	}

	public void setOnda(Provenienza onda) {
		this.onda = onda;
	}

	public float getMaxIntensitaVentoPrevalente() {
		return maxIntensitaVentoPrevalente;
	}

	public void setMaxIntensitaVentoPrevalente(float maxIntensitaVentoPrevalente) {
		this.maxIntensitaVentoPrevalente = maxIntensitaVentoPrevalente;
	}

	public float getMaxIntensitaVentoRaffica() {
		return maxIntensitaVentoRaffica;
	}

	public void setMaxIntensitaVentoRaffica(float maxIntensitaVentoRaffica) {
		this.maxIntensitaVentoRaffica = maxIntensitaVentoRaffica;
	}

	public float getDurataSopraSoglia() {
		return durataSopraSoglia;
	}

	public void setDurataSopraSoglia(float durataSopraSoglia) {
		this.durataSopraSoglia = durataSopraSoglia;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Mareggiata getMareggiata() {
		return mareggiata;
	}

	public void setMareggiata(Mareggiata mareggiata) {
		this.mareggiata = mareggiata;
	}

	public List<Rilevazione> getRilevazioni() {
		if(this.rilevazioni == null)
			this.rilevazioni = new ArrayList<Rilevazione>();
		return rilevazioni;
	}

	public void setRilevazioni(List<Rilevazione> rilevazioni) {
		this.rilevazioni = rilevazioni;
	}

	public float getMaxAltezzaMarea() {
		return maxAltezzaMarea;
	}

	public float getMaxAltezzaOnda() {
		return maxAltezzaOnda;
	}

	public void setMaxAltezzaMarea(float maxAltezzaMarea) {
		this.maxAltezzaMarea = maxAltezzaMarea;
	}

	public void setMaxAltezzaOnda(float maxAltezzaOnda) {
		this.maxAltezzaOnda = maxAltezzaOnda;
	}

	public Provenienza getDirezioneVentoPrevalente() {
		return direzioneVentoPrevalente;
	}

	public void setDirezioneVentoPrevalente(Provenienza direzioneVentoPrevalente) {
		this.direzioneVentoPrevalente = direzioneVentoPrevalente;
	}

	public float getMaxAltezzaOndaSignificativa() {
		return maxAltezzaOndaSignificativa;
	}

	public void setMaxAltezzaOndaSignificativa(float maxAltezzaOndaSignificativa) {
		this.maxAltezzaOndaSignificativa = maxAltezzaOndaSignificativa;
	}
	
	
}
