package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;


@Entity
@Table(name="MRG_DANNI")
public class Danno implements Serializable{

	private static final long serialVersionUID = -5352383710580120554L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne(optional=false)
	private TipoDanno tipoDanno;
	
	@ManyToOne(optional=false)
	private Localita localita;
	
	@OneToMany(cascade={CascadeType.ALL})
	private List<Allegato> allegati;
	
	@Basic(optional=true)
	private Long idGeometriaPoint;
	
	@Basic(optional=true)
	private Long idGeometriaLine;
	
	@Basic(optional=true)
	private Long idGeometriaPolygon;
	

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public TipoDanno getTipoDanno() {
		return tipoDanno;
	}

	public void setTipoDanno(TipoDanno tipoDanno) {
		this.tipoDanno = tipoDanno;
	}

	public Localita getLocalita() {
		return localita;
	}

	public void setLocalita(Localita localita) {
		this.localita = localita;
	}

	public List<Allegato> getAllegati() {
		if(this.allegati == null)
			this.allegati = new ArrayList<Allegato>();
		return allegati;
	}

	public void setAllegati(List<Allegato> allegati) {
		this.allegati = allegati;
	}

	public Long getIdGeometriaPoint() {
		return idGeometriaPoint;
	}

	public void setIdGeometriaPoint(Long idGeometriaPoint) {
		this.idGeometriaPoint = idGeometriaPoint;
	}

	public Long getIdGeometriaLine() {
		return idGeometriaLine;
	}

	public void setIdGeometriaLine(Long idGeometriaLine) {
		this.idGeometriaLine = idGeometriaLine;
	}

	public Long getIdGeometriaPolygon() {
		return idGeometriaPolygon;
	}

	public void setIdGeometriaPolygon(Long idGeometriaPolygon) {
		this.idGeometriaPolygon = idGeometriaPolygon;
	}


}
