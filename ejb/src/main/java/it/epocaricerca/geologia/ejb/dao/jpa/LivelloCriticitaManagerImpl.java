package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.LivelloCriticitaManager;
import it.epocaricerca.geologia.model.LivelloCriticita;

@Stateless
@Remote
public class LivelloCriticitaManagerImpl extends GenericManager implements LivelloCriticitaManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public LivelloCriticita findItemById(long id) {
		return super.findItemById(LivelloCriticita.class, em, id);
	}

	public LivelloCriticita findItemByName(String name) {
		
		Query q = em.createQuery("SELECT a FROM LivelloCriticita a WHERE a.nome = ?1");
		q.setParameter(1, name);
		return (LivelloCriticita) q.getSingleResult();
	}

	public List<LivelloCriticita> selectAll() {
		Query q = em.createQuery("SELECT a FROM LivelloCriticita a ORDER BY a.nome");
		return q.getResultList();
	}

}
