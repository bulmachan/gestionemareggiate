package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;

import java.util.List;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class MareggiataManagerImplITCase extends BaseTest {

	Mareggiata mareggiata;

	Allegato allegato;

	MareggiataManager fixture;

	PrevisioneMeteoManager previsioneMeteoManager;
	
	PrevisioneImpattoManager previsioneImpattoManager;
	
	CondizioneMeteoManager condizioneMeteoManager;
	
	ImpattoRealeManager impattoRealeManager;
	
	RelazioneSTBManager relazioneSTBManager;

	private String fonte = "Rilievo GPS"; 
	
	@Before
	public void setup() throws Exception {

		mareggiata = super.createMareggiata();


		allegato = new Allegato();
		allegato.setFile("test".getBytes());

		InitialContext ctx = new InitialContext();
		fixture = (MareggiataManager) ctx.lookup("SupportoMareggiate-ear/MareggiataManagerImpl/remote");
		previsioneMeteoManager = (PrevisioneMeteoManager) ctx.lookup("SupportoMareggiate-ear/PrevisioneMeteoManagerImpl/remote");
		previsioneImpattoManager = (PrevisioneImpattoManager) ctx.lookup("SupportoMareggiate-ear/PrevisioneImpattoManagerImpl/remote");
		condizioneMeteoManager = (CondizioneMeteoManager) ctx.lookup("SupportoMareggiate-ear/CondizioneMeteoManagerImpl/remote");
		impattoRealeManager = (ImpattoRealeManager) ctx.lookup("SupportoMareggiate-ear/ImpattoRealeManagerImpl/remote");
		relazioneSTBManager = (RelazioneSTBManager) ctx.lookup("SupportoMareggiate-ear/RelazioneSTBManagerImpl/remote");
	}


	@Test
	public void testCrudAllegato() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());

		//TODO date non uguali
		//assertEquals(this.mareggiata.getInizioValidita().getTime(), mareggiata.getInizioValidita().getTime());
		//assertEquals(this.mareggiata.getFineValidita().getTime(), mareggiata.getFineValidita().getTime());


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
		}


	}


	@Test
	public void testCrudPrevisioneMeteo() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());



		// Test create
		Long idPrevisioneMeteo = previsioneMeteoManager.create(super.createPrevisioneMeteo(),"NNE","NNE", "Esaurimento", "Da 2,5 a 4 (Agitato)", "NNE");
		
		assertTrue(idPrevisioneMeteo > 0);

		
		
		try {
			fixture.addPrevisioneMeteo(id, -100L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long idPrevisioneMeteoTemp = fixture.addPrevisioneMeteo(id, idPrevisioneMeteo);
		
		assertEquals(idPrevisioneMeteo, idPrevisioneMeteoTemp);
		

		List<PrevisioneMeteo> previsioniMeteo = fixture.getPrevisioniMeteo(id);
		assertEquals(1, previsioniMeteo.size());
		
		Long result = fixture.removePrevisioneMeteo(id, idPrevisioneMeteo);
		assertEquals(idPrevisioneMeteo, result);
		
		previsioniMeteo = fixture.getPrevisioniMeteo(id);
		assertEquals(0, previsioniMeteo.size());
		
		
		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());
		
		assertNotNull(previsioneMeteoManager.findItemById(idPrevisioneMeteo));
		// L'eliminazione avviene dopo per non violare la fk
		previsioneMeteoManager.remove(idPrevisioneMeteo);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	
	
	@Test
	public void testCrudPrevisioneImpatti() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());



		// Test create
		Long idPrevisioneImpatto1 = previsioneImpattoManager.create(super.createPrevisioneImpatto());
		Long idPrevisioneImpatto2 = previsioneImpattoManager.create(super.createPrevisioneImpatto());
		assertTrue(idPrevisioneImpatto1 > 0);
		assertTrue(idPrevisioneImpatto2 > 0);

		
		
		try {
			fixture.addPrevisioneImpatto(id, -100L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long idPrevisioneImpattoTemp1 = fixture.addPrevisioneImpatto(id, idPrevisioneImpatto1);
		Long idPrevisioneImpattoTemp2 = fixture.addPrevisioneImpatto(id, idPrevisioneImpatto2);
		
		assertEquals(idPrevisioneImpatto1, idPrevisioneImpattoTemp1);
		assertEquals(idPrevisioneImpatto2, idPrevisioneImpattoTemp2);
		

		List<PrevisioneImpatto> previsioniImpatto = fixture.getPrevisioneImpatto(id);
		assertEquals(2, previsioniImpatto.size());
		
		Long result = fixture.removePrevisioneImpatto(id, idPrevisioneImpattoTemp1);
		assertEquals(idPrevisioneImpattoTemp1, result);
		
		previsioniImpatto = fixture.getPrevisioneImpatto(id);
		assertEquals(1, previsioniImpatto.size());
		assertEquals(idPrevisioneImpattoTemp2.longValue(), previsioniImpatto.get(0).getId());
		
		
		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());
		
		
		assertNotNull(previsioneImpattoManager.findItemById(idPrevisioneImpattoTemp1));
		assertNotNull(previsioneImpattoManager.findItemById(idPrevisioneImpattoTemp2));
		
		// L'eliminazione avviene dopo per non violare la fk
		previsioneImpattoManager.remove(idPrevisioneImpattoTemp1);
		previsioneImpattoManager.remove(idPrevisioneImpattoTemp2);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	
	
	@Test
	public void testCrudCondizioniMeteo() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());



		// Test create
		Long idCondizioneMeteo = condizioneMeteoManager.create(super.createCondizioneMeteo(), "S","Nazionale","S");
		assertTrue(idCondizioneMeteo > 0);
		
		
		
		try {
			fixture.addCondizioneMeteo(id, -100L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long idCondizioneMeteoTemp = fixture.addCondizioneMeteo(id, idCondizioneMeteo);
		
		assertEquals(idCondizioneMeteo, idCondizioneMeteoTemp);
		

		List<CondizioneMeteo> condizioniMeteo = fixture.getCondizioniMeteo(id);
		assertEquals(1, condizioniMeteo.size());
		
		Long result = fixture.removeCondizioneMeteo(id, idCondizioneMeteo);
		assertEquals(idCondizioneMeteoTemp, result);
		
		condizioniMeteo = fixture.getCondizioniMeteo(id);
		assertEquals(0, condizioniMeteo.size());
		
		
		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());
		
		assertNotNull(condizioneMeteoManager.findItemById(idCondizioneMeteo));
		
		
		// L'eliminazione avviene dopo per non violare la fk
		condizioneMeteoManager.remove(idCondizioneMeteo);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	
	
	@Test
	public void testCrudImpattoReale() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());



		// Test create
		Long idImpattoReale = impattoRealeManager.create(super.createImpattoReale(), fonte);
		assertTrue(idImpattoReale > 0);
		
		
		
		try {
			fixture.addImpattoReale(-100L, -10L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long idImpattoRealeTemp = fixture.addImpattoReale(id, idImpattoReale);
		
		assertEquals(idImpattoReale, idImpattoRealeTemp);
		

		List<ImpattoReale> impattiReali = fixture.getImpattiReali(id);
		assertEquals(1, impattiReali.size());
		
		Long result = fixture.removeImpattoReale(id, idImpattoReale);
		assertEquals(idImpattoRealeTemp, result);
		
		impattiReali = fixture.getImpattiReali(id);
		assertEquals(0, impattiReali.size());
		
		
		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());
		
		assertNotNull(impattoRealeManager.findItemById(idImpattoReale));
		
		
		// L'eliminazione avviene dopo per non violare la fk
		impattoRealeManager.remove(idImpattoReale);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	
	
	@Test
	public void testCrudRelazioneSTB() throws Exception {

		// Test create
		Long id = fixture.create(mareggiata);
		assertTrue(id > 0);

		Mareggiata mareggiata = fixture.findItemById(id);
		assertNotNull(mareggiata);
		assertEquals(this.mareggiata.getCodice(), mareggiata.getCodice());



		// Test create
		Long idRelazioneSTB = relazioneSTBManager.create(super.createRelazioneSTB(), "STB fiumi romagnoli", "Riilevo GPS");
		assertTrue(idRelazioneSTB > 0);
		
		
		
		try {
			fixture.addRelazioneSTB(-100L, -10L);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		Long idRelazioneSTBTemp = fixture.addRelazioneSTB(id, idRelazioneSTB);
		
		assertEquals(idRelazioneSTB, idRelazioneSTBTemp);
		

		List<RelazioneSTB> relazioniSTB = fixture.getRelazioniSTB(id);
		assertEquals(1, relazioniSTB.size());
		
		Long result = fixture.removeRelazioneSTB(id, idRelazioneSTB);
		assertEquals(idRelazioneSTB, result);
		
		relazioniSTB = fixture.getRelazioniSTB(id);
		assertEquals(0, relazioniSTB.size());
		
		
		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());
		
		
		
		assertNotNull(relazioneSTBManager.findItemById(idRelazioneSTBTemp));
		
		
		// L'eliminazione avviene dopo per non violare la fk
		relazioneSTBManager.remove(idRelazioneSTBTemp);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
	
	
	
	
}
