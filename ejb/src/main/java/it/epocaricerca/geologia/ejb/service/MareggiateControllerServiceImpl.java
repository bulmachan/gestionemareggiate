package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

@Stateless
@Remote
public class MareggiateControllerServiceImpl implements MareggiateControllerService {

	
	private static Logger logger = Logger.getLogger(MareggiateControllerServiceImpl.class);
	
	@EJB
	private MareggiataManager mareggiataManager;

	
	
	public void salvaMareggiata(Mareggiata mareggiata,FileUploadBean allegati, Map<String, TipoAllegato> tipiAllegato,
								List<PrevisioneMeteo> filtered_previsioniMeteo,
								List<PrevisioneImpatto> filtered_previsioniImpatti, List<ImpattoReale> filtered_impattiReali,
								List<CondizioneMeteo> filtered_condizioniMeteo,List<RelazioneSTB> filtered_relazioniSTB,
								Map<Long, Boolean> checkedRelazioniSTB, Map<Long, Boolean> checkedPrevisioniMeteo ,
								Map<Long, Boolean> checkedImpattiReali, Map<Long, Boolean> checkedCondizioniMeteo,
								Map<Long, Boolean> checkedPrevisioniImpatti) throws EntityNotFoundException, IOException {



		
			long idMareggiata = mareggiataManager.create(mareggiata);



			for (PrevisioneMeteo previsioneMeteo : filtered_previsioniMeteo) {
				if (checkedPrevisioniMeteo.get(previsioneMeteo.getId())) {
					logger.info("Aggiungo relazione Previsione Impatto "+previsioneMeteo.getId()+" alla Mareggiata "+idMareggiata);
					mareggiataManager.addPrevisioneMeteo(idMareggiata, previsioneMeteo.getId());
				}
			}

			for (PrevisioneImpatto previsioneImpatto : filtered_previsioniImpatti) {
				if (checkedPrevisioniImpatti.get(previsioneImpatto.getId())) {
					logger.info("Aggiungo relazione Previsione Impatto "+previsioneImpatto.getId()+" alla Mareggiata "+idMareggiata);
					mareggiataManager.addPrevisioneImpatto(idMareggiata, previsioneImpatto.getId());
				}
			}

			for (CondizioneMeteo condizioneMeteo : filtered_condizioniMeteo) {
				if (checkedCondizioniMeteo.get(condizioneMeteo.getId())) {
					logger.info("Aggiungo relazione Condizione Meteo "+condizioneMeteo.getId()+" alla Mareggiata "+idMareggiata);
					mareggiataManager.addCondizioneMeteo(idMareggiata, condizioneMeteo.getId());
				}
			}

			for (ImpattoReale impattoReale : filtered_impattiReali) {
				if (checkedImpattiReali.get(impattoReale.getId())) {
					logger.info("Aggiungo relazione Impatto Reale "+impattoReale.getId()+" alla Mareggiata "+idMareggiata);
					mareggiataManager.addImpattoReale(idMareggiata, impattoReale.getId());
				}
			}

			for (RelazioneSTB relazioneTecnica : filtered_relazioniSTB) {
				if (checkedRelazioniSTB.get(relazioneTecnica.getId())) {
					logger.info("Aggiungo relazione Tecnica STB "+relazioneTecnica.getId()+" alla Mareggiata "+idMareggiata);
					mareggiataManager.addRelazioneSTB(idMareggiata, relazioneTecnica.getId());
				}
			}

			// GESTIONE ALLEGATI
			if (!allegati.getUploadedFiles().isEmpty()) {
				Allegato allegato;
				String nomeFile;
				String nomeTipoAllegato;
				TipoAllegato tipoAllegato;
				for (Map.Entry<String, File> entry : allegati.getUploadedFiles().entrySet()) {
					nomeFile = entry.getKey();
					allegato = new Allegato();
					allegato.setNome(nomeFile);
					nomeTipoAllegato = allegati.getTipiAllegatiUploadedFiles().get(nomeFile);
					tipoAllegato = tipiAllegato.get(nomeTipoAllegato);
					allegato.setTipo(tipoAllegato);
					allegato.setFile(FileUtils.readFileToByteArray(entry.getValue()));
					
					mareggiataManager.insertAllegato(idMareggiata, allegato, tipoAllegato.getNome());
				}
			}

	}
}
