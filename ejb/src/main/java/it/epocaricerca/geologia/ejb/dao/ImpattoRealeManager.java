package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import com.vividsolutions.jts.io.ParseException;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;

public interface ImpattoRealeManager {

	Long create(ImpattoReale impatto, String fonte) throws EntityNotFoundException;
		
	Long update(ImpattoReale impatto, String fonte) throws EntityNotFoundException;

	ImpattoReale findItemById(long id);

	Long remove(long idImpattoReale) throws EntityNotFoundException, Exception;
	
	Long insertAllegato(Long idImpatto, Allegato allegato) throws EntityNotFoundException;
	
	void removeAllegato(Long idImpatto, Long idAllegato) throws EntityNotFoundException;

	// Gestione Dati di Danno
	Long insertDanno(long idImpattoReale, Danno danno, String nomeLocalita, String tipo,String geometryText) throws EntityNotFoundException, ParseException, Exception;

	List<Danno> getDanni(long idImpattoReale) throws EntityNotFoundException;

	Long removeDanno(long idImpattoReale, long idDanno) throws EntityNotFoundException, Exception;
}
