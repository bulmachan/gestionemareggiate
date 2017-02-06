package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.Fonte;

import java.util.List;

public interface FonteManager {

	Fonte findItemById(long id);
	Fonte findItemByName(String name);
	List<Fonte> selectAll();
}
