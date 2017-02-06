package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;
import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.LivelloCriticitaManager;
import it.epocaricerca.geologia.ejb.dao.MacroAreaManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneDannoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;

import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class PrevisioneImpattoManagerImplITCase extends BaseTest {

	PrevisioneDannoManager previsioneDannoManager;
	PrevisioneImpattoManager fixture;
	MacroAreaManager macroAreaManager;
	LivelloCriticitaManager livelloCriticitaManager;

	Allegato allegato;

	PrevisioneImpatto impatto;

	@Before
	public void setup() throws Exception {

		impatto = super.createPrevisioneImpatto();
		allegato = new Allegato();
		allegato.setFile("test".getBytes());

		InitialContext ctx = new InitialContext();
		previsioneDannoManager = (PrevisioneDannoManager) ctx.lookup("SupportoMareggiate-ear/PrevisioneDannoManagerImpl/remote");
		fixture =  (PrevisioneImpattoManager) ctx.lookup("SupportoMareggiate-ear/PrevisioneImpattoManagerImpl/remote");
		
		macroAreaManager = JNDIUtils.getMacroAreaManager();
		livelloCriticitaManager = JNDIUtils.getLivelloCriticitaManager();
	}


	@Test
	public void testCrudPrevisioneImpatto() throws Exception {

		// Test create
		Long id = fixture.create(impatto);
		assertTrue(id > 0);

		PrevisioneImpatto impatto = fixture.findItemById(id);
		assertNotNull(impatto);

		impatto.setCodice("xyz");

		Long idTemp =  fixture.update(impatto);
		assertEquals(idTemp, id);

		impatto = fixture.findItemById(id);
		assertNotNull(impatto);
		
		assertEquals("xyz", impatto.getCodice());

		// Test insertDanno
		
		PrevisioneDanno danno = new PrevisioneDanno();
		danno.setCommento("test");
		danno.setLivello_criticita(livelloCriticitaManager.findItemByName("livello 1"));
		danno.setMacroArea(macroAreaManager.findItemByName("B1"));

		Long dannoId = fixture.insertDanno(id, danno);
		System.out.println("Danno id"+dannoId);
		
		danno = previsioneDannoManager.findItemById(dannoId);
		assertNotNull(danno);
		assertEquals("B1", danno.getMacroArea().getNome());
	
		List<PrevisioneDanno> danni = fixture.getDanni(id);

		assertEquals(1,danni.size());
		assertEquals("B1", danni.get(0).getMacroArea().getNome());
		

		// Test removeDanno
		Long result = fixture.removeDanno(id, dannoId);

		assertEquals(dannoId.longValue(),result.longValue());

		danni = fixture.getDanni(id);

		assertTrue(danni.size() == 0);


		// Test insertDanno
		
		danno = new PrevisioneDanno();
		danno.setCommento("test");
		danno.setLivello_criticita(livelloCriticitaManager.findItemByName("livello 1"));
		
		danno.setMacroArea(macroAreaManager.findItemByName("B1"));

		dannoId = fixture.insertDanno(id, danno);
		System.out.println("Danno id"+dannoId);
		danno = previsioneDannoManager.findItemById(dannoId);
		assertNotNull(danno);
		assertEquals("B1", danno.getMacroArea().getNome());
		
		danni = fixture.getDanni(id);

		assertEquals(1,danni.size());
		assertEquals("B1", danni.get(0).getMacroArea().getNome());
	


		// Test remove
		Long removedId = fixture.remove(id);
		assertEquals(id.longValue(),removedId.longValue());

		danno = previsioneDannoManager.findItemById(dannoId);
		assertNull(danno);


		try {
			fixture.remove(id);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}


	}
}
