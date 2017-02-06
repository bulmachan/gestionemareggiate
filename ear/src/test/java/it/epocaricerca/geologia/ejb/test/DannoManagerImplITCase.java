package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;


public class DannoManagerImplITCase extends BaseTest  {

	DannoManager fixture;

	Allegato allegato;

	@Before
	public void setup() throws Exception {


		allegato = new Allegato();
		allegato.setFile("test".getBytes());

		InitialContext ctx = new InitialContext();
		fixture = (DannoManager) ctx.lookup("SupportoMareggiate-ear/DannoManagerImpl/remote");
	}


	@Test
	public void testCrudAllegato() throws Exception {

		// Test create
		Long id = fixture.create(new Danno(),"Goro","Erosione", "POINT(10 15)");
		assertTrue(id > 0);

		Danno danno = fixture.findItemById(id);
		assertNotNull(danno);
		assertEquals("Goro", danno.getLocalita().getNome());
		assertEquals("Erosione", danno.getTipoDanno().getNome());

		Point point = (Point)fixture.getGeometry(id);

		assertEquals("POINT (10 15)",point.toText());


		// Test insertAllegato
		Long idAllegato = fixture.insertAllegato(id, allegato,"Bollettino ARPA");
		assertTrue(idAllegato > 0);

		List<Allegato> allegati = fixture.getAllegati(id);

		assertTrue(allegati.size() == 1);
		assertEquals(idAllegato.longValue(),allegati.get(0).getId());
		assertEquals("Bollettino ARPA",allegati.get(0).getTipo().getNome());

		// Test removeAllegato
		Long result = fixture.removeAllegato(id, idAllegato);

		assertEquals(idAllegato.longValue(),result.longValue());

		allegati = fixture.getAllegati(id);

		assertTrue(allegati.size() == 0);



		// Test addAllegato
		idAllegato = fixture.insertAllegato(id, allegato,"Bollettino ARPA");
		assertTrue(idAllegato > 0);

		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}/**/


	}


	@Test
	public void testCrudDanno() throws Exception {

		// Test create
		Long id = fixture.create(new Danno(),"Goro","Erosione","LINESTRING (30 10, 10 30, 40 40)");
		assertTrue(id > 0);

		Danno danno = fixture.findItemById(id);
		assertNotNull(danno);


		List<Geometry>  geometries = fixture.getGeometry(id);

		assertTrue(geometries.get(0) instanceof LineString);
		assertEquals("LINESTRING (30 10, 10 30, 40 40)",geometries.get(0).toText());


		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}

}
