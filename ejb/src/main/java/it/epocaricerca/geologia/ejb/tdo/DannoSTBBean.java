package it.epocaricerca.geologia.ejb.tdo;

import it.epocaricerca.geologia.model.Localita;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Lob;

public class DannoSTBBean implements Serializable{
	
	private Long id;
	private Localita localita;
	private float volumiErosi;
	private float lunghezzaTratto;
	private float stimaCostiRipascimenti;
	private float stimaCostiDanni;
	
	private boolean erosioni;
	private boolean tracimazioni;
	private boolean inondazioni;
	private boolean danniDifese;
	private boolean danniInfrastrutture;
	
	private String descrizione;
	private String geometryText;
	private List<FileAllegatoBean> fileAllegati;
	
	public DannoSTBBean() {
		super();
		this.fileAllegati = new ArrayList<FileAllegatoBean>();
	}
	
	public DannoSTBBean(Localita localita, float volumiErosi, float lunghezzaTratto, float stimaCostiRipascimenti, float stimaCostiDanni, 
	String descrizione, boolean erosioni, boolean tracimazioni, boolean inondazioni, boolean danniDifese, boolean danniInfrastrutture,
			List<FileAllegatoBean> fileAllegati) {
		super();
		this.localita = localita;
		this.volumiErosi = volumiErosi;
		this.lunghezzaTratto = lunghezzaTratto;
		this.stimaCostiRipascimenti = stimaCostiRipascimenti;
		this.stimaCostiDanni = stimaCostiDanni;		
		this.descrizione = descrizione;
		this.fileAllegati = fileAllegati;
		this.erosioni = erosioni;
		this.tracimazioni = tracimazioni;
		this.inondazioni = inondazioni;
		this.danniDifese = danniDifese;
		this.danniInfrastrutture = danniInfrastrutture;
	

	}
	
	public Localita getLocalita() {
		return localita;
	}
	public void setLocalita(Localita localita) {
		this.localita = localita;
	}
	public List<FileAllegatoBean> getFileAllegati() {
		return fileAllegati;
	}
	public void setFileAllegati(List<FileAllegatoBean> fileAllegati) {
		this.fileAllegati = fileAllegati;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public float getVolumiErosi() {
		return volumiErosi;
	}

	public void setVolumiErosi(float volumiErosi) {
		this.volumiErosi = volumiErosi;
	}

	public float getLunghezzaTratto() {
		return lunghezzaTratto;
	}

	public void setLunghezzaTratto(float lunghezzaTratto) {
		this.lunghezzaTratto = lunghezzaTratto;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getGeometryText() {
		return geometryText;
	}

	public void setGeometryText(String geometryText) {
		this.geometryText = geometryText;
	}

	public boolean isErosioni() {
		return erosioni;
	}

	public void setErosioni(boolean erosioni) {
		this.erosioni = erosioni;
	}
	
	public boolean isTracimazioni() {
		return tracimazioni;
	}

	public void setTracimazioni(boolean tracimazioni) {
		this.tracimazioni = tracimazioni;
	}
	
	public boolean isInondazioni() {
		return inondazioni;
	}

	public void setInondazioni(boolean inondazioni) {
		this.inondazioni = inondazioni;
	}
	
	public boolean isDanniDifese() {
		return danniDifese;
	}

	public void setDanniDifese(boolean danniDifese) {
		this.danniDifese = danniDifese;
	}
	
	public boolean isDanniInfrastrutture() {
		return danniInfrastrutture;
	}

	public void setDanniInfrastrutture(boolean danniInfrastrutture) {
		this.danniInfrastrutture = danniInfrastrutture;
	}
	
	public float getStimaCostiRipascimenti() {
		return stimaCostiRipascimenti;
	}

	public void setStimaCostiRipascimenti(float stimaCostiRipascimenti) {
		this.stimaCostiRipascimenti = stimaCostiRipascimenti;
	}
	
	public float getStimaCostiDanni() {
		return stimaCostiDanni;
	}

	public void setStimaCostiDanni(float stimaCostiDanni) {
		this.stimaCostiDanni = stimaCostiDanni;
	}
	
	
}
