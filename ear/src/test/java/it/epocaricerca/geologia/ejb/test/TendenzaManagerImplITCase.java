package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.ejb.dao.TendenzaManager;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Tendenza;

import java.util.List;

import javax.ejb.EJBException;
import javax.naming.InitialContext;

import org.junit.Before;
import org.junit.Test;

public class TendenzaManagerImplITCase  extends BaseTest{

	TendenzaManager fixture;

	@Before
	public void setup() throws Exception {


		InitialContext ctx = new InitialContext();
		fixture = (TendenzaManager) ctx.lookup("SupportoMareggiate-ear/TendenzaManagerImpl/remote");
	}


	@Test
	public void testReadProvenienza() throws Exception 
	{
		Tendenza tedenza = fixture.findItemByName("Esaurimento");
		assertNotNull(tedenza);

		List<Tendenza> tedenze = fixture.selectAll();
		assertNotNull(tedenze);
		assertEquals(2, tedenze.size());
	}

	@Test(expected=EJBException.class)
	public void testReadNonExistingProvenienza() throws Exception 
	{
		Tendenza tedenza = fixture.findItemByName("dassd");
	}
}
