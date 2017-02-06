package it.epocaricerca.geologia.model.geometry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;


public class DannoMultiLineString implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2507240089724336594L;
	
	
	private long id;
	
	
	private MultiLineString geometry;
	
	public MultiLineString getGeometry() {
		return geometry;
	}


	public void setGeometry(MultiLineString geometry) {
		this.geometry = geometry;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}
	
	


}
