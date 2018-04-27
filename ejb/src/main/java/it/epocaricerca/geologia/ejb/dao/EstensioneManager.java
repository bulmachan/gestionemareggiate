package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.Estensione;
import java.util.List;

public interface EstensioneManager {

	Estensione findItemById(long id);
	Estensione findItemByName(String name);
	List<Estensione> selectAll();
}
