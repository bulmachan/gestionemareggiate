package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.LocalitaManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.TipoDannoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.utils.GeometryUtil;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.model.TipoDanno;
import it.epocaricerca.geologia.model.geometry.DannoLineString;
import it.epocaricerca.geologia.model.geometry.DannoPoint;
import it.epocaricerca.geologia.model.geometry.DannoPolygon;


@Stateless
@Remote
public class DannoManagerImpl extends GenericManager implements DannoManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private LocalitaManager localitaManager;

	@EJB
	private TipoDannoManager tipoDannoManagaer;

	@EJB
	private TipoAllegatoManager tipoAllegatoCRUD;

	private final String separator = ";";

	public Long create(Danno danno, String nomeLocalita, String tipoDanno,String geometriesTextEncoded) throws Exception {

		String geometriesText[] = geometriesTextEncoded.split(separator);

		Long idGeometriaPoint = null;

		Long idGeometriaLine =  null;

		Long idGeometriaPolygon =  null;

		for (String geometryText : geometriesText) {
			WKTReader fromTextReader = new WKTReader();

			Geometry geometry = fromTextReader.read(geometryText);

			String geometryType = geometry.getGeometryType();

			if(!geometryType.equalsIgnoreCase("Point") && !geometryType.equalsIgnoreCase("Polygon") && !geometryType.equalsIgnoreCase("LineString") && !geometryType.equalsIgnoreCase("MultiLineString") && !geometryType.equalsIgnoreCase("MultiPolygon")  && !geometryType.equalsIgnoreCase("MultiPoint"))
			{
				throw new ParseException("Geometry has to be of Point, Polygon or Linestring (or Multi)");
			}

			String entityName = "it.epocaricerca.geologia.model.geometry.Danno"+geometryType;
			Object entity = null;
			try {
				entity = Class.forName(entityName).newInstance();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception(e);
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception(e);

			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new Exception(e);
			}






			long idGeometry =  GeometryUtil.persistGeometry(em,entity,geometry);

			if (geometryType.equalsIgnoreCase("Point") || geometryType.equalsIgnoreCase("MultiPoint"))
				idGeometriaPoint = idGeometry;
			else if (geometryType.equalsIgnoreCase("LineString") || geometryType.equalsIgnoreCase("MultiLineString"))
				idGeometriaLine = idGeometry;

			else if (geometryType.equalsIgnoreCase("Polygon") || geometryType.equalsIgnoreCase("MultiPolygon"))
				idGeometriaPolygon = idGeometry;
		}

		Localita localitaObj = localitaManager.findItemByName(nomeLocalita);
		TipoDanno tipoDannoObj = tipoDannoManagaer.findItemByName(tipoDanno);

		if(localitaObj == null || tipoDannoObj == null)
			throw new EntityNotFoundException("localitaObj == null || tipoDannoObj == null" +" --- nomeLocalita"+nomeLocalita+" --- tipoDanno"+ tipoDanno);


		danno.setLocalita(localitaObj);
		danno.setTipoDanno(tipoDannoObj);
		danno.setIdGeometriaLine(idGeometriaLine);
		danno.setIdGeometriaPoint(idGeometriaPoint);
		danno.setIdGeometriaPolygon(idGeometriaPolygon);
		em.persist(danno);

		return danno.getId();
	}


	public Danno findItemById(long id) {
		return super.findItemById(Danno.class,em, id);
	}

	public Long remove(long idDanno) throws Exception {
		Danno danno = this.findItemById(idDanno);
		if(danno == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		Long idGeometriaLine = danno.getIdGeometriaLine();
		Long idGeometriaPoint = danno.getIdGeometriaPoint();
		Long idGeometriaPolygon = danno.getIdGeometriaPolygon();


		try {
			if(idGeometriaLine != null)
				GeometryUtil.remove("line", em, idGeometriaLine);

			if(idGeometriaPoint != null)
				GeometryUtil.remove("point", em, idGeometriaPoint);

			if(idGeometriaPolygon != null)
				GeometryUtil.remove("polygon", em, idGeometriaPolygon);


		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new Exception(e);
		}

		List<Allegato> allegati = danno.getAllegati();
		for (Iterator iterator = allegati.iterator(); iterator.hasNext();) {
			Allegato allegato = (Allegato) iterator.next();
			em.remove(allegato);

		}
		allegati.clear();

		em.remove(danno);

		return danno.getId();
	}


	/** Gestione allegato **/

	public Long insertAllegato(long idDanno, Allegato allegato,String tipoAllegato) throws EntityNotFoundException {
		Danno danno = this.findItemById(idDanno);
		TipoAllegato tipoAllegatoObj = tipoAllegatoCRUD.findItemByName(tipoAllegato);

		if(danno == null || tipoAllegatoObj == null)
			throw new EntityNotFoundException("danno == null || tipoAllegatoObj == null");

		allegato.setTipo(tipoAllegatoObj);
		em.persist(allegato);
		danno.getAllegati().add(allegato);
		em.merge(danno);

		return allegato.getId();
	}

	public List<Allegato> getAllegati(long idDanno) throws EntityNotFoundException {
		if(this.findItemById(idDanno) == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		Query q = em.createQuery("SELECT pm.allegati FROM Danno pm WHERE pm.id = ?1");
		q.setParameter(1, idDanno);
		return q.getResultList();
	}

	public Long removeAllegato(long idDanno, long idAllegato) throws EntityNotFoundException {
		Danno danno = this.findItemById(idDanno);
		if(danno == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);

		boolean removed = danno.getAllegati().remove(allegato);

		if(removed)
		{
			em.merge(danno);
			em.remove(allegato);
			return allegato.getId();
		}

		return -1L;
	}



	/** Gestione geometria  **/

	public List<Geometry> getGeometry(long idDanno) throws Exception {
		Danno danno = this.findItemById(idDanno);
		if(danno == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		Long idGeometriaLine = danno.getIdGeometriaLine();
		Long idGeometriaPoint = danno.getIdGeometriaPoint();
		Long idGeometriaPolygon = danno.getIdGeometriaPolygon();

		List<Geometry> geometries = new ArrayList<Geometry>();


		if(idGeometriaLine != null)
		{
			Geometry geometry = GeometryUtil.findItemById("line", em, idGeometriaLine);
			geometries.add(geometry);
		}

		if(idGeometriaPoint != null)
		{
			Geometry geometry = GeometryUtil.findItemById("point", em, idGeometriaPoint);
			geometries.add(geometry);
		}

		if(idGeometriaPolygon != null)
		{
			Geometry geometry = GeometryUtil.findItemById("polygon", em, idGeometriaPolygon);
			geometries.add(geometry);
		}


		return geometries;

	}


}
