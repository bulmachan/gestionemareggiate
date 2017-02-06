package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.IndexColumn;

@Entity
@Table(name="MRG_RILEVAZIONI")
public class Rilevazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4717569454809355954L;
	
	@Id
	@GeneratedValue
	private long id;
	
	
	@ManyToOne
	@Basic(optional=false)
	private Stazione stazione;
	
	@ManyToOne
	@Basic(optional=false)
	private VariabileStazione variabileStazione;

	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable( name="MRG_RILEVAZIONE_DATI_SENSORE" )
	private List<DatoSensore> datiSensore;


	private double minValue;
	
	private double maxValue;
	
	
	public long getId() {
		return id;
	}


	public Stazione getStazione() {
		return stazione;
	}


	public VariabileStazione getVariabileStazione() {
		return variabileStazione;
	}


	public List<DatoSensore> getDatiSensore() {
		return datiSensore;
	}


	public void setId(long id) {
		this.id = id;
	}


	public void setStazione(Stazione stazione) {
		this.stazione = stazione;
	}


	public void setVariabileStazione(VariabileStazione variabileStazione) {
		this.variabileStazione = variabileStazione;
	}


	public void setDatiSensore(List<DatoSensore> datiSensore) {
		this.datiSensore = datiSensore;
	}


	public double getMinValue() {
		return minValue;
	}


	public double getMaxValue() {
		return maxValue;
	}


	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}


	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
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
		Rilevazione other = (Rilevazione) obj;
		if (id != other.id)
			return false;
		return true;
	}


	
	
	
}
