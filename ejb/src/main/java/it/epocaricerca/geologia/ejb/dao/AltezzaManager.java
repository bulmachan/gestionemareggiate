package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.model.Altezza;



public interface AltezzaManager {

	Altezza findItemById(long id);
	Altezza findItemByName(String name);
	List<Altezza> selectAll();
	
}
