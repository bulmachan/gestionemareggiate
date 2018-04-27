package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import it.epocaricerca.geologia.ejb.dao.STBManager;
import it.epocaricerca.geologia.model.STB;



@Stateless
@Remote
public class STBManagerImpl extends GenericManager implements STBManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public STB findItemById(long id) {
		return super.findItemById(STB.class, em, id);
	}

	public STB findItemByName(String name) {
		Query q = em.createQuery("SELECT t FROM STB t WHERE t.nome = ?1");
		q.setParameter(1, name);
		return (STB) q.getSingleResult();
	}

	public List<STB> selectAll() {
		Query q = em.createQuery("SELECT t FROM STB t");
		return q.getResultList();
	}

}
