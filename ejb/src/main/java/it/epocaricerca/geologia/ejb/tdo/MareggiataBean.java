package it.epocaricerca.geologia.ejb.tdo;

import java.io.Serializable;
import java.util.Date;


public class MareggiataBean implements Serializable {

	
	
	private String codice;
	
	private Date inizioValidita;
	
	private Date fineValidita;
	
	
	private String descrizione;
	
	/*private List<Allegato> allegati;
	
	private List<PrevisioneMeteo> previsioniMeteo;
	
	private List<PrevisioneImpatto> previsioniImpatti;
	
	private List<ImpattoReale> impattiReali;
	
	private List<CondizioneMeteo> condizioniMeteo;
	private List<RelazioneSTB> relazioniSTB;*/
	
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

	
	

}
