package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_EVENTI_COSTIERI")
public class EventoCostiero implements Serializable {

	
	private static final long serialVersionUID = -4227229705064950068L;

	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	private PrevisioneMeteo previsione;
	
	@ManyToOne(optional=false)
	private MacroArea macroArea;
	
	@ManyToOne(optional=false)
	private Fenomeno fenomeno;
	
	private float altezzaOnda;
	
	private float livelloMare;
	
	private Provenienza direzione;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date inizio;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date fine;
	
	@Lob
	private String note;
	
	
	/** Getter and Setter **/

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public PrevisioneMeteo getPrevisione() {
		return previsione;
	}

	public void setPrevisione(PrevisioneMeteo previsione) {
		this.previsione = previsione;
	}

	public MacroArea getMacroArea() {
		return macroArea;
	}

	public void setMacroArea(MacroArea macroArea) {
		this.macroArea = macroArea;
	}
	
	public Fenomeno getFenomeno() {
		return fenomeno;
	}

	public void setFenomeno(Fenomeno fenomeno) {
		this.fenomeno = fenomeno;
	}

	public float getAltezzaOnda() {
		return altezzaOnda;
	}

	public void setAltezzaOnda(float altezzaOnda) {
		this.altezzaOnda = altezzaOnda;
	}

	public float getLivelloMare() {
		return livelloMare;
	}

	public void setLivelloMare(float livelloMare) {
		this.livelloMare = livelloMare;
	}

	public Provenienza getDirezione() {
		return direzione;
	}

	public void setDirezione(Provenienza direzione) {
		this.direzione = direzione;
	}

	public Date getInizio() {
		return inizio;
	}

	public void setInizio(Date inizio) {
		this.inizio = inizio;
	}

	public Date getFine() {
		return fine;
	}

	public void setFine(Date fine) {
		this.fine = fine;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	
	
	
}
