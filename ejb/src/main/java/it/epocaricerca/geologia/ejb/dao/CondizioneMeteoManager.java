package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Rilevazione;

public interface CondizioneMeteoManager {

	Long create(CondizioneMeteo condizioneMeteo,String provenienza, String estensioneTerritoriale,String direzioneVentoPrevalente) throws EntityNotFoundException;
	
	Long update(CondizioneMeteo condizioneMeteo,String provenienza, String estensioneTerritoriale,String direzioneVentoPrevalente) throws EntityNotFoundException;
	
	CondizioneMeteo findItemById(long id);
	
	Long remove(long idCondizioneMeteo) throws EntityNotFoundException;
	
	
	// Gestione Dati di Marea
	Long insertRilevazione(long idCondizioneMeteo, List<DatoSensore> dati, long idVariabile, long idStazione,double min, double max) throws EntityNotFoundException;
	
	List<Rilevazione> getRilevazioni(long idCondizioneMeteo) throws EntityNotFoundException;
	
	Long removeRilevazione(long idCondizioneMeteo, long idRilevazioneMarea) throws EntityNotFoundException;
	

}
