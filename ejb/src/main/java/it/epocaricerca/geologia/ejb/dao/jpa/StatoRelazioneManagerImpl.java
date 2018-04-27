package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import it.epocaricerca.geologia.ejb.dao.StatoRelazioneManager;
import it.epocaricerca.geologia.model.StatoRelazione;

@Stateless
@Remote
public class StatoRelazioneManagerImpl extends GenericManager implements StatoRelazioneManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public StatoRelazione findItemById(long id) {
		return super.findItemById(StatoRelazione.class, em, id);
	}

	public StatoRelazione findItemByName(String name) {
		Query q = em.createQuery("SELECT t FROM StatoRelazione t WHERE t.nome = ?1");
		q.setParameter(1, name);
		return (StatoRelazione) q.getSingleResult();
	}

	public List<StatoRelazione> selectAll() {
		Query q = em.createQuery("SELECT t FROM StatoRelazione t");
		return q.getResultList();
	}

}
