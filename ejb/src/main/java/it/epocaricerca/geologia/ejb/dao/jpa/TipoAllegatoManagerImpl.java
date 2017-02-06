package it.epocaricerca.geologia.ejb.dao.jpa;

import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.model.TipoAllegato;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@Remote
public class TipoAllegatoManagerImpl extends GenericManager implements TipoAllegatoManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public TipoAllegato findItemById(long id) {
		return super.findItemById(TipoAllegato.class, em, id);
	}

	public TipoAllegato findItemByName(String name) {
		Query q = em.createQuery("SELECT t FROM TipoAllegato t WHERE t.nome = ?1");
		q.setParameter(1, name);
		return (TipoAllegato) q.getSingleResult();
	}

	public List<TipoAllegato> selectAll() {
		Query q = em.createQuery("SELECT t FROM TipoAllegato t  ORDER BY t.nome");
		return q.getResultList();
	}

}
