package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.LocalitaManager;
import it.epocaricerca.geologia.model.Localita;



@Stateless
@Remote
public class LocalitaManagerImpl extends GenericManager implements LocalitaManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Localita findItemById(long id) {
		return super.findItemById(Localita.class, em, id);
	}

	public Localita findItemByName(String name) {
		Query q = em.createQuery("SELECT l FROM Localita l WHERE l.nome = ?1");
		q.setParameter(1, name);
		return (Localita) q.getSingleResult();
	}

	public List<Localita> selectAll() {
		Query q = em.createQuery("SELECT l FROM Localita l ORDER BY l.nome");
		return q.getResultList();
	}

}
