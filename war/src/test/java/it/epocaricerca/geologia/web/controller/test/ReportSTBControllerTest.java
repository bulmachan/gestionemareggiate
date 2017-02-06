package it.epocaricerca.geologia.web.controller.test;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Comune;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Indirizzo;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.STB;
import it.epocaricerca.geologia.model.TipoAllegato;
import it.epocaricerca.geologia.web.controller.ReportSTBController;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class ReportSTBControllerTest {

	ReportSTBController fixture = new ReportSTBController();
	
	@Before
	public void setup() throws Exception {
		STB stb = new STB();
		stb.setNome("Servizio Tecnico Bacino Po di Volano e della Costa");
		
		
		Map<Indirizzo, Boolean> checkedIndirizzi = new HashMap<Indirizzo, Boolean>();
		checkedIndirizzi.put(new Indirizzo("All'Assessore Sicurezza territoriale, difesa del suolo e della costa, protezione civile","Via della Fiera, 8 40127 Bologna"), true);
		checkedIndirizzi.put(new Indirizzo("Al Direttore generale ambiente e difesa del suolo e della costa","Via della Fiera, 8 40127 Bologna"), true);
		checkedIndirizzi.put(new Indirizzo("Al Responsabile del Servizio difesa del suolo, della costa e bonifica","Via della Fiera, 8 40127 Bologna"), true);
		checkedIndirizzi.put(new Indirizzo("Al Responsabile dell'Agenzia di Protezione Civile","Viale Silvani 6, 40122 Bologna"), true);
		checkedIndirizzi.put(new Indirizzo("Al Responsabile del Servizio Tecnico di bacino Romagna","Via Rosaspina 7, 47900 RIMINI"), true);
		
		fixture.setItemToAct(1);
		fixture.setCheckedIndirizzi(checkedIndirizzi);
		fixture.setTitle("Ing.");
		
		RelazioneSTB relazione = new RelazioneSTB();
		fixture.setRelazioneSTB(relazione);
		fixture.setRelazioneTecnica(true);
		fixture.setResponsabile("Andrea Peretti");
		relazione.setStb(stb);
		
		relazione.setDescrizione("2° report dei danni al litorale regionale conseguenti alla mareggiata del 11 novembre 2013.\n"+
								"L'11 novembre abbiamo registrato una mareggiata che ha danneggiato il sistema litorale regionale, per concomitanza sfavorevole di altezza d'onda e acqua alta.\n"+
								"L'evento è stato caratterizzato da forte vento inizialmente da N e poi da NE (Bora) oltre 70 km/ora con raffiche fino a 100 km/ora, altezza d'onda oltre 3,7 m al largo (boa ondametrica regionale di Cesenatico) e oltre 2 m sotto costa, quote di acqua alta di +0,85 m al mareografo di Porto Corsini RA.\n"+
								"Le mareggiate hanno causato danni agli argini invernali realizzati sulle spiagge e posti a difesa delle infrastrutture, formazione di falesie di erosione sui tratti critici del litorale regionale, allagamenti e danni su strutture turistiche, ingressione di mare e sabbia sulle strade pubbliche, spiaggiamenti di tronchi e detriti. Per effetto della maggiore lunghezza del fetch il livello di danno è andato intensificandosi da nord verso sud,\n"+
								"Gli STB, durante e dopo la mareggiata, hanno eseguito accertamenti e sopralluoghi, anche congiuntamente ai tecnici comunali e alcuni rappresentanti degli operatori di spiaggia lungo tutti i tratti danneggiati.");
		
		Comune comacchio = new Comune();
		comacchio.setNome("Comacchio");
		
		Localita volano = new Localita();
		volano.setComune(comacchio);
		volano.setNome("Località Lido di Volano");
		
		DannoSTB danno1 = new DannoSTB();
		danno1.setLocalita(volano);
		danno1.setStimaCostiDanni(10000f);
		danno1.setDescrizione("L'area degli stabilimenti balneari non è stata particolarmente colpita, si registrano falesie di erosione di spiaggia nella zona di Volano sud pineta.");

		DannoSTB danno2 = new DannoSTB();
		danno2.setLocalita(volano);
		danno2.setStimaCostiDanni(1000f);
		danno2.setDescrizione("L'area degli stabilimenti balneari non è stata particolarmente colpita, si registrano falesie di erosione di spiaggia nella zona di Volano sud pineta.");

		
		Allegato allegato = new Allegato();
		allegato.setFile(FileUtils.readFileToByteArray(new File(this.getClass().getResource("/images/danno.png").getFile())));
		TipoAllegato tipoAllegato = new TipoAllegato();
		tipoAllegato.setNome("Immagine");
		allegato.setTipo(tipoAllegato);
		allegato.setNome("Erosione al Lido di Volano, zona sud.png");
		
		danno1.getAllegati().add(allegato);
		danno2.getAllegati().add(allegato);
		relazione.getDanni().add(danno1);
		relazione.getDanni().add(danno2);
	}


	@Test
	public void testReportPdf() throws Exception 
	{
		fixture.generaReportItem();
	}
}
