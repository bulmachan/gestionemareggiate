package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_PREVISIONI_METEO")
public class PrevisioneMeteo implements Serializable {

	
	private static final long serialVersionUID = 1267552293967742919L;


	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String idAvviso;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date dataAvviso;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date inizioValidita;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date fineValidita;
	
	@ManyToOne
	@Basic(optional=false)
	private Tendenza tendenza;
	
	@ManyToOne
	private Altezza altezzaStimataOnda;
	
	
	@Lob
	private String descrizione;
	
	@OneToMany(/*cascade={CascadeType.ALL}*/)
	@JoinTable( name="MRG_PREVISIONEMETEO_ALLEGATI" )
	private List<Allegato> allegati;
	
	@OneToMany(/*cascade={CascadeType.ALL},*/ mappedBy="previsione")
	private List<EventoCostiero> eventiCostieri;
	
	@ManyToOne
	private Mareggiata mareggiata;
	
	
	private float pianuraVento;
	private float pianuraVentoMax;
	
	@ManyToOne
	private Provenienza pianuraDirezioneVento;
	
	
	private float pedemontanaVento;
	private float pedemontanaVentoMax;
	
	@ManyToOne
	private Provenienza pedemontanaDirezioneVento;

	@ManyToOne
	private Provenienza direzioneProvStimataOnda;

	/** Getter and Setter **/

	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}


	public String getDescrizione() {
		return descrizione;
	}


	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public List<Allegato> getAllegati() {
		if(this.allegati == null)
			this.allegati = new ArrayList<Allegato>();
		return allegati;
	}


	public void setAllegati(List<Allegato> allegati) {
		this.allegati = allegati;
	}


	public String getIdAvviso() {
		return idAvviso;
	}


	public void setIdAvviso(String idAvviso) {
		this.idAvviso = idAvviso;
	}

	
	public Date getDataAvviso() {
		return dataAvviso;
	}

	
	public void setDataAvviso(Date dataAvviso) {
		this.dataAvviso = dataAvviso;
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


	public Tendenza getTendenza() {
		return tendenza;
	}


	public void setTendenza(Tendenza tendenza) {
		this.tendenza = tendenza;
	}

	
	public Altezza getAltezzaStimataOnda() {
		return altezzaStimataOnda;
	}

	public void setAltezzaStimataOnda(Altezza altezzaStimataOnda) {
		this.altezzaStimataOnda = altezzaStimataOnda;
	}

	
	public List<EventoCostiero> getEventiCostieri() {
		if(this.eventiCostieri == null)
			this.eventiCostieri = new ArrayList<EventoCostiero>();
		return eventiCostieri;
	}

	public void setEventiCostieri(List<EventoCostiero> eventiCostieri) {
		this.eventiCostieri = eventiCostieri;
	}

	
	public Mareggiata getMareggiata() {
		return mareggiata;
	}

	public void setMareggiata(Mareggiata mareggiata) {
		this.mareggiata = mareggiata;
	}

	public Provenienza getDirezioneProvStimataOnda() {
		return direzioneProvStimataOnda;
	}

	public float getPianuraVento() {
		return pianuraVento;
	}

	public float getPianuraVentoMax() {
		return pianuraVentoMax;
	}


	public Provenienza getPianuraDirezioneVento() {
		return pianuraDirezioneVento;
	}

	public float getPedemontanaVento() {
		return pedemontanaVento;
	}

	public float getPedemontanaVentoMax() {
		return pedemontanaVentoMax;
	}

	public Provenienza getPedemontanaDirezioneVento() {
		return pedemontanaDirezioneVento;
	}

	public void setDirezioneProvStimataOnda(Provenienza direzioneProvStimataOnda) {
		this.direzioneProvStimataOnda = direzioneProvStimataOnda;
	}
	

	public void setPianuraVento(float pianuraVento) {
		this.pianuraVento = pianuraVento;
	}


	public void setPianuraVentoMax(float pianuraVentoMax) {
		this.pianuraVentoMax = pianuraVentoMax;
	}


	public void setPianuraDirezioneVento(Provenienza pianuraDirezioneVento) {
		this.pianuraDirezioneVento = pianuraDirezioneVento;
	}


	public void setPedemontanaVento(float pedemontanaVento) {
		this.pedemontanaVento = pedemontanaVento;
	}


	public void setPedemontanaVentoMax(float pedemontanaVentoMax) {
		this.pedemontanaVentoMax = pedemontanaVentoMax;
	}


	public void setPedemontanaDirezioneVento(Provenienza pedemontanaDirezioneVento) {
		this.pedemontanaDirezioneVento = pedemontanaDirezioneVento;
	}
	
	
	
	
}
