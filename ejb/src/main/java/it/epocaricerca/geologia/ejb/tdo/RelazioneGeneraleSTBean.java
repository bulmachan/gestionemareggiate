package it.epocaricerca.geologia.ejb.tdo;

import it.epocaricerca.geologia.model.RelazioneSTB;

import java.io.Serializable;
import java.util.Date;
import java.util.List;




public class RelazioneGeneraleSTBean implements Serializable{

	private Date data;
	
	private String informazioniGenerali;
	
	private String informazioniMeteo;
	
	
	private List<RelazioneSTB> relazioniSTB;
	

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
		return relazioniSTB;
	}

	public void setRelazioniSTB(List<RelazioneSTB> relazioniSTB) {
		this.relazioniSTB = relazioniSTB;
	}


}
