package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.RelazioneGeneraleSTBean;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface RelazioniGeneraliSTBService {

	void salvaRelazioneGenerale(	RelazioneGeneraleSTBean relazioneGeneraleEdit,List<RelazioneSTB> filtered_relazioniSTB,Map<Long, Boolean> checkedRelazioni,
			FileUploadBean allegati,Map<String, TipoAllegato> tipiAllegato) throws EntityNotFoundException, IOException;
}
