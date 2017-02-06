package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.AltezzaManager;
import it.epocaricerca.geologia.model.Altezza;

@Stateless
@Remote
public class AltezzaManagerImpl extends GenericManager implements AltezzaManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Altezza findItemById(long id) {
		return super.findItemById(Altezza.class, em, id);
	}

	public Altezza findItemByName(String name) {
		
		Query q = em.createQuery("SELECT a FROM Altezza a WHERE a.nome = ?1");
		q.setParameter(1, name);
		return (Altezza) q.getSingleResult();
	}

	public List<Altezza> selectAll() {
		Query q = em.createQuery("SELECT a FROM Altezza a ORDER BY a.nome");
		return q.getResultList();
	}

}
