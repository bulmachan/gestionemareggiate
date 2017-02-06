package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.model.Tendenza;



public interface TendenzaManager {

	Tendenza findItemById(long id);
	Tendenza findItemByName(String name);
	List<Tendenza> selectAll();
	
}
