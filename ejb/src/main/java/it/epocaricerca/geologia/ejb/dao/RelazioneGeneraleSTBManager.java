package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;

public interface RelazioneGeneraleSTBManager {

	Long create(RelazioneGeneraleSTB relazioneGenerale) throws EntityNotFoundException;
	
	Long updateStato(long idRelazioneGenerale, String statoRelazione) throws EntityNotFoundException;
	
	Long remove(long idRelazioneGenerale) throws EntityNotFoundException;
	
	RelazioneGeneraleSTB findItemById(long idRelazioneGenerale);
	
	
	// Gestione Relazioni STB
	Long addRelazioneSTB(long idRelazioneGenerale, long idRelazione) throws EntityNotFoundException;
	
	List<RelazioneSTB> getRelazioniSTB(long idRelazioneGenerale) throws EntityNotFoundException;

	Long removeRelazioneSTB(long idRelazioneGenerale, long idRelazione) throws EntityNotFoundException;
	
}
