package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name="MRG_IMPATTI")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="IMPATTO_TYPE", discriminatorType=DiscriminatorType.STRING, length=11)
public abstract class Impatto implements Serializable {

	
	private static final long serialVersionUID = -4808413520686759494L;

	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String codice;
	
	@Basic(optional=false)
	private String numeroAllerta;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date dataAllerta;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date inizioValidita;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date fineValidita;
	
	@Lob
	private String descrizione;
	
	
	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	
	public String getNumeroAllerta() {
		return numeroAllerta;
	}

	public void setNumeroAllerta(String numeroAllerta) {
		this.numeroAllerta = numeroAllerta;
	}
	
	public Date getInizioValidita() {
		return dataAllerta;
	}

	public void setDataAllerta(Date dataAllerta) {
		this.dataAllerta = dataAllerta;
	}

	public Date getDataAllerta() {
		return dataAllerta;
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

	

}
