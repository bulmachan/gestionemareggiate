package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.CondizioneMeteoBean;
import it.epocaricerca.geologia.ejb.tdo.DatiArpaBean;
import it.epocaricerca.geologia.model.Rilevazione;

import java.util.List;
import java.util.Map;

public interface CondizioniMeteoMarineService {

	void salvaCondizioneMeteo(boolean editMode, CondizioneMeteoBean meteoBeanEdit,List<Rilevazione> rilevazioni,
			Map<DatiArpaBean, Boolean> checkedDatiArpaBean, Map<DatiArpaBean, Boolean> datiMareografoBeanToRemove,
			Map<String,DatiArpaBean> datiMareografo) throws EntityNotFoundException;
}
