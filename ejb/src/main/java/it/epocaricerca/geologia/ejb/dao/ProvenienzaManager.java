package it.epocaricerca.geologia.ejb.dao;

import java.util.List;

import it.epocaricerca.geologia.model.Provenienza;

public interface ProvenienzaManager {

	Provenienza findItemById(long id);
	
	Provenienza findItemByName(String name);
	
	List selectAll();
}
