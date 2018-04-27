package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;


import java.util.List;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;

public interface DannoManager {


	Long create(Danno danno, String nomeLocalita, String tipoDanno, String geometry) throws EntityNotFoundException, ParseException, Exception;

	Long remove(long idDanno) throws EntityNotFoundException, Exception;

	Danno findItemById(long id);



	// Gestione Allegati
	Long insertAllegato(long idDanno, Allegato allegato, String tipoAllegato) throws EntityNotFoundException;

	List<Allegato> getAllegati(long idDanno) throws EntityNotFoundException;

	Long removeAllegato(long idDanno, long idAllegato) throws EntityNotFoundException;
	
	
	//Gestione geometria
	
	List<Geometry> getGeometry(long idDanno)throws EntityNotFoundException, Exception;
}
