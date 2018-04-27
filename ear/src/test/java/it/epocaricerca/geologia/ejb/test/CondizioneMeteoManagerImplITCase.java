package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;

import java.util.Iterator;
import java.util.List;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;



public class CondizioneMeteoManagerImplITCase extends BaseTest {

	CondizioneMeteoManager condizioneMeteoManager;
	

	private VariabileManager variabileManager;
	
	private StazioneManager stazioneManager;
	
	@Before
	public void setup() throws Exception {
		
		
		InitialContext ctx = new InitialContext();
		condizioneMeteoManager = (CondizioneMeteoManager) ctx.lookup("SupportoMareggiate-ear/CondizioneMeteoManagerImpl/remote");
		
		
		variabileManager = (VariabileManager) ctx.lookup("SupportoMareggiate-ear/VariabileManagerImpl/remote");
		
		stazioneManager = (StazioneManager) ctx.lookup("SupportoMareggiate-ear/StazioneManagerImpl/remote");
	}
	
	
	@Test
	public void testCrudCondizioneMeteo() throws Exception 
	{
		CondizioneMeteo condizioneMeteo = super.createCondizioneMeteo();
		long idCondizioneMeteo = condizioneMeteoManager.create(condizioneMeteo,"S","Nazionale","S");
		assertTrue(idCondizioneMeteo > 0);
		
		CondizioneMeteo persisted = condizioneMeteoManager.findItemById(idCondizioneMeteo);
		
		assertEquals(idCondizioneMeteo, persisted.getId());
		
		assertEquals("S", persisted.getOnda().getNome());
		
		//persisted.setOnda(Provenienza.ENE);
		
		long idUpdate = condizioneMeteoManager.update(persisted,"S","Nazionale","S");
		
		assertEquals(idCondizioneMeteo, idUpdate);
		
		
		persisted = condizioneMeteoManager.findItemById(idCondizioneMeteo);
		
		//assertEquals(Provenienza.ENE, persisted.getOnda());
		
	
		
		condizioneMeteoManager.remove(idCondizioneMeteo);
		
		assertNull(condizioneMeteoManager.findItemById(idCondizioneMeteo));
		
		
		
	}
	

	@Test
	public void testCrudRilevazioneMarea() throws EntityNotFoundException
	{
		
		VariabileStazione variabile = super.createVariabile();
		
		VariabileStazione variabile2 = super.createVariabile2();
		
		Stazione stazione = super.createStazione();
		stazione.addVariabile(variabile);
		stazione.addVariabile(variabile2);
		long stazioneId = stazioneManager.create(stazione);
		
		long variabileId = variabileManager.findItemByCodice(variabile.getCodice()).getId();
		long variabileId2 = variabileManager.findItemByCodice(variabile2.getCodice()).getId();
		
		assertTrue(stazioneId>0);
		
		List<DatoSensore> dati = super.createDatiSensore();
		
		CondizioneMeteo condizioneMeteo = super.createCondizioneMeteo();
		long idCondizioneMeteo = condizioneMeteoManager.create(condizioneMeteo,"S","Nazionale","S");
		assertTrue(idCondizioneMeteo > 0);
		
		long rilevazioneMarea1 = condizioneMeteoManager.insertRilevazione(idCondizioneMeteo,dati, variabileId , stazioneId,0,10);
		long rilevazioneMarea2 = condizioneMeteoManager.insertRilevazione(idCondizioneMeteo, dati , variabileId2,stazioneId,0,10);
		
		assertTrue(rilevazioneMarea1 > 0);
		assertTrue(rilevazioneMarea2 > 0);
		
		List<Rilevazione> rilevazioni = condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);
		
		assertEquals(2, rilevazioni.size());
		
		for (Iterator iterator = rilevazioni.iterator(); iterator.hasNext();) {
			Rilevazione temp = (Rilevazione) iterator.next();
			assertNotNull(temp.getStazione());
			assertEquals(stazione.getCodice(), temp.getStazione().getCodice());
			
		}
		try {
			condizioneMeteoManager.insertRilevazione(idCondizioneMeteo,dati, variabileId ,-1L,0,10);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		try {
			condizioneMeteoManager.insertRilevazione(idCondizioneMeteo,dati, -1L ,stazioneId,0,10);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		assertEquals(rilevazioneMarea1, condizioneMeteoManager.removeRilevazione(idCondizioneMeteo, rilevazioneMarea1).longValue());
		rilevazioni = condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);
		
		assertEquals(1, rilevazioni.size());
		
		
		long idCondizioneMeteo2 = condizioneMeteoManager.create(condizioneMeteo,"S","Nazionale","S");
		assertTrue(idCondizioneMeteo2 > 0);
		
		
		assertEquals(-1L, condizioneMeteoManager.removeRilevazione(idCondizioneMeteo2, rilevazioneMarea2).longValue());
		
		condizioneMeteoManager.remove(idCondizioneMeteo2);
		
		assertEquals(rilevazioneMarea2, condizioneMeteoManager.removeRilevazione(idCondizioneMeteo, rilevazioneMarea2).longValue());
		
		
		rilevazioni = condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);
		
		assertEquals(0, rilevazioni.size());
		
		
		condizioneMeteoManager.remove(idCondizioneMeteo);
		
		assertNull(condizioneMeteoManager.findItemById(idCondizioneMeteo));
		
		try {
			condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
		
		
		stazioneManager.remove(stazioneId);
		
		variabileManager.remove(variabileId);
		
		variabileManager.remove(variabileId2);
		
	}
	
	
	
	
}
