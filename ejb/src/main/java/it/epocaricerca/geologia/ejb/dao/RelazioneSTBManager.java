package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;

import java.util.List;

import com.vividsolutions.jts.io.ParseException;

public interface RelazioneSTBManager {

	Long create(RelazioneSTB relazioneSTB, String STB, String fonte) throws EntityNotFoundException;
	
	Long update(RelazioneSTB relazione,String STB, String fonte) throws EntityNotFoundException;
 
	Long updateStato(long idRelazione, String statoRelazione) throws EntityNotFoundException;

	Long remove(long idRelazione) throws EntityNotFoundException, Exception;

	RelazioneSTB findItemById(long idRelazione);


	// Gestione Danni STB
	Long insertDannoSTB(long idRelazione, DannoSTB danno, String localita, String geometryText) throws EntityNotFoundException, ParseException, Exception;

	Long removeDannoSTB(long idRelazione, long idDanno) throws EntityNotFoundException, Exception;

	List<DannoSTB> getDanniSTB(long idRelazione) throws EntityNotFoundException;

	Long insertAllegato(Long idRelazioneTecnica, Allegato allegato) throws EntityNotFoundException;

	void removeAllegato(Long idRelazione, Long idAllegato) throws EntityNotFoundException;

 
}
