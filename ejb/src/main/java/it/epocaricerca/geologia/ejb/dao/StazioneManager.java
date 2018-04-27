package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Stazione;

public interface StazioneManager {

	
	Long create(Stazione stazione);
	
	Long update(Stazione stazione);
	
	Stazione findItemById(long stazioneId);
	
	List<Stazione> findItemBySorgente(String sorgente);
	
	Long remove(long stazioneId) throws EntityNotFoundException;

}
