package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface AvvisiMeteoEventiCostieriService {

	
	void salvaAvvisoMeteo(boolean editMode, PrevisioneMeteo pm, 
			String pianuraDirezioneVento,String pedemontanaDirezioneVento,String tendenza, String altezzaStimataOnda, String direzioneProvStimataOnda,
			List<EventoCostiero> eventiCostieriToAddList, List<EventoCostiero> eventiCostieriToRemoveList, 
			Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati,  List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, IOException;

}
