package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.RelazioneGeneraleSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.StatoRelazioneManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.RelazioneGeneraleSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.StatoRelazione;

@Stateless
@Remote
public class RelazioneGeneraleSTBManagerImpl  extends GenericManager implements RelazioneGeneraleSTBManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private StatoRelazioneManager statoRelazioneCRUD;
	
	@EJB
	private RelazioneSTBManager relazioneSTBCRUD;

	public Long create(RelazioneGeneraleSTB relazioneGenerale) throws EntityNotFoundException{

		StatoRelazione statoObj = statoRelazioneCRUD.findItemByName("Bozza");
		
		if(statoObj == null)
			throw new EntityNotFoundException("statoObj == null");
		
		relazioneGenerale.setStato(statoObj);
		em.persist(relazioneGenerale);
		return relazioneGenerale.getId();
	}

	public Long updateStato(long idRelazioneGenerale, String statoRelazione) throws EntityNotFoundException {

		RelazioneGeneraleSTB relazione = findItemById(idRelazioneGenerale);

		StatoRelazione statoObj = statoRelazioneCRUD.findItemByName(statoRelazione);

		if(relazione == null || statoObj == null)
			throw new EntityNotFoundException("relazione == null || statoObj == null");

		if(relazione.getStato().getNome().equals("Protocollato"))
			return -1L;

		if(relazione.getStato().getNome().equals("Definitivo") && !statoObj.getNome().equals("Protocollato") )
			return -1L;

		relazione.setStato(statoObj);
		em.merge(relazione);

		return relazione.getId();
	}

	public Long remove(long idRelazioneGenerale) throws EntityNotFoundException {
		RelazioneGeneraleSTB relazione = findItemById(idRelazioneGenerale);
		if(relazione == null)
			throw new EntityNotFoundException("RelazioneGeneraleSTB "+idRelazioneGenerale);
		
		String statoRelazione = relazione.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		
		List<RelazioneSTB> relazioniSTB = relazione.getRelazioniSTB();
		for (RelazioneSTB relazioneSTB : relazioniSTB) {
			relazioneSTB.setRelazioneGeneraleSTB(null);
			em.merge(relazioneSTB);
		}
		
		em.remove(relazione);
		
		return relazione.getId();
	}

	public RelazioneGeneraleSTB findItemById(long idRelazioneGenerale) {
		return super.findItemById(RelazioneGeneraleSTB.class,em, idRelazioneGenerale);
	}

	public Long addRelazioneSTB(long idRelazioneGenerale, long idRelazione) throws EntityNotFoundException {
		
		RelazioneGeneraleSTB relazioneGenerale = this.findItemById(idRelazioneGenerale);
		RelazioneSTB relazioneSTB = super.findItemById(RelazioneSTB.class, em, idRelazione);
		if(relazioneGenerale == null || relazioneSTB == null)
			throw new EntityNotFoundException("relazioneGenerale == null || relazioneSTB == null");
		
		String statoRelazione = relazioneGenerale.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		relazioneSTB.setRelazioneGeneraleSTB(relazioneGenerale);
		em.merge(relazioneSTB);
		
		relazioneGenerale.getRelazioniSTB().add(relazioneSTB);

		em.merge(relazioneGenerale);
		return relazioneSTB.getId();
	}

	public List<RelazioneSTB> getRelazioniSTB(long idRelazioneGenerale) throws EntityNotFoundException {
		if(this.findItemById(idRelazioneGenerale) == null)
			throw new EntityNotFoundException("RelazioneGeneraleSTB "+idRelazioneGenerale);
		
		Query q = em.createQuery("SELECT pm.relazioniSTB FROM RelazioneGeneraleSTB pm WHERE pm.id = ?1");
		q.setParameter(1, idRelazioneGenerale);
		return q.getResultList();
	}

	public Long removeRelazioneSTB(long idRelazioneGenerale, long idRelazione) throws EntityNotFoundException {
		RelazioneGeneraleSTB relazioneGenerale = this.findItemById(idRelazioneGenerale);
		RelazioneSTB relazioneSTB = super.findItemById(RelazioneSTB.class, em, idRelazione);
		if(relazioneGenerale == null || relazioneSTB == null)
			throw new EntityNotFoundException("relazioneGenerale == null || relazioneSTB == null");
		
		String statoRelazione = relazioneGenerale.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		boolean removed = relazioneGenerale.getRelazioniSTB().remove(relazioneSTB);
		
		if(removed)
		{
			relazioneSTB.setRelazioneGeneraleSTB(null);
			em.merge(relazioneSTB);
			em.merge(relazioneGenerale);

			return relazioneSTB.getId();
		}
		
		return -1L;
	}

}
