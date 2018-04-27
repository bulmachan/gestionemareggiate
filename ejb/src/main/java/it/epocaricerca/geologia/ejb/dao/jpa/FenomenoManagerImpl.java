package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.FenomenoManager;
import it.epocaricerca.geologia.model.Fenomeno;


@Stateless
@Remote
public class FenomenoManagerImpl extends GenericManager implements FenomenoManager {

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Fenomeno findItemById(long id) {
		return super.findItemById(Fenomeno.class, em, id);
	}

	public Fenomeno findItemByName(String name) {
		Query q = em.createQuery("SELECT f FROM Fenomeno f WHERE f.nome = ?1");
		q.setParameter(1, name);
		return (Fenomeno) q.getSingleResult();
	}

	public List<Fenomeno> selectAll() {
		Query q = em.createQuery("SELECT f FROM Fenomeno f ORDER BY f.nome");
		return q.getResultList();
	}

}
