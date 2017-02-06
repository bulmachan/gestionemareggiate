package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.vividsolutions.jts.io.ParseException;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneDannoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.PrevisioneDanno;
import it.epocaricerca.geologia.model.PrevisioneImpatto;

@Stateless
@Remote
public class PrevisioneImpattoManagerImpl extends GenericManager implements PrevisioneImpattoManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private PrevisioneDannoManager dannoCRUD;

	public Long create(PrevisioneImpatto impatto) throws EntityNotFoundException {
		//em.persist(impatto);
		return em.merge(impatto).getId();
	}

	public Long update(PrevisioneImpatto impatto) {
		em.merge(impatto);
		return impatto.getId();
	}
	
	public PrevisioneImpatto findItemById(long id) {
		return super.findItemById(PrevisioneImpatto.class, em, id);
	}

	public Long remove(long idPrevisioneImpatto) throws Exception {
		PrevisioneImpatto previsioneImpatto = this.findItemById(idPrevisioneImpatto);
		if(previsioneImpatto == null)
			throw new EntityNotFoundException(""+idPrevisioneImpatto);
		
		List<PrevisioneDanno> danni = previsioneImpatto.getDanni();
		
		for (PrevisioneDanno danno : danni) {
			dannoCRUD.remove(danno.getId());
		}
		
		danni.clear();
		
		em.remove(previsioneImpatto);
		return idPrevisioneImpatto;
	}

	public Long insertDanno(long idPrevisioneImpatto, PrevisioneDanno danno) throws ParseException, Exception {
		PrevisioneImpatto previsioneImpatto = this.findItemById(idPrevisioneImpatto);
		if(previsioneImpatto == null)
			throw new EntityNotFoundException(""+idPrevisioneImpatto);

		
		em.persist(danno);

		previsioneImpatto.getDanni().add(danno);
		em.merge(previsioneImpatto);

		return danno.getId();
	}

	public List<PrevisioneDanno> getDanni(long idPrevisioneImpatto) throws EntityNotFoundException {
		if(this.findItemById(idPrevisioneImpatto) == null)
			throw new EntityNotFoundException("PrevisioneImpatto "+idPrevisioneImpatto);

		Query q = em.createQuery("SELECT ir.danni FROM PrevisioneImpatto ir WHERE ir.id = ?1");
		q.setParameter(1, idPrevisioneImpatto);
		return q.getResultList();
	}

	public Long removeDanno(long idPrevisioneImpatto, long idDanno)throws Exception {
		PrevisioneImpatto previsioneImpatto = this.findItemById(idPrevisioneImpatto);
		if(previsioneImpatto == null)
			throw new EntityNotFoundException(""+idPrevisioneImpatto);

		Danno danno = super.findItemById(Danno.class, em, idDanno);
		if(danno == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		boolean removed = previsioneImpatto.getDanni().remove(danno);

		if(removed)
		{
			em.merge(previsioneImpatto);
			dannoCRUD.remove(idDanno);
			return danno.getId();
		}

		return -1L;
	}

	public Long insertAllegato(Long idPrevisioneImpatto, Allegato allegato) throws EntityNotFoundException {
		PrevisioneImpatto previsioneImpatto = this.findItemById(idPrevisioneImpatto);
		if(previsioneImpatto == null)
			throw new EntityNotFoundException(""+idPrevisioneImpatto);
		
		previsioneImpatto.getAllegati().add(allegato);
		em.merge(previsioneImpatto);
		return allegato.getId();
	}

	public void removeAllegato(Long idPrevisioneImpatto, Long idAllegato) throws EntityNotFoundException {
		PrevisioneImpatto previsioneImpatto = this.findItemById(idPrevisioneImpatto);
		if(previsioneImpatto == null)
			throw new EntityNotFoundException(""+idPrevisioneImpatto);
		
		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);
		
		boolean removed = previsioneImpatto.getAllegati().remove(allegato);
		
		if(removed)
		{
			em.merge(previsioneImpatto);
			em.remove(allegato);
		}
		
	}


}
