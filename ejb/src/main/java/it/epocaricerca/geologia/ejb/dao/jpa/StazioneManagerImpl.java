package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Fenomeno;
import it.epocaricerca.geologia.model.Stazione;

@Stateless
@Remote
public class StazioneManagerImpl extends GenericManager implements StazioneManager {

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Long create(Stazione stazione) {
	
		em.persist(stazione);
		return stazione.getId();
	}

	public Long update(Stazione stazione) {
		em.merge(stazione);
		return stazione.getId();
	}

	public Stazione findItemById(long stazioneId) {
		return super.findItemById(Stazione.class, em, stazioneId);
	}

	public List<Stazione> findItemBySorgente(String sorgente) {
		Query q = em.createQuery("SELECT s FROM Stazione s WHERE s.sorgente_dati = ?1");
		q.setParameter(1, sorgente);
		return q.getResultList();
	}

	public Long remove(long stazioneId) throws EntityNotFoundException {
		Stazione stazione = findItemById(stazioneId);
		if(stazione == null)
			throw new EntityNotFoundException("stazione == null");
		
		em.remove(stazione);
		return stazione.getId();
		
	}

}
