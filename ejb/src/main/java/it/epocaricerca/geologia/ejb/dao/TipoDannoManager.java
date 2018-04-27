package it.epocaricerca.geologia.ejb.dao;


import it.epocaricerca.geologia.model.TipoDanno;

import java.util.List;


public interface TipoDannoManager {

	TipoDanno findItemById(long id);
	TipoDanno findItemByName(String name);
	List<TipoDanno> selectAll();
}
