package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

import java.util.List;

public interface VariabileManager {

	Long create(VariabileStazione variabile);
	
	Long update(VariabileStazione variabile);
	
	VariabileStazione findItemByCodice(String codice);
	
	Long remove(long variabileId) throws EntityNotFoundException;
	
	VariabileStazione findItemById(long variabileId);
}
