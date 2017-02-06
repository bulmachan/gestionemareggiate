package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ValutazioneImpattiService {

	
	public void salvaValutazioneImpatti(boolean editMode, PrevisioneImpatto impatto, List<PrevisioneDanno> tipologieDannoToRemove, 
			Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati, 
			List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, IOException;

}
