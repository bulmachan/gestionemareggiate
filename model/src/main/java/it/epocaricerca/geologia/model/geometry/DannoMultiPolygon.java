package it.epocaricerca.geologia.model.geometry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.MultiPolygon;



public class DannoMultiPolygon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5651755705347876644L;
	
	
	private long id;
	
	
	private MultiPolygon geometry;


	public MultiPolygon getGeometry() {
		return geometry;
	}


	public void setGeometry(MultiPolygon geometry) {
		this.geometry = geometry;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}
	
	

}
