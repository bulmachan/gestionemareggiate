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
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_MAREGGIATE")
public class Mareggiata implements Serializable{

	
	private static final long serialVersionUID = 373873929228314814L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String codice;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date inizioValidita;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date fineValidita;
	
	@Lob
	private String descrizione;
	
	@OneToMany(cascade={CascadeType.ALL})
	private List<Allegato> allegati;
	
	@OneToMany(mappedBy="mareggiata")
	private List<PrevisioneMeteo> previsioniMeteo;
	
	@OneToMany(mappedBy="mareggiataPrevisioneImpatto")
	private List<PrevisioneImpatto> previsioniImpatti;
	
	@OneToMany(mappedBy="mareggiataImpattoReale")
	private List<ImpattoReale> impattiReali;
	
	@OneToMany(mappedBy="mareggiata")
	private List<CondizioneMeteo> condizioniMeteo;
	
	@ManyToMany
	@JoinTable(name="MRG_MAREGGIATA_RELAZIONE_STB")
	private List<RelazioneSTB> relazioniSTB;
	
	/** Getter and Setter **/

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
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

	public List<PrevisioneMeteo> getPrevisioniMeteo() {
		if(this.previsioniMeteo == null)
			this.previsioniMeteo = new ArrayList<PrevisioneMeteo>();
		return previsioniMeteo;
	}

	public void setPrevisioniMeteo(List<PrevisioneMeteo> previsioniMeteo) {
		this.previsioniMeteo = previsioniMeteo;
	}

	public List<PrevisioneImpatto> getPrevisioniImpatti() {
		if(this.previsioniImpatti == null)
			this.previsioniImpatti = new ArrayList<PrevisioneImpatto>();
		return previsioniImpatti;
	}

	public void setPrevisioniImpatti(List<PrevisioneImpatto> previsioniImpatti) {
		this.previsioniImpatti = previsioniImpatti;
	}

	public List<ImpattoReale> getImpattiReali() {
		if(this.impattiReali == null)
			this.impattiReali = new ArrayList<ImpattoReale>();
		return impattiReali;
	}

	public void setImpattiReali(List<ImpattoReale> impattiReali) {
		this.impattiReali = impattiReali;
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

	public List<CondizioneMeteo> getCondizioniMeteo() {
		if(this.condizioniMeteo == null)
			this.condizioniMeteo = new ArrayList<CondizioneMeteo>();
		return condizioniMeteo;
	}

	public void setCondizioniMeteo(List<CondizioneMeteo> condizioniMeteo) {
		this.condizioniMeteo = condizioniMeteo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codice == null) ? 0 : codice.hashCode());
		result = prime * result
				+ ((descrizione == null) ? 0 : descrizione.hashCode());
		result = prime * result
				+ ((fineValidita == null) ? 0 : fineValidita.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((inizioValidita == null) ? 0 : inizioValidita.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Mareggiata other = (Mareggiata) obj;
		if (codice == null) {
			if (other.codice != null)
				return false;
		} else if (!codice.equals(other.codice))
			return false;
		if (descrizione == null) {
			if (other.descrizione != null)
				return false;
		} else if (!descrizione.equals(other.descrizione))
			return false;
		if (fineValidita == null) {
			if (other.fineValidita != null)
				return false;
		} else if (!fineValidita.equals(other.fineValidita))
			return false;
		if (id != other.id)
			return false;
		if (inizioValidita == null) {
			if (other.inizioValidita != null)
				return false;
		} else if (!inizioValidita.equals(other.inizioValidita))
			return false;
		return true;
	}
	
	
	

}
