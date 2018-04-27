package it.epocaricerca.geologia.ejb.utils;

import java.math.BigInteger;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import it.epocaricerca.geologia.model.geometry.DannoLineString;
import it.epocaricerca.geologia.model.geometry.DannoMultiLineString;
import it.epocaricerca.geologia.model.geometry.DannoPoint;
import it.epocaricerca.geologia.model.geometry.DannoMultiPoint;
import it.epocaricerca.geologia.model.geometry.DannoPolygon;
import it.epocaricerca.geologia.model.geometry.DannoMultiPolygon;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.apache.log4j.Logger;

public class GeometryUtil {

	protected static String geomFromText = DatabasePropertiesUtil.getGeomFromText();
	protected static String textFromGeom = DatabasePropertiesUtil.getTextFromGeom();

	// AGGIUNTA MARUCCI
	protected static String geomTransform = DatabasePropertiesUtil.getGeomTransform();
	protected static String geomSrid3857 = DatabasePropertiesUtil.getGeomSrid3857();
	protected static String geomSrid32632 = DatabasePropertiesUtil.getGeomSrid32632();
	// FINE
	
	protected static String nextVal = DatabasePropertiesUtil.getNextVal();

	private static Logger logger = Logger.getLogger(GeometryUtil.class);

	
	public static Geometry getGeometry(Object entity)
	{
		if(entity instanceof DannoLineString)
		{
			DannoLineString temp = (DannoLineString) entity;

			return temp.getGeometry();

		}
		else if(entity instanceof DannoLineString)
		{
			DannoMultiLineString temp = (DannoMultiLineString) entity;
			return temp.getGeometry();
		}
		
		else if(entity instanceof DannoPoint)
		{
			DannoPoint temp = (DannoPoint) entity;
			return temp.getGeometry();
		}
		
		else if(entity instanceof DannoMultiPoint)
		{
			DannoMultiPoint temp = (DannoMultiPoint) entity;
			return temp.getGeometry();
		}
		
		else if(entity instanceof DannoPolygon)
		{
			DannoPolygon temp = (DannoPolygon) entity;
			return temp.getGeometry();
		}

		else if(entity instanceof DannoMultiPolygon)
		{
			DannoMultiPolygon temp = (DannoMultiPolygon) entity;
			return temp.getGeometry();
		}

		return null;
	}

