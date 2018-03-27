package it.epocaricerca.geologia.ejb.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Estensione;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.MacroArea;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.Tendenza;
import it.epocaricerca.geologia.model.VariabileStazione;
import it.epocaricerca.geologia.model.auth.Role;
import it.epocaricerca.geologia.model.auth.User;

public class BaseTest {

	protected final long idMareografoPortoCorsini = 1;
	
	protected final long idBoaCesenatico = 1;

	
	
	protected Mareggiata createMareggiata()
	{
		Mareggiata mareggiata = new Mareggiata();
		mareggiata.setCodice("xyz");
		mareggiata.setDescrizione("xyz");
		mareggiata.setInizioValidita(new Date());
		mareggiata.setFineValidita(new Date());
		return mareggiata;
	}
	
	
	protected User createUser()
	{
		User user = new User();
		user.setUserID("EXTRARER/fontana.damiano");
		return user;
	}
	
	protected Role createRuoloAdmin()
	{
		Role role = new Role();
		role.setDescription("super admin");
		role.setName("admin");
		return role;
	}

	protected Role createRuoloStb()
	{
		Role role = new Role();
		role.setDescription("stb admin");
		role.setName("stb_user");
		return role;
	}
	
	
	protected VariabileStazione createVariabile()
	{
		VariabileStazione variabileStazione = new VariabileStazione();
		variabileStazione.setCodice("684");
		variabileStazione.setDescrizione("massima altezza d'onda");
		return variabileStazione;
	}
	
	
	protected VariabileStazione createVariabile2()
	{
		VariabileStazione variabileStazione = new VariabileStazione();
		variabileStazione.setCodice("683");
		variabileStazione.setDescrizione("livello del mare");
		return variabileStazione;
	}
	
	protected VariabileStazione createVariabile3()
	{
		VariabileStazione variabileStazione = new VariabileStazione();
		variabileStazione.setCodice("440");
		variabileStazione.setDescrizione("vento direzione media oraria vettoriale (URBANE)");
		return variabileStazione;
	}
	
	 
	
	protected Stazione createStazione()
	{
		Stazione stazione = new Stazione();
		stazione.setCodice("2980");
		stazione.setLatitude(22.00);
		stazione.setLongitude(24.00);
		stazione.setNome("Volano");
		stazione.setRete("locali");
		stazione.setSorgente_dati("ARPA");
		return stazione;
	}
	
	protected DannoSTB createDannoSTB()
	{
		DannoSTB danno = new DannoSTB();
		danno.setDescrizione("danno test");
		danno.setLunghezzaTratto(100);
		danno.setStimaCostiDanni(100);
		danno.setVolumiErosi(100);
		
		return danno;


	}

	protected RelazioneGeneraleSTB createRelazioneGeneraleSTB()
	{
		RelazioneGeneraleSTB relazione = new RelazioneGeneraleSTB();
		relazione.setData(new Date());
		relazione.setInformazioniGenerali("test");
		relazione.setInformazioniMeteo("test");
		return relazione;

	}

	protected RelazioneSTB createRelazioneSTB()
	{
		RelazioneSTB relazione = new RelazioneSTB();
		relazione.setDescrizione("test");

		relazione.setFineValidita(new Date());
		relazione.setInizioValidita(new Date());

		return relazione;
	}
	
	
	protected ImpattoReale createImpattoReale()
	{
		ImpattoReale impattoReale = new ImpattoReale();
		impattoReale.setCodice("codice");
		impattoReale.setDescrizione("impatto desc");
		impattoReale.setFineValidita(new Date());
		impattoReale.setInizioValidita(new Date());
		
		return impattoReale;
	}
	
	
	
	protected PrevisioneImpatto createPrevisioneImpatto()
	{
		PrevisioneImpatto previsioneImpatto = new PrevisioneImpatto();
		previsioneImpatto.setCodice("codice");
		previsioneImpatto.setDescrizione("impatto desc");
		previsioneImpatto.setFineValidita(new Date());
		previsioneImpatto.setInizioValidita(new Date());
		
		return previsioneImpatto;
	}
	
	
	protected PrevisioneMeteo createPrevisioneMeteo()
	{
		PrevisioneMeteo previsione = new PrevisioneMeteo();
		previsione.setIdAvviso("123Allegato");
		previsione.setInizioValidita( new Date());
		previsione.setFineValidita(new Date());
		previsione.setDescrizione("test");
		return previsione;


	}

	protected EventoCostiero createEventoCostiero(int num)
	{
		EventoCostiero eventoCostiero = new EventoCostiero();
		eventoCostiero.setAltezzaOnda(10.0f);
		eventoCostiero.setFine(new Date());
		eventoCostiero.setInizio(new Date());
		eventoCostiero.setLivelloMare(10.0f);
		eventoCostiero.setNote("evento "+num);

		return eventoCostiero;
	}

	protected List<DatoSensore> createDatiSensore()
	{
		DatoSensore dato1 = new DatoSensore();
		dato1.setValue(1);
		dato1.setTimestamp(new Date());
		
		DatoSensore dato2 = new DatoSensore();
		dato2.setValue(2.9);
		dato2.setTimestamp(new Date());
		
		DatoSensore dato3 = new DatoSensore();
		dato3.setValue(3.9);
		dato3.setTimestamp(new Date());
		
		ArrayList<DatoSensore> result =  new ArrayList<DatoSensore>();
		result.add(dato1); result.add(dato2); result.add(dato3);
		return result;
		
	}

	protected CondizioneMeteo createCondizioneMeteo()
	{
		CondizioneMeteo condizioneMeteo = new CondizioneMeteo();
		condizioneMeteo.setDurataSopraSoglia(1);
		condizioneMeteo.setFineValidita(new Date());
		condizioneMeteo.setInizioValidita(new Date());
		condizioneMeteo.setMaxAltezzaOndaSignificativa(1.0f);
		condizioneMeteo.setMaxIntensitaVentoPrevalente(1.0f);
		condizioneMeteo.setMaxIntensitaVentoRaffica(1.0f);
		return condizioneMeteo;
	}

	

}
