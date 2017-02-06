package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.StatoRelazione;
import java.util.List;

public interface StatoRelazioneManager {

	StatoRelazione findItemById(long id);
	StatoRelazione findItemByName(String name);
	List<StatoRelazione> selectAll();
}
