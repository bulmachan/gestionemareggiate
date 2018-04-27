package it.epocaricerca.geologia.model.geometry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;



public class DannoPolygon implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 5651755705347876644L;
	
	
	private long id;
	
	
	private Polygon geometry;


	public Polygon getGeometry() {
		return geometry;
	}


	public void setGeometry(Polygon geometry) {
		this.geometry = geometry;
	}


	public long getId() {
		return id;
	}


	public void setId(long id) {
		this.id = id;
	}
	
	

}
