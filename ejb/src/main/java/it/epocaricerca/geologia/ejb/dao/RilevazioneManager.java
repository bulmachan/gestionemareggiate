package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Rilevazione;

public interface RilevazioneManager {

	Long create(List<DatoSensore> dati, long idVariabile, long idStazione,double min, double max) throws EntityNotFoundException;
	Long remove(long idRilevazione) throws EntityNotFoundException;
	Rilevazione findItemById(long id);
	List<DatoSensore> findDatiById(long id);
	
	Long update(long idRilevazione,List<DatoSensore> dati, double min, double max)throws EntityNotFoundException;
}
