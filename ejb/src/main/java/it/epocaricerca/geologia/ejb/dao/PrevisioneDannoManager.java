package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.PrevisioneDanno;

public interface PrevisioneDannoManager {

	Long remove(long previsioneDannoId) throws EntityNotFoundException;

	PrevisioneDanno findItemById(Long previsioneDannoId);

}
