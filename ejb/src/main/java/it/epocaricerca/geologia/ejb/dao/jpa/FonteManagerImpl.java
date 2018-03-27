package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.model.Fonte;



@Stateless
@Remote
public class FonteManagerImpl extends GenericManager implements FonteManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Fonte findItemById(long id) {
		return super.findItemById(Fonte.class, em, id);
	}

	public Fonte findItemByName(String name) {
		Query q = em.createQuery("SELECT f FROM Fonte f WHERE f.nome = ?1");
		q.setParameter(1, name);
		return (Fonte) q.getSingleResult();
	}

	public List<Fonte> selectAll() {
		Query q = em.createQuery("SELECT f FROM Fonte f");
		return q.getResultList();
	}

}
