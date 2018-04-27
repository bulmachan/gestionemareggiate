package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertTrue;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.RilevazioneManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class RilevazioneManagerImplITCase extends BaseTest {

	RilevazioneManager fixture;
	
	
	private VariabileManager variabileManager;
	
	private StazioneManager stazioneManager;
	
	
	@Before
	public void setup() throws Exception {
		
		
		InitialContext ctx = new InitialContext();
		fixture = (RilevazioneManager) ctx.lookup("SupportoMareggiate-ear/RilevazioneManagerImpl/remote");
		variabileManager = (VariabileManager) ctx.lookup("SupportoMareggiate-ear/VariabileManagerImpl/remote");
		
		stazioneManager = (StazioneManager) ctx.lookup("SupportoMareggiate-ear/StazioneManagerImpl/remote");

	}
	
	
	@Test
	public void testCrudRilevazioneMarea() throws Exception 
	{
		
		VariabileStazione variabile = super.createVariabile();
		
		Stazione stazione = super.createStazione();
		stazione.addVariabile(variabile);
		long stazioneId = stazioneManager.create(stazione);
		
		long variabileId = variabileManager.findItemByCodice(variabile.getCodice()).getId();
		
		assertTrue(stazioneId>0);
		
		List<DatoSensore> dati = super.createDatiSensore();
		Long idRilevazione = fixture.create( dati, variabileId,stazioneId, 0 ,10);
		assertTrue(idRilevazione > 0);
		
		
		
		Rilevazione rilevazione = fixture.findItemById(idRilevazione);
		assertNotNull(rilevazione);
		assertEquals(variabile.getCodice(), rilevazione.getVariabileStazione().getCodice());
		assertEquals(stazione.getCodice(), rilevazione.getStazione().getCodice());
		assertEquals(dati.size(), fixture.findDatiById(idRilevazione).size());
		
		try {
			fixture.remove(-1l);
			fail();
		} catch (Exception e) { assertTrue(true);}
		
		Long removedItem = fixture.remove(idRilevazione);
		assertEquals(idRilevazione, removedItem);
		
		assertNull(fixture.findItemById(idRilevazione));
		
		stazioneManager.remove(stazioneId);
		
		variabileManager.remove(variabileId);
		
		
		
		
	}
}
