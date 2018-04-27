package it.epocaricerca.geologia.model.geometry;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import com.vividsolutions.jts.geom.Point;

public class DannoPoint implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9210744373183008898L;
	
	private long id;
	
	
	private Point geometry;


	public Point getGeometry() {
		return geometry;
	}


	public void setGeometry(Point geometry) {
		this.geometry = geometry;
	}


	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

}
