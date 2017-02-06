package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.TendenzaManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.Tendenza;
import it.epocaricerca.geologia.model.VariabileStazione;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class VariabileStazioneImplITCase extends BaseTest {

	
	private VariabileManager variabileManager;
	
	private StazioneManager stazioneManager;

	@Before
	public void setup() throws Exception {


		InitialContext ctx = new InitialContext();
		variabileManager = (VariabileManager) ctx.lookup("SupportoMareggiate-ear/VariabileManagerImpl/remote");
		
		stazioneManager = (StazioneManager) ctx.lookup("SupportoMareggiate-ear/StazioneManagerImpl/remote");
	}
	

	
	@Test
	public void testCRUD() throws Exception 
	{
		VariabileStazione variabile = super.createVariabile();
	
		Stazione stazione = super.createStazione();
		stazione.addVariabile(variabile);
		long id = stazioneManager.create(stazione);
		
		assertTrue(id>0);
		
		VariabileStazione retrieved = variabileManager.findItemByCodice("684");
		assertEquals(variabile.getCodice(), retrieved.getCodice());
		
		List<Stazione> stazioni = stazioneManager.findItemBySorgente("ARPA");
		assertTrue( stazioni.size() > 0);
		
		stazione = stazioneManager.findItemById(id);
		assertEquals(1, stazione.getVariabili().size());
		
		stazioneManager.remove(id);
		
		variabileManager.remove(retrieved.getId());
		
		
	}

}
