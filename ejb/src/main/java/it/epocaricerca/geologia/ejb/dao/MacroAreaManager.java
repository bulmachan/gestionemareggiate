package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.MacroArea;
import java.util.List;

public interface MacroAreaManager {

	MacroArea findItemById(long id);
	MacroArea findItemByName(String name);
	List<MacroArea> selectAll();
}
