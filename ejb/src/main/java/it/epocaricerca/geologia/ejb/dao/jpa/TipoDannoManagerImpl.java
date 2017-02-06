package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.TipoDannoManager;
import it.epocaricerca.geologia.model.TipoDanno;


@Stateless
@Remote
public class TipoDannoManagerImpl extends GenericManager implements TipoDannoManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	
	public TipoDanno findItemById(long id) {
		return super.findItemById(TipoDanno.class, em, id);
	}

	public TipoDanno findItemByName(String name) {
		Query q = em.createQuery("SELECT t FROM TipoDanno t WHERE t.nome = ?1");
		q.setParameter(1, name);
		return (TipoDanno) q.getSingleResult();
	}

	public List<TipoDanno> selectAll() {
		Query q = em.createQuery("SELECT t FROM TipoDanno t ORDER BY t.nome");
		return q.getResultList();
	}

}
