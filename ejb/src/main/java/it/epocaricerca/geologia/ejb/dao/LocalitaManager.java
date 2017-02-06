package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.Localita;

import java.util.List;

public interface LocalitaManager {

	Localita findItemById(long id);
	Localita findItemByName(String name);
	List<Localita> selectAll();
}