	public static long persistGeometry(EntityManager em,Object entity, Geometry geometry)
	{

		Query q = em.createNativeQuery(nextVal);
		Number Objectid = null;

		if(entity instanceof DannoLineString)
		{
			DannoLineString temp = (DannoLineString) entity;
			temp.setGeometry((LineString) geometry);

			Objectid = (Number) q.getSingleResult();

			// MODIFICA MARUCCI: le coordinate arrivano in mercator e le trasformiamo in wsg84 32
			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";
			
			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_LIN (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();

		}

		else if(entity instanceof DannoMultiLineString)
		{
			DannoMultiLineString temp = (DannoMultiLineString) entity;
			temp.setGeometry((MultiLineString) geometry);

			Objectid = (Number) q.getSingleResult();

			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";
			
			//logger.info("ST: "+geomText);
			
			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_LIN (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();

		}
		
		else if(entity instanceof DannoPoint)
		{
			DannoPoint temp = (DannoPoint) entity;
			temp.setGeometry((Point) geometry);


			Objectid = (Number) q.getSingleResult();

			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";

			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_PUN (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();

		}

		else if(entity instanceof DannoMultiPoint)
		{
			DannoMultiPoint temp = (DannoMultiPoint) entity;
			temp.setGeometry((MultiPoint) geometry);


			Objectid = (Number) q.getSingleResult();

			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";

			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_PUN (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();

		}

		else if(entity instanceof DannoPolygon)
		{
			DannoPolygon temp = (DannoPolygon) entity;
			temp.setGeometry((Polygon) geometry);

			Objectid = (Number) q.getSingleResult();

			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";

			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_POL (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();
		}
		else if(entity instanceof DannoMultiPolygon)
		{
			DannoMultiPolygon temp = (DannoMultiPolygon) entity;
			temp.setGeometry((MultiPolygon) geometry);

			Objectid = (Number) q.getSingleResult();

			//String geomText = geomFromText+"('"+geometry.toText()+"', 0 )";
			String geomText = geomTransform+"("+geomFromText+"('"+geometry.toText()+"', "+geomSrid3857+" )"+","+geomSrid32632+")";

			int numUpdated = em.createNativeQuery(
					"INSERT INTO MRG_F_DANNI_POL (\"OBJECTID\", \"SHAPE\", \"GISID\") " +
							"VALUES ("+Objectid.longValue()+","+geomText+","+Objectid.longValue()+")" 
					).executeUpdate();

			if(numUpdated != 1)
				return -1L;

			return Objectid.longValue();
		}

		return -1L;


	}

	/*public static Object findItemById(Object entity, EntityManager em, long idGeometria) {

		WKTReader reader = new WKTReader();
		
		if(entity instanceof DannoLineString)
		{
			DannoLineString temp = (DannoLineString) entity;
			temp.setId(idGeometria);
			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_LIN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				temp.setGeometry((LineString) reader.read(wkt));
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}

			
			return entity;

		}
		else if(entity instanceof DannoMultiLineString)
		{
			DannoMultiLineString temp = (DannoMultiLineString) entity;
			temp.setId(idGeometria);
			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_LIN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				temp.setGeometry((MultiLineString) reader.read(wkt));
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}

			
			return entity;

		}
		
		
		else if(entity instanceof DannoPoint)
		{
			DannoPoint temp = (DannoPoint) entity;
			temp.setId(idGeometria);
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_PUN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				temp.setGeometry((Point) reader.read(wkt));
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}

			
			return entity;

		}
		
		else if(entity instanceof DannoMultiPoint)
		{
			DannoMultiPoint temp = (DannoMultiPoint) entity;
			temp.setId(idGeometria);
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_PUN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				temp.setGeometry((MultiPoint) reader.read(wkt));
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}

			
			return entity;

		}
		else if(entity instanceof DannoPolygon)
		{
			DannoPolygon temp = (DannoPolygon) entity;
			temp.setId(idGeometria);
			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_POL " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				temp.setGeometry((Polygon) reader.read(wkt));
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}
			return entity;
		}
		else if(entity instanceof DannoMultiPolygon)
		{
			DannoMultiPolygon temp = (DannoMultiPolygon) entity;
			temp.setId(idGeometria);
			
			String textFromGeomFRA="sde.st_astext";

			logger.info("SELECT "+textFromGeomFRA+"("+geomTransform+"(SHAPE,"+geomSrid3857+"))"+" FROM MRG_F_DANNI_POL " +
					"WHERE \"OBJECTID\" = "+ idGeometria);
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeomFRA+"("+geomTransform+"(SHAPE,"+geomSrid3857+"))"+" FROM MRG_F_DANNI_POL " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			logger.info(wkt);
			
			try {
			
				temp.setGeometry((MultiPolygon) reader.read(wkt));
				
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}
			return entity;
		}
		return null;
	}*/

	
	
	public static void remove(String geometryType, EntityManager em, long idGeometria) throws Exception {
		

		if(geometryType.equals("line"))
		{
			

			int numUpdated =  em.createNativeQuery(
					"DELETE FROM MRG_F_DANNI_LIN WHERE \"OBJECTID\" = "+ idGeometria
					).executeUpdate();		
			
			if(numUpdated != 1)
				throw new Exception("Failed to remove Geometry "+idGeometria);
			
			return;

		}

		else if(geometryType.equals("point"))
		{
			int numUpdated =  em.createNativeQuery(
					"DELETE FROM MRG_F_DANNI_PUN WHERE \"OBJECTID\" = "+ idGeometria
					).executeUpdate();		
			
			if(numUpdated != 1)
				throw new Exception("Failed to remove Geometry "+idGeometria);

			return;
		}

		else if(geometryType.equals("polygon"))
		{
			int numUpdated =  em.createNativeQuery(
					"DELETE FROM MRG_F_DANNI_POL WHERE \"OBJECTID\" = "+ idGeometria
					).executeUpdate();		
			
			if(numUpdated != 1)
				throw new Exception("Failed to remove Geometry "+idGeometria);
			return;
		}

		throw new Exception("Failed to remove Geometry "+idGeometria);
	}
	
	
	public static Geometry findItemById(String geometryType, EntityManager em, long idGeometria) {

		WKTReader reader = new WKTReader();
		
		if(geometryType.equals("line"))
		{

			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_LIN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				return reader.read(wkt);
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}

		}

		else if(geometryType.equals("point"))
		{
			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_PUN " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				return reader.read(wkt);
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}


		}
		else if(geometryType.equals("polygon"))
		{
			
			
			
			String wkt = (String) em.createNativeQuery(
					"SELECT "+textFromGeom+" FROM MRG_F_DANNI_POL " +
					"WHERE \"OBJECTID\" = "+ idGeometria
					).getSingleResult();		
			
			try {
				return  reader.read(wkt);
			} catch (ParseException e) {
				
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}
	
}
