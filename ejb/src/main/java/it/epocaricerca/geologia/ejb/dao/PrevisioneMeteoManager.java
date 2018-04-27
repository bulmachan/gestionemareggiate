package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.PrevisioneMeteo;


public interface PrevisioneMeteoManager {

	Long create(PrevisioneMeteo previsione, String provenienzaVentoPianura, String provenienzaVentoPedemontana, String tendenza, String altezza, String direzione) throws EntityNotFoundException;
	
	Long update(PrevisioneMeteo previsione, String provenienzaVentoPianura, String provenienzaVentoPedemontana, String tendenza, String altezza, String direzione) throws EntityNotFoundException;
	
	Long remove(long idPrevisione) throws EntityNotFoundException;
	
	PrevisioneMeteo findItemById(long id);
	
	
	
	// Gestione Eventi Costieri
	Long insertEventoCostiero(long idPrevisione, EventoCostiero eventoCostiero, String direzioneEventoCostiero, String macroArea, String fenomeno) throws EntityNotFoundException;
	
	Long removeEventoCostiero(long idPrevisione,long idEventoCostiero) throws EntityNotFoundException;
	
	List<EventoCostiero> getEventiCostieri(long idPrevisione) throws EntityNotFoundException;
	
	// Gestione Allegati
	Long insertAllegato(long idPrevisione, Allegato allegato, String tipoAllegato) throws EntityNotFoundException;
	
	List<Allegato> getAllegati(long idPrevisione) throws EntityNotFoundException;
	
	Long removeAllegato(long idPrevisioneMeteo, long idAllegato) throws EntityNotFoundException;
	

	
}
