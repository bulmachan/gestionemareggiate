package it.epocaricerca.geologia.ejb.test;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.SensorDataSourceManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

import javax.naming.InitialContext;
import javax.persistence.Table;

import org.junit.Before;
import org.junit.Test;

public class ArpaDataSourceITCase extends BaseTest {



	private SensorDataSourceManager fixture;

	private VariabileManager variabileManager;

	private StazioneManager stazioneManager;

	CondizioneMeteoManager condizioneMeteoManager;

	@Before
	public void setup() throws Exception {

		InitialContext ctx = new InitialContext();
		fixture = (SensorDataSourceManager) ctx.lookup("SupportoMareggiate-ear/ArpaDataSource/remote");

		condizioneMeteoManager = (CondizioneMeteoManager) ctx.lookup("SupportoMareggiate-ear/CondizioneMeteoManagerImpl/remote");


		variabileManager = (VariabileManager) ctx.lookup("SupportoMareggiate-ear/VariabileManagerImpl/remote");

		stazioneManager = (StazioneManager) ctx.lookup("SupportoMareggiate-ear/StazioneManagerImpl/remote");
	}


	@Test
	public void testgetDate() throws Exception 
	{

		VariabileStazione variabile3 = super.createVariabile3();

		Stazione stazione = super.createStazione();
		stazione.addVariabile(variabile3);
		long stazioneId = stazioneManager.create(stazione);

		long variabileId = variabileManager.findItemByCodice(variabile3.getCodice()).getId();

		assertTrue(stazioneId>0);
		Calendar startDate = Calendar.getInstance();
		startDate.set(Calendar.DAY_OF_MONTH, 19);
		startDate.set(Calendar.MONTH, 07);
		startDate.set(Calendar.YEAR, 2013);

	
		Calendar endDate = Calendar.getInstance();
		endDate.set(Calendar.DAY_OF_MONTH, 21);
		endDate.set(Calendar.MONTH, 07);
		endDate.set(Calendar.YEAR, 2013);


		List<DatoSensore> data = fixture.getDataFromVariabileStazione(stazioneId, variabileId, endDate.getTime(), endDate.getTime());

		assertNotNull(data);
		assertTrue(data.size() == 0);


		data = fixture.getDataFromVariabileStazione(stazioneId, variabileId, startDate.getTime(), endDate.getTime());

		assertNotNull(data);
		assertTrue(data.size() > 0);


		for (DatoSensore datoSensore : data) {
			assertTrue(datoSensore.getTimestamp().after(startDate.getTime()) && datoSensore.getTimestamp().before(endDate.getTime()) );
		}




		CondizioneMeteo condizioneMeteo = super.createCondizioneMeteo();
		long idCondizioneMeteo = condizioneMeteoManager.create(condizioneMeteo,"S","Nazionale","S");
		assertTrue(idCondizioneMeteo > 0);

		long rilevazioneId = condizioneMeteoManager.insertRilevazione(idCondizioneMeteo,data, variabileId , stazioneId,0,10);

		assertTrue(rilevazioneId > 0);

		List<Rilevazione> rilevazioni = condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);

		assertEquals(1, rilevazioni.size());

		assertEquals(rilevazioneId, condizioneMeteoManager.removeRilevazione(idCondizioneMeteo, rilevazioneId).longValue());
		rilevazioni = condizioneMeteoManager.getRilevazioni(idCondizioneMeteo);

		assertEquals(0, rilevazioni.size());


		condizioneMeteoManager.remove(idCondizioneMeteo);

		assertNull(condizioneMeteoManager.findItemById(idCondizioneMeteo));



		stazioneManager.remove(stazioneId);
		variabileManager.remove(variabileId);

	}

}
