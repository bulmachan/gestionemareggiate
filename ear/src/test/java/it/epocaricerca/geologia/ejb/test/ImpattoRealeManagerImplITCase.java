package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;

public class ImpattoRealeManagerImplITCase extends BaseTest {

	DannoManager dannoCRUD;
	ImpattoRealeManager fixture;
	
	private String fonte = "Rilievo GPS";
	
	Allegato allegato;
	
	ImpattoReale impatto;
	
	@Before
	public void setup() throws Exception {
		
		impatto = super.createImpattoReale();
		allegato = new Allegato();
		allegato.setFile("test".getBytes());
		
		InitialContext ctx = new InitialContext();
		dannoCRUD = (DannoManager) ctx.lookup("SupportoMareggiate-ear/DannoManagerImpl/remote");
		fixture =  (ImpattoRealeManager) ctx.lookup("SupportoMareggiate-ear/ImpattoRealeManagerImpl/remote");
	}
	
	
	@Test
	public void testCrudImpattoReale() throws Exception {
		
		// Test create
		Long id = fixture.create(impatto, fonte);
		assertTrue(id > 0);
		
		ImpattoReale impatto = fixture.findItemById(id);
		assertNotNull(impatto);
		
		
		// Test insertDanno
		
		Long dannoId = fixture.insertDanno(id, new Danno(), "Goro","Erosione","POINT(10 15)");
		System.out.println("Danno id"+dannoId);
		Danno danno = dannoCRUD.findItemById(dannoId);
		assertNotNull(danno);
		assertEquals("Goro", danno.getLocalita().getNome());
		assertEquals("Erosione", danno.getTipoDanno().getNome());
		
		List<Danno> danni = fixture.getDanni(id);
		
		assertEquals(1,danni.size());
		assertEquals("Goro", danni.get(0).getLocalita().getNome());
		assertEquals("Erosione", danni.get(0).getTipoDanno().getNome());
		
		
		// Test removeDanno
		Long result = fixture.removeDanno(id, dannoId);
		
		assertEquals(dannoId.longValue(),result.longValue());
		
		danni = fixture.getDanni(id);
		
		assertTrue(danni.size() == 0);
		
	
		
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
