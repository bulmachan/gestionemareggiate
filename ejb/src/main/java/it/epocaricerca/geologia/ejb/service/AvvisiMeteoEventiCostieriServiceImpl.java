package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;

@Stateless
@Remote
public class AvvisiMeteoEventiCostieriServiceImpl implements AvvisiMeteoEventiCostieriService {


	@EJB
	private PrevisioneMeteoManager previsioneMeteoManager;

	public void salvaAvvisoMeteo(boolean editMode, 	PrevisioneMeteo pm, 
								String pianuraDirezioneVento,String pedemontanaDirezioneVento,String tendenza, String altezzaStimataOnda, String direzioneProvStimataOnda, 
								List<EventoCostiero> eventiCostieriToAddList, List<EventoCostiero> eventiCostieriToRemoveList, 
								Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati,  List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, IOException {

		Long previsioneId;


		if(!editMode){


			previsioneId = previsioneMeteoManager.create(pm, pianuraDirezioneVento, pedemontanaDirezioneVento, tendenza, altezzaStimataOnda, direzioneProvStimataOnda);
		}
		else{

			previsioneId = previsioneMeteoManager.update(pm, pianuraDirezioneVento, pedemontanaDirezioneVento, tendenza, altezzaStimataOnda, direzioneProvStimataOnda);
		}
		// se sono in edit mode prima rimuovo quelli rimossi
		if (editMode && null!=eventiCostieriToRemoveList && !eventiCostieriToRemoveList.isEmpty()) {
			for (EventoCostiero ecToRemove : eventiCostieriToRemoveList) {
				previsioneMeteoManager.removeEventoCostiero(previsioneId, ecToRemove.getId());
			}
		}

		for (EventoCostiero ec : eventiCostieriToAddList) {
			if (ec.getId() < 1){
				String direz=null;
				if(ec.getDirezione()!=null)
					direz=ec.getDirezione().getNome();
					
				previsioneMeteoManager.insertEventoCostiero(previsioneId, ec, direz, ec.getMacroArea().getNome(), ec.getFenomeno().getNome());
				
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

				
				previsioneMeteoManager.insertAllegato(previsioneId, allegato, tipoAllegato.getNome());
			}
		}

		if(editMode && !attachmentsToRemoveList.isEmpty())
		{
			for (Allegato allegato : attachmentsToRemoveList) {
				previsioneMeteoManager.removeAllegato(previsioneId, allegato.getId());
			}
		}			
	}
}
