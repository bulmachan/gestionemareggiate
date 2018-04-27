package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface MareggiateControllerService {

	
	 void salvaMareggiata(Mareggiata mareggiata,FileUploadBean allegati, Map<String, TipoAllegato> tipiAllegato,
			List<PrevisioneMeteo> filtered_previsioniMeteo,
			List<PrevisioneImpatto> filtered_previsioniImpatti, List<ImpattoReale> filtered_impattiReali,
			List<CondizioneMeteo> filtered_condizioniMeteo,List<RelazioneSTB> filtered_relazioniSTB,
			Map<Long, Boolean> checkedRelazioniSTB, Map<Long, Boolean> checkedPrevisioniMeteo ,
			Map<Long, Boolean> checkedImpattiReali, Map<Long, Boolean> checkedCondizioniMeteo,
			Map<Long, Boolean> checkedPrevisioniImpatti) throws EntityNotFoundException, IOException;

}
