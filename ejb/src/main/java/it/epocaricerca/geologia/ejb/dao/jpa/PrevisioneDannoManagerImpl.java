package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.Iterator;
import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.epocaricerca.geologia.ejb.dao.PrevisioneDannoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.Stazione;

@Stateless
@Remote
public class PrevisioneDannoManagerImpl  extends GenericManager implements PrevisioneDannoManager {

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Long remove(long id) throws EntityNotFoundException {
		PrevisioneDanno previsioneDanno = findItemById(id);
		if(previsioneDanno == null)
			throw new EntityNotFoundException("previsioneDanno == null");
		
		List<Allegato> allegati = previsioneDanno.getAllegati();
		
		
		for (Iterator iterator = allegati.iterator(); iterator.hasNext();) {
			Allegato allegato = (Allegato) iterator.next();
			em.remove(allegato);

		}
		allegati.clear();
		
		previsioneDanno.getTipiDanno().clear();
		
		em.remove(previsioneDanno);
		return previsioneDanno.getId();

	}

	public PrevisioneDanno findItemById(Long previsioneDannoId) {
		return super.findItemById(PrevisioneDanno.class, em, previsioneDannoId);
	}


}
