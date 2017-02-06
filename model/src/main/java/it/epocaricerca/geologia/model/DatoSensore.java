package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_DATI_SENSORI")
public class DatoSensore implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -891218690600522805L;
	
	
	@Id
	@GeneratedValue
	private long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date timestamp;

	@Basic(optional=false)
	private double value;

	public long getId() {
		return id;
	}

	
	public double getValue() {
		return value;
	}

	public void setId(long id) {
		this.id = id;
	}


	public void setValue(double value) {
		this.value = value;
	}


	public Date getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
}
