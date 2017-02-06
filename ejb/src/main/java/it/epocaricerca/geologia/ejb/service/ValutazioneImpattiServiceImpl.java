package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.PrevisioneDannoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;

@Stateless
@Remote
public class ValutazioneImpattiServiceImpl implements ValutazioneImpattiService {

	@EJB
	PrevisioneImpattoManager previsioneImpattoManager;

	@EJB
	PrevisioneDannoManager previsioneDannoManager;

	public void salvaValutazioneImpatti(boolean editMode, PrevisioneImpatto impatto, List<PrevisioneDanno> tipologieDannoToRemove, 
											Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati, 
											List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, IOException {

		Long previsioneImpattoId = null;
		if (editMode) {
	
			previsioneImpattoId = previsioneImpattoManager.update(impatto);

			for (PrevisioneDanno previsioneDanno : tipologieDannoToRemove) {
				previsioneDannoManager.remove(previsioneDanno.getId());
			}
		} 
		else {
					
			previsioneImpattoId = previsioneImpattoManager.create(impatto);
			System.out.println("Previsione Impatto creata "+previsioneImpattoId);
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

			
				previsioneImpattoManager.insertAllegato(previsioneImpattoId, allegato);
			}
		}

		if(editMode && !attachmentsToRemoveList.isEmpty())
		{
			for (Allegato allegato : attachmentsToRemoveList) {
				previsioneImpattoManager.removeAllegato(previsioneImpattoId, allegato.getId());
			}
		}




	}
}
