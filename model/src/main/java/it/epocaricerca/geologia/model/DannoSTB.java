package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
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
import javax.persistence.OrderBy;
import javax.persistence.Table;

@Entity
@Table(name="MRG_DANNI_STB")
public class DannoSTB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9166539727062175850L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne(optional=false)
	private Localita localita;
	
	@OneToMany(cascade={CascadeType.ALL})
	@JoinTable( name="MRG_DANNI_STB_ALLEGATI" )
	private List<Allegato> allegati;
	
	private float volumiErosi;
	
	private float lunghezzaTratto;
	
	private float stimaCostiRipascimenti;
	
	private float stimaCostiDanni;
	
	@Lob
	private String descrizione;
	
	@Basic(optional=false)
	private boolean erosioni;
	
	@Basic(optional=false)
	private boolean tracimazioni;
	
	@Basic(optional=false)
	private boolean inondazioni;
	
	@Basic(optional=false)
	private boolean danniDifese;
	
	@Basic(optional=false)
	private boolean danniInfrastrutture;
	
	@Basic(optional=true)
	private Long idGeometriaPoint;
	
	@Basic(optional=true)
	private Long idGeometriaLine;
	
	@Basic(optional=true)
	private Long idGeometriaPolygon;
	
	public Localita getLocalita() {
		return localita;
	}

	public void setLocalita(Localita localita) {
		this.localita = localita;
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

	public long getId() {
		return id;
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
