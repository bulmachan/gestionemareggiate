package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;

public interface MareggiataManager {

	Long create(Mareggiata mareggiata);

	Long remove(long idMareggiata) throws EntityNotFoundException;

	Mareggiata findItemById(long id);


	// Gestione Allegati
	Long insertAllegato(long idMareggiata, Allegato allegato, String tipoAllegato) throws EntityNotFoundException;

	List<Allegato> getAllegati(long idMareggiata) throws EntityNotFoundException;

	Long removeAllegato(long idMareggiata, long idAllegato) throws EntityNotFoundException;
	
	
	// Gestione previsioniMeteo
	
	Long addPrevisioneMeteo(long idMareggiata, long idPrevisioneMeteo ) throws EntityNotFoundException;

	List<PrevisioneMeteo> getPrevisioniMeteo(long idMareggiata) throws EntityNotFoundException;

	Long removePrevisioneMeteo(long idMareggiata, long idPrevisioneMeteo) throws EntityNotFoundException;
	
	
	
	// Gestione previsioniImpatti
	
	Long addPrevisioneImpatto(long idMareggiata, long idPrevisioneImpatto) throws EntityNotFoundException;

	List<PrevisioneImpatto> getPrevisioneImpatto(long idMareggiata) throws EntityNotFoundException;

	Long removePrevisioneImpatto(long idMareggiata, long idPrevisioneImpatto) throws EntityNotFoundException;
	
	// Gestione condizioniMeteo
	
	Long addCondizioneMeteo(long idMareggiata, long idCondizioneMeteo) throws EntityNotFoundException;

	List<CondizioneMeteo> getCondizioniMeteo(long idMareggiata) throws EntityNotFoundException;

	Long removeCondizioneMeteo(long idMareggiata, long idCondizioneMeteo) throws EntityNotFoundException;
	
	// Gestione impattiReali
	

	Long addImpattoReale(long idMareggiata, long idImpattoReale) throws EntityNotFoundException;

	List<ImpattoReale> getImpattiReali(long idMareggiata) throws EntityNotFoundException;

	Long removeImpattoReale(long idMareggiata, long idImpattoReale) throws EntityNotFoundException;
	
	// Gestione relazioniSTB
	
	Long addRelazioneSTB(long idMareggiata, long idRelazioneSTB) throws EntityNotFoundException;

	List<RelazioneSTB> getRelazioniSTB(long idMareggiata) throws EntityNotFoundException;

	Long removeRelazioneSTB(long idMareggiata, long idRelazioneSTB) throws EntityNotFoundException;
	

}
