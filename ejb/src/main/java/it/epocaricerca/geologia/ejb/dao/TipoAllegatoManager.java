package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.TipoAllegato;

import java.util.List;

public interface TipoAllegatoManager {

	TipoAllegato findItemById(long id);
	TipoAllegato findItemByName(String name);
	List<TipoAllegato> selectAll();
}
