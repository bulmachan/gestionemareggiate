package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.Fenomeno;
import java.util.List;

public interface FenomenoManager {

	Fenomeno findItemById(long id);
	Fenomeno findItemByName(String name);
	List<Fenomeno> selectAll();
}
