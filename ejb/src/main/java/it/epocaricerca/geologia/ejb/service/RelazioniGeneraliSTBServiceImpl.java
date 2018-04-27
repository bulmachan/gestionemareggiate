package it.epocaricerca.geologia.ejb.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.RelazioneGeneraleSTBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;


@Stateless
@Remote
public class RelazioniGeneraliSTBServiceImpl implements RelazioniGeneraliSTBService {

	private static Logger logger = Logger.getLogger(RelazioniGeneraliSTBServiceImpl.class);



	@EJB
	RelazioneGeneraleSTBManager stbGeneraleManager;


	public void salvaRelazioneGenerale(	RelazioneGeneraleSTBean relazioneGeneraleEdit,List<RelazioneSTB> filtered_relazioniSTB,Map<Long, Boolean> checkedRelazioni,
			FileUploadBean allegati,Map<String, TipoAllegato> tipiAllegato) throws EntityNotFoundException, IOException
			{

		RelazioneGeneraleSTB temp = new RelazioneGeneraleSTB();
		temp.setData(relazioneGeneraleEdit.getData());
		temp.setInformazioniGenerali(relazioneGeneraleEdit.getInformazioniGenerali());
		temp.setInformazioniMeteo(relazioneGeneraleEdit.getInformazioniMeteo());


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
				temp.getAllegati().add(allegato);
			}
		}

		long idRelazioneGeneraleSTB = stbGeneraleManager.create(temp);


		for (RelazioneSTB relazioneTecnica : filtered_relazioniSTB) {
			if (checkedRelazioni.get(relazioneTecnica.getId())) {
				logger.info("Aggiungo relazione Tecnica "+relazioneTecnica.getId()+" alla relazione Generale "+idRelazioneGeneraleSTB);
				stbGeneraleManager.addRelazioneSTB(idRelazioneGeneraleSTB, relazioneTecnica.getId());
			}
		}





			}
}
