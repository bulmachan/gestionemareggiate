package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.apache.log4j.Logger;
import it.epocaricerca.geologia.ejb.dao.IndirizzoManager;
import it.epocaricerca.geologia.model.Indirizzo;

@Stateless
@Remote
public class IndirizzoManagerImpl extends GenericManager implements IndirizzoManager {

	
	private static Logger logger = Logger.getLogger(IndirizzoManagerImpl.class);

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Indirizzo findItemById(long id) {
		return super.findItemById(Indirizzo.class, em, id);
	}

	public List<Indirizzo> selectAll() {
		Query q = em.createQuery("SELECT a FROM Indirizzo a");
		return q.getResultList();
	}

}
