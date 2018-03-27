package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;


import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Polygon;

public class RelazioneSTBManagerITCase extends BaseTest{


	protected RelazioneSTBManager fixture;
	protected DannoSTBManager dannoSTBCRUD;

	protected RelazioneSTB relazione;

	Allegato allegato;

	private String stb = "STB fiumi romagnoli";
	private String fonte = "Rilievo GPS";
	
	@Before
	public void setup() throws Exception {

		allegato = new Allegato();
		allegato.setFile("test".getBytes());

		relazione = super.createRelazioneSTB();

		InitialContext ctx = new InitialContext();
		fixture = (RelazioneSTBManager) ctx.lookup("SupportoMareggiate-ear/RelazioneSTBManagerImpl/remote");
		dannoSTBCRUD = (DannoSTBManager) ctx.lookup("SupportoMareggiate-ear/DannoSTBManagerImpl/remote");
	}



	@Test
	public void testCrudRelazioneSTB() throws Exception {

		// Test create
		Long id = fixture.create(relazione, stb, fonte);
		assertTrue(id > 0);

		RelazioneSTB relazionePersisted = fixture.findItemById(id);
		assertNotNull(relazionePersisted);
		assertEquals("Bozza", relazionePersisted.getStato().getNome());

		DannoSTB dannoSTB = super.createDannoSTB();

		// Test insert DANNO stb
		Long idDannoSTB = fixture.insertDannoSTB(id, dannoSTB,"Ravenna","POLYGON ((30 10, 10 20, 20 40, 40 40, 30 10))");
		assertTrue(idDannoSTB > 0);
		
		Long idDannoSTB2 = fixture.insertDannoSTB(id, dannoSTB,"Ravenna","POLYGON ((30 40, 10 20, 20 40, 40 40, 30 40))");
		assertTrue(idDannoSTB2 > 0);
		
		Long idDannoSTB3 = fixture.insertDannoSTB(id, dannoSTB,"Ravenna","POLYGON ((30 40, 10 20, 20 40, 40 40, 30 40))");
		assertTrue(idDannoSTB3 > 0);

		List<Geometry> geometries = dannoSTBCRUD.getGeometry(idDannoSTB);
		assertTrue(geometries.get(0) instanceof Polygon);

		List<DannoSTB> danniSTB = fixture.getDanniSTB(id);

		assertTrue(danniSTB.size() == 3);
		assertEquals(idDannoSTB.longValue(),danniSTB.get(0).getId());
		assertEquals("Ravenna",danniSTB.get(0).getLocalita().getNome());




		// Test insertAllegato
		Long idAllegato = dannoSTBCRUD.insertAllegato(idDannoSTB, allegato,"Bollettino ARPA");
		assertTrue(idAllegato > 0);

		List<Allegato> allegati = dannoSTBCRUD.getAllegati(idDannoSTB);

		assertTrue(allegati.size() == 1);
		assertEquals(idAllegato.longValue(),allegati.get(0).getId());
		assertEquals("Bollettino ARPA",allegati.get(0).getTipo().getNome());

		// Test removeAllegato
		Long result = dannoSTBCRUD.removeAllegato(idDannoSTB, idAllegato);

		assertEquals(idAllegato.longValue(),result.longValue());

		allegati = dannoSTBCRUD.getAllegati(idDannoSTB);

		assertTrue(allegati.size() == 0);


		// Test remove DANNO stb
		result = fixture.removeDannoSTB(id, idDannoSTB);

		assertEquals(idDannoSTB.longValue(),result.longValue());

		danniSTB = fixture.getDanniSTB(id);

		assertTrue(danniSTB.size() == 2);







		// Test update stato
		/*Long idOperazione = fixture.updateStato(id, "Definitivo");
		assertEquals(id.longValue(),idOperazione.longValue());


		relazionePersisted = fixture.findItemById(id);
		assertNotNull(relazionePersisted);
		assertEquals("Definitivo", relazionePersisted.getStato().getNome());*/






		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id,removedId);



	}
}
