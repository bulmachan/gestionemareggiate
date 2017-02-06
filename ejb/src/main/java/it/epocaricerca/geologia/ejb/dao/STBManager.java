package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.STB;

import java.util.List;

public interface STBManager {

	STB findItemById(long id);
	STB findItemByName(String name);
	List<STB> selectAll();
}
