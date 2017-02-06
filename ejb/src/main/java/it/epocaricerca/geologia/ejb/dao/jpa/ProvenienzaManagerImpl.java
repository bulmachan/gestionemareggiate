package it.epocaricerca.geologia.ejb.dao.jpa;

import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.model.Provenienza;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@Remote
public class ProvenienzaManagerImpl extends GenericManager implements ProvenienzaManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	
	public Provenienza findItemById(long id) {
		return super.findItemById(Provenienza.class, em, id);
	}

	public Provenienza findItemByName(String name) {
		
		//logger.info("FRA: ---"+name+"---");
		
		Query q = em.createQuery("SELECT p FROM Provenienza p WHERE p.nome = ?1");
		q.setParameter(1, name);
		return (Provenienza) q.getSingleResult();
	}

	public List selectAll() {
		Query q = em.createQuery("SELECT p FROM Provenienza p ORDER BY p.nome");
		return q.getResultList();
	}

}
