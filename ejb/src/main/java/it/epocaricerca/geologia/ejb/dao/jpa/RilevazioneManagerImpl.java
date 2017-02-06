package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.RilevazioneManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

@Stateless
@Remote
public class RilevazioneManagerImpl  extends GenericManager implements RilevazioneManager {

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	@EJB
	private VariabileManager variabileCRUD;
	
	@EJB
	private StazioneManager stazioneCRUD;
	
	public Long create(List<DatoSensore> dati, long idVariabile, long idStazione, double min, double max)throws EntityNotFoundException {
		
		
		VariabileStazione variabile = variabileCRUD.findItemById(idVariabile);
		
		Stazione stazione = stazioneCRUD.findItemById(idStazione);
		
		if(variabile == null)
			throw new EntityNotFoundException("variabile == null");
		
		if(stazione == null)
			throw new EntityNotFoundException("stazione == null");
		
		if(!stazione.getVariabili().contains(variabile))
			throw new EntityNotFoundException("!stazione.getVariabili().contains(variabile)");
		
		Rilevazione rilevazione = new Rilevazione();
		rilevazione.setDatiSensore(dati);
		rilevazione.setStazione(stazione);
		rilevazione.setVariabileStazione(variabile);
		rilevazione.setMinValue(min);
		rilevazione.setMaxValue(max);
		
		em.persist(rilevazione);
		return rilevazione.getId();
	}

	public Long remove(long idRilevazione) throws EntityNotFoundException {
		Rilevazione rilevazione = this.findItemById(idRilevazione);
		if(rilevazione == null)
			throw new EntityNotFoundException("rilevazione == null");
		em.remove(rilevazione);
		
		return idRilevazione;
	}
	
	
	public Long update(long idRilevazione,List<DatoSensore> dati, double min, double max)throws EntityNotFoundException {

		Rilevazione rilevazione = this.findItemById(idRilevazione);
		if(rilevazione == null)
			throw new EntityNotFoundException("rilevazione == null");
	
		rilevazione.setDatiSensore(dati);
		rilevazione.setMinValue(min);
		rilevazione.setMaxValue(max);
		em.merge(rilevazione);

		return idRilevazione;
	}

	public Rilevazione findItemById(long id) {
		return super.findItemById(Rilevazione.class, em, id);
	}
	
	
	public List<DatoSensore> findDatiById(long id) {
		
		Query q = em.createQuery("SELECT dato FROM Rilevazione pm inner join pm.datiSensore dato WHERE pm.id = ?1 ORDER BY dato.timestamp ASC");
		
		q.setParameter(1, id);
		List<DatoSensore> dati =  q.getResultList();
		dati.size();
		
		return dati;
	}
	
	
	

}
