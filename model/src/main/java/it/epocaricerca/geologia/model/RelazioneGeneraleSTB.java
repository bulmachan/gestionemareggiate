package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.JoinTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_RELAZIONI_GENERALI_STB")
public class RelazioneGeneraleSTB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6085844289397827742L;
	
	@Id
	@GeneratedValue
	private long id;
	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date data;
	
	@Lob
	private String informazioniGenerali;
	
	@Lob
	private String informazioniMeteo;
	
	@ManyToOne(optional=false)
	private StatoRelazione stato;
	
	
	@OneToMany(mappedBy="relazioneGeneraleSTB")
	private List<RelazioneSTB> relazioniSTB;
	
	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.EAGER)
	@JoinTable( name="MRG_RELAZIONIGENSTB_ALLEGATI" )
	private Set<Allegato> allegati;
	
	

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getInformazioniGenerali() {
		return informazioniGenerali;
	}

	public void setInformazioniGenerali(String informazioniGenerali) {
		this.informazioniGenerali = informazioniGenerali;
	}

	public String getInformazioniMeteo() {
		return informazioniMeteo;
	}

	public void setInformazioniMeteo(String informazioniMeteo) {
		this.informazioniMeteo = informazioniMeteo;
	}

	public List<RelazioneSTB> getRelazioniSTB() {
		if(this.relazioniSTB == null)
			this.relazioniSTB = new ArrayList<RelazioneSTB>();
		return relazioniSTB;
	}

	public void setRelazioniSTB(List<RelazioneSTB> relazioniSTB) {
		this.relazioniSTB = relazioniSTB;
	}

	public long getId() {
		return id;
	}

	public StatoRelazione getStato() {
		return stato;
	}

	public void setStato(StatoRelazione stato) {
		this.stato = stato;
	}

	public Set<Allegato> getAllegati() {
		if(this.allegati == null)
			this.allegati = new HashSet<Allegato>();
		return allegati;
	}

	public void setAllegati(Set<Allegato> allegati) {
		this.allegati = allegati;
	}

}
