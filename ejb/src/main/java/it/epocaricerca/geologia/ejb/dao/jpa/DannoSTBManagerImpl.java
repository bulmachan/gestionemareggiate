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
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.LocalitaManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.utils.GeometryUtil;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.TipoAllegato;

@Stateless
@Remote
public class DannoSTBManagerImpl extends GenericManager implements DannoSTBManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private LocalitaManager localitaManager;


	@EJB
	private TipoAllegatoManager tipoAllegatoCRUD;
	
	private final String separator = ";";


	public Long create(DannoSTB danno, String localita,String geometriesTextEncoded) throws EntityNotFoundException, ParseException,Exception {

	
		Localita localitaObj = localitaManager.findItemByName(localita);
		if(localitaObj == null)
			throw new EntityNotFoundException("localitaObj "+localita);


		Long idGeometriaPoint = null;

		Long idGeometriaLine =  null;

		Long idGeometriaPolygon =  null;

		if (geometriesTextEncoded != null && !geometriesTextEncoded.equals("")) {
	
			String geometriesText[] = geometriesTextEncoded.split(separator);
			
			for (String geometryText : geometriesText) {

				WKTReader fromTextReader = new WKTReader();

				Geometry geometry = fromTextReader.read(geometryText);


				String geometryType = geometry.getGeometryType();

				if(!geometryType.equals("Point") && !geometryType.equals("Polygon") && !geometryType.equals("LineString") && !geometryType.equalsIgnoreCase("MultiLineString") && !geometryType.equalsIgnoreCase("MultiPolygon")  && !geometryType.equalsIgnoreCase("MultiPoint") )
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
		}

		danno.setLocalita(localitaObj);
		danno.setIdGeometriaLine(idGeometriaLine);
		danno.setIdGeometriaPoint(idGeometriaPoint);
		danno.setIdGeometriaPolygon(idGeometriaPolygon);

		em.persist(danno);

		return danno.getId();
	}

	public Long remove(long idDanno) throws EntityNotFoundException, Exception {

		System.out.println("Remove Danno STBManager "+idDanno);
		DannoSTB danno = this.findItemById(idDanno);
		if(danno == null)
			throw new EntityNotFoundException("DannoSTB "+idDanno);


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

	public DannoSTB findItemById(long id) {
		return super.findItemById(DannoSTB.class,em, id);
	}

	/** Gestione allegato **/

	public Long insertAllegato(long idDannoSTB, Allegato allegato,String tipoAllegato) throws EntityNotFoundException {
		DannoSTB danno = super.findItemById(DannoSTB.class, em, idDannoSTB);
		TipoAllegato tipoAllegatoObj = tipoAllegatoCRUD.findItemByName(tipoAllegato);

		if(danno == null || tipoAllegatoObj == null)
			throw new EntityNotFoundException("danno == null || tipoAllegatoObj == null");

		allegato.setTipo(tipoAllegatoObj);
		em.persist(allegato);
		danno.getAllegati().add(allegato);
		em.merge(danno);

		return allegato.getId();
	}

	public List<Allegato> getAllegati(long idDannoSTB) throws EntityNotFoundException {
		if(super.findItemById(DannoSTB.class, em, idDannoSTB) == null)
			throw new EntityNotFoundException("DannoSTB "+idDannoSTB);

		Query q = em.createQuery("SELECT pm.allegati FROM DannoSTB pm WHERE pm.id = ?1");
		q.setParameter(1, idDannoSTB);
		return q.getResultList();
	}

	public Long removeAllegato(long idDannoSTB, long idAllegato) throws EntityNotFoundException {
		DannoSTB danno = super.findItemById(DannoSTB.class, em, idDannoSTB);
		if(danno == null)
			throw new EntityNotFoundException("DannoSTB "+idDannoSTB);

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
		DannoSTB danno = this.findItemById(idDanno);
		if(danno == null)
			throw new EntityNotFoundException("DannoSTB "+idDanno);

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
