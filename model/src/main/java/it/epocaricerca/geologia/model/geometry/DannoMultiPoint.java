package it.epocaricerca.geologia.model.geometry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.MultiPoint;

public class DannoMultiPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9210744373183008898L;
	
	private long id;
	
	
	private MultiPoint geometry;


	public MultiPoint getGeometry() {
		return geometry;
	}


	public void setGeometry(MultiPoint geometry) {
		this.geometry = geometry;
	}


	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

}
