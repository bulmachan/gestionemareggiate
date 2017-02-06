package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.model.Indirizzo;
import it.epocaricerca.geologia.model.LivelloCriticita;



public interface IndirizzoManager {

	Indirizzo findItemById(long id);
	List<Indirizzo> selectAll();
	
}
