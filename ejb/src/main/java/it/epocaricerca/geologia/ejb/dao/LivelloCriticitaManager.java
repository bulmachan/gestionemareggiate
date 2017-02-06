package it.epocaricerca.geologia.ejb.dao;

import java.util.List;
import it.epocaricerca.geologia.model.LivelloCriticita;



public interface LivelloCriticitaManager {

	LivelloCriticita findItemById(long id);
	LivelloCriticita findItemByName(String name);
	List<LivelloCriticita> selectAll();
	
}
