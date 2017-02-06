package it.epocaricerca.geologia.ejb.dao.jpa;

import javax.persistence.EntityManager;
import org.apache.log4j.Logger;


public class GenericManager {

	protected static Logger logger = Logger.getLogger(GenericManager.class.getName());
	
	protected <T> T findItemById(Class<T> clazz, EntityManager em,Long id) {
		return em.find(clazz, id);
	}
	
	
	

}
