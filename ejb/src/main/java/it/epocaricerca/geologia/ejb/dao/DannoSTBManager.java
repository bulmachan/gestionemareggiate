package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public interface DannoSTBManager {

	Long create(DannoSTB danno, String nomeLocalita , String geometry) throws EntityNotFoundException, ParseException, Exception;

	Long remove(long idDannoSTB) throws EntityNotFoundException, Exception;

	DannoSTB findItemById(long id);



	// Gestione Allegati

	Long insertAllegato(long idDannoSTB, Allegato allegato, String tipoAllegato) throws EntityNotFoundException;

	List<Allegato> getAllegati(long idDannoSTB) throws EntityNotFoundException;

	Long removeAllegato(long idDannoSTB, long idAllegato) throws EntityNotFoundException;


	//Gestione geometria

	List<Geometry> getGeometry(long idDanno)throws EntityNotFoundException, Exception;
}
