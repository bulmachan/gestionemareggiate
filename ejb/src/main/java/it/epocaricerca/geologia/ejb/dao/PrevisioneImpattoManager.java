package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;

import java.util.List;

import com.vividsolutions.jts.io.ParseException;

public interface PrevisioneImpattoManager {

	Long create(PrevisioneImpatto impatto) throws EntityNotFoundException;

	PrevisioneImpatto findItemById(long id);
	
	public Long update(PrevisioneImpatto impatto);

	Long remove(long idPrevisioneImpatto ) throws EntityNotFoundException, Exception;

	// Gestione Dati di Danno
	Long insertDanno(long idPrevisioneImpatto, PrevisioneDanno danno) throws EntityNotFoundException, ParseException, Exception;

	List<PrevisioneDanno> getDanni(long idPrevisioneImpatto ) throws EntityNotFoundException;

	Long removeDanno(long idPrevisioneImpatto, long idDanno) throws EntityNotFoundException, Exception;

	Long insertAllegato(Long previsioneImpattoId, Allegato allegato) throws EntityNotFoundException;

	void removeAllegato(Long previsioneImpattoId, Long idAllegato) throws EntityNotFoundException;
}
