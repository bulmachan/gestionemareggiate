package it.epocaricerca.geologia.ejb.dao.jpa;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.VariabileStazione;

@Stateless
@Remote
public class VariabileManagerImpl extends GenericManager implements VariabileManager{

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Long create(VariabileStazione variabile) {
		em.persist(variabile);
		return variabile.getId();
	}

	public Long update(VariabileStazione variabile) {
		em.merge(variabile);
		return variabile.getId();

	}
	
	
	public VariabileStazione findItemById(long variabileId) {
		return super.findItemById(VariabileStazione.class, em, variabileId);
	}


	public VariabileStazione findItemByCodice(String codice) {
		Query q = em.createQuery("SELECT s FROM VariabileStazione s WHERE s.codice = ?1");
		q.setParameter(1, codice);
		return (VariabileStazione) q.getSingleResult();
	}

	public Long remove(long variabileId) throws EntityNotFoundException {
		VariabileStazione variabile = findItemById(variabileId);
		if(variabile == null)
			throw new EntityNotFoundException("variabile == null");
		
		em.remove(variabile);
		return variabile.getId();
	}

}
