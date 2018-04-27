package it.epocaricerca.geologia.ejb.tdo;

import it.epocaricerca.geologia.model.Localita;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class TipologiaDannoBean implements Serializable{
	
	private Long id;
	private String tipoDanno;
	private Localita localita;
	private String geometryText;
	private List<FileAllegatoBean> fileAllegati;
	
	public TipologiaDannoBean() {
		super();
		this.fileAllegati = new ArrayList<FileAllegatoBean>();
	}
	
	public TipologiaDannoBean(String tipoDanno, Localita localita,
			List<FileAllegatoBean> fileAllegati) {
		super();
		this.tipoDanno = tipoDanno;
		this.localita = localita;
		this.fileAllegati = fileAllegati;
	}
	
	public String getTipoDanno() {
		return tipoDanno;
	}
	public void setTipoDanno(String tipoDanno) {
		this.tipoDanno = tipoDanno;
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

	public String getGeometryText() {
		return geometryText;
	}

	public void setGeometryText(String geometryText) {
		this.geometryText = geometryText;
	}

}
