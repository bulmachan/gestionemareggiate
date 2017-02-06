package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.model.Provenienza;

import javax.ejb.EJBException;
import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class ProvenienzaManagerImplITCase extends BaseTest {

	
	ProvenienzaManager fixture;
	
	@Before
	public void setup() throws Exception {


		InitialContext ctx = new InitialContext();
		fixture = (ProvenienzaManager) ctx.lookup("SupportoMareggiate-ear/ProvenienzaManagerImpl/remote");
	}
	
	
	@Test
	public void testReadProvenienza() throws Exception 
	{
		Provenienza provenienza = fixture.findItemByName("NNE");
		assertNotNull(provenienza);
		
		List<Provenienza> provenienze = fixture.selectAll();
		assertNotNull(provenienze);
		assertEquals(3, provenienze.size());
	}
	
	@Test(expected=EJBException.class)
	public void testReadNonExistingProvenienza() throws Exception 
	{
		Provenienza provenienza = fixture.findItemByName("dassd");
	}
}
