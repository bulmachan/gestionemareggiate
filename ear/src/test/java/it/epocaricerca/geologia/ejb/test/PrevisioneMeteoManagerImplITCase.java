package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.naming.InitialContext;

import it.epocaricerca.geologia.ejb.dao.PagingManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.TipoAllegato;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;

public class PrevisioneMeteoManagerImplITCase extends BaseTest {

	PrevisioneMeteo previsione;

	Allegato allegato;

	PrevisioneMeteoManager fixture;

	PagingManager pagingManager;

	@Before
	public void setup() throws Exception {

		previsione = super.createPrevisioneMeteo();


		allegato = new Allegato();
		//allegato.setTipo(TipoAllegato.Bollettino_ARPA);
		allegato.setFile("test".getBytes());

		InitialContext ctx = new InitialContext();
		fixture = (PrevisioneMeteoManager) ctx.lookup("SupportoMareggiate-ear/PrevisioneMeteoManagerImpl/remote");
		pagingManager = (PagingManager) ctx.lookup("SupportoMareggiate-ear/PagingManagerImpl/remote");
	}


	@Test
	public void testCrudAllegato() throws Exception {

		// Test create
		Long id = fixture.create(previsione, "NNE","NNE", "Esaurimento", "Da 2,5 a 4 (Agitato)", "NNE");
		assertTrue(id > 0);

		PrevisioneMeteo previsione = fixture.findItemById(id);
		assertNotNull(previsione);
		assertEquals(this.previsione.getDescrizione(), previsione.getDescrizione());
		assertEquals("NNE", previsione.getPedemontanaDirezioneVento().getNome());
		assertEquals("NNE", previsione.getPianuraDirezioneVento().getNome());
		assertEquals("Esaurimento", previsione.getTendenza().getNome());


		// Test insertAllegato
		Long idAllegato = fixture.insertAllegato(id, allegato,"Bollettino ARPA");
		assertTrue(idAllegato > 0);

		List<Allegato> allegati = fixture.getAllegati(id);

		assertTrue(allegati.size() == 1);
		assertEquals(idAllegato.longValue(),allegati.get(0).getId());
		assertEquals("Bollettino ARPA",allegati.get(0).getTipo().getNome());


		// Test update

		Long result = fixture.update(previsione, "NE", "NE", "Stazionarieta", "Da 2,5 a 4 (Agitato)", "NNE");
		assertEquals(id.longValue(), result.longValue());

		previsione = fixture.findItemById(id);
		assertNotNull(previsione);
		assertEquals(this.previsione.getDescrizione(), previsione.getDescrizione());
		assertEquals("NE", previsione.getPedemontanaDirezioneVento().getNome());
		assertEquals("NE", previsione.getPianuraDirezioneVento().getNome());
		assertEquals("Stazionarieta", previsione.getTendenza().getNome());
		
		allegati = fixture.getAllegati(id);

		assertTrue(allegati.size() == 1);
		assertEquals(idAllegato.longValue(),allegati.get(0).getId());
		assertEquals("Bollettino ARPA",allegati.get(0).getTipo().getNome());



		// Test removeAllegato
		result = fixture.removeAllegato(id, idAllegato);

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
		Long id = fixture.create(previsione,"NNE","NNE", "Esaurimento", "Da 2,5 a 4 (Agitato)", "NNE");
		assertTrue(id > 0);

		PrevisioneMeteo previsione = fixture.findItemById(id);
		assertNotNull(previsione);
		assertEquals(this.previsione.getDescrizione(), previsione.getDescrizione());
		assertEquals("NNE", previsione.getPedemontanaDirezioneVento().getNome());
		assertEquals("NNE", previsione.getPianuraDirezioneVento().getNome());
		assertEquals("Esaurimento", previsione.getTendenza().getNome());


		// Test update

		Long result = fixture.update(previsione,"NE" ,"NE", "Stazionarieta", "Da 1,25 a 2,5 (Molto Mosso)", "NE");
		assertEquals(id.longValue(), result.longValue());

		previsione = fixture.findItemById(id);
		assertNotNull(previsione);
		assertEquals(this.previsione.getDescrizione(), previsione.getDescrizione());
		assertEquals("NE", previsione.getPedemontanaDirezioneVento().getNome());
		assertEquals("NE", previsione.getPianuraDirezioneVento().getNome());
		assertEquals("Stazionarieta", previsione.getTendenza().getNome());

		//Test search

		int numEntity = pagingManager.countByCriteria(PrevisioneMeteo.class, null);
		assertEquals(1, numEntity);

		List<Criterion> criterions = new ArrayList<Criterion>();
		criterions.add(Restrictions.like("idAvviso", "123Allegato"));

		numEntity = pagingManager.countByCriteria(PrevisioneMeteo.class,criterions);
		assertEquals(1, numEntity);

		List<PrevisioneMeteo> previsioni = pagingManager.findByCriteria(PrevisioneMeteo.class,0, 100, criterions, null);
		assertNotNull(previsioni);
		assertEquals(1, previsioni.size());
		assertEquals("NE", previsioni.get(0).getPianuraDirezioneVento().getNome());

		criterions.clear();

		criterions.add(Restrictions.like("idAvviso", "dadsdsa"));

		numEntity = pagingManager.countByCriteria(PrevisioneMeteo.class,criterions);
		assertEquals(0, numEntity);

		criterions.clear();

		Calendar start = Calendar.getInstance();
		start.add(Calendar.DAY_OF_MONTH, -5);

		Calendar end = Calendar.getInstance();
		end.add(Calendar.DAY_OF_MONTH, 5);

		criterions.add(Restrictions.and(
				Restrictions.ge("inizioValidita", start.getTime()),
				Restrictions.le("fineValidita", end.getTime())));

		numEntity = pagingManager.countByCriteria(PrevisioneMeteo.class,criterions);
		assertEquals(1, numEntity);


		// Test insertEventoCostiero
		EventoCostiero evento1 = super.createEventoCostiero(1);
		EventoCostiero evento2 = super.createEventoCostiero(2);
		Long idEventoCostiero = fixture.insertEventoCostiero(id,evento1,"NNE","B1", "Livello del mare sopra soglia");
		assertTrue(idEventoCostiero > 0);

		List<EventoCostiero> eventiCostieri = fixture.getEventiCostieri(id);

		assertTrue(eventiCostieri.size() == 1);
		assertEquals(idEventoCostiero.longValue(),eventiCostieri.get(0).getId());
		assertEquals(evento1.getAltezzaOnda(),eventiCostieri.get(0).getAltezzaOnda(),0.000001f);
		assertEquals("B1", eventiCostieri.get(0).getMacroArea().getNome());
		assertEquals("NNE", eventiCostieri.get(0).getDirezione().getNome());

		idEventoCostiero = fixture.insertEventoCostiero(id,evento2,"NNE","B1", "Livello onda sopra soglia");
		assertTrue(idEventoCostiero > 0);

		eventiCostieri = fixture.getEventiCostieri(id);

		assertTrue(eventiCostieri.size() == 2);


		// Test removeEventoCostiero
		result = fixture.removeEventoCostiero(id,idEventoCostiero);
		assertEquals(idEventoCostiero.longValue(),result.longValue());

		eventiCostieri = fixture.getEventiCostieri(id);

		assertTrue(eventiCostieri.size() == 1);



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
