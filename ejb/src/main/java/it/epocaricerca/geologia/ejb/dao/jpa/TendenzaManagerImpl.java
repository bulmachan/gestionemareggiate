package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.TendenzaManager;
import it.epocaricerca.geologia.model.Tendenza;

@Stateless
@Remote
public class TendenzaManagerImpl extends GenericManager implements TendenzaManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Tendenza findItemById(long id) {
		return super.findItemById(Tendenza.class, em, id);
	}

	public Tendenza findItemByName(String name) {
		
		Query q = em.createQuery("SELECT t FROM Tendenza t WHERE t.nome = ?1");
		q.setParameter(1, name);
		return (Tendenza) q.getSingleResult();
	}

	public List<Tendenza> selectAll() {
		Query q = em.createQuery("SELECT t FROM Tendenza t");
		return q.getResultList();
	}

}
