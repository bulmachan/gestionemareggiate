package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;


import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class RelazioneGeneraleSTBManagerImplITCase extends BaseTest {

	protected RelazioneSTBManager relazioneSTBManager;

	protected RelazioneGeneraleSTBManager fixture;

	protected RelazioneSTB relazione;

	private String stb = "STB fiumi romagnoli";

	@Before
	public void setup() throws Exception {


		relazione = super.createRelazioneSTB();

		InitialContext ctx = new InitialContext();
		relazioneSTBManager = (RelazioneSTBManager) ctx.lookup("SupportoMareggiate-ear/RelazioneSTBManagerImpl/remote");
		fixture = (RelazioneGeneraleSTBManager) ctx.lookup("SupportoMareggiate-ear/RelazioneGeneraleSTBManagerImpl/remote");
	}



	@Test
	public void testCrudRelazioneSTB() throws Exception {

		// Test create
		Long id = fixture.create(super.createRelazioneGeneraleSTB());
		assertTrue(id > 0);

		RelazioneGeneraleSTB relazionePersisted = fixture.findItemById(id);
		assertNotNull(relazionePersisted);
		assertEquals("Bozza", relazionePersisted.getStato().getNome());

		DannoSTB dannoSTB = super.createDannoSTB();

		// Test insert Relazione STB

		// Test create
		Long idRelazioneSTB = relazioneSTBManager.create(super.createRelazioneSTB(), "STB fiumi romagnoli", "Rilievo GPS");
		assertTrue(idRelazioneSTB > 0);



		try {
			fixture.addRelazioneSTB(-100L, -10L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long result = fixture.addRelazioneSTB(id, idRelazioneSTB);
		assertEquals(idRelazioneSTB,result);
		
		List<RelazioneSTB> relazioniSTB = fixture.getRelazioniSTB(id);
		assertEquals(1, relazioniSTB.size());
		assertEquals(result.longValue(), relazioniSTB.get(0).getId());
		
		
		Long removedId = fixture.removeRelazioneSTB(id, idRelazioneSTB);
		assertEquals(idRelazioneSTB,removedId);
		
		
		relazioniSTB = fixture.getRelazioniSTB(id);
		assertEquals(0, relazioniSTB.size());
		
		RelazioneSTB relazioneTemp = relazioneSTBManager.findItemById(idRelazioneSTB);
		assertNotNull(relazioneTemp);
		

		// Test update stato
		Long idOperazione = fixture.updateStato(id, "Definitivo");
		assertEquals(id.longValue(),idOperazione.longValue());


		relazionePersisted = fixture.findItemById(id);
		assertNotNull(relazionePersisted);
		assertEquals("Definitivo", relazionePersisted.getStato().getNome());


		// Test add su relazione chiusa
		result = fixture.addRelazioneSTB(id, idRelazioneSTB);
		assertEquals(-1L,result.longValue());
		
		//relazioneSTBManager.remove(idRelazioneSTB);
		
		



	}
}
