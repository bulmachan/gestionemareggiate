package it.epocaricerca.geologia.ejb.dao.jpa;


import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import it.epocaricerca.geologia.ejb.dao.MacroAreaManager;
import it.epocaricerca.geologia.ejb.dao.FenomenoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.AltezzaManager;
import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.ejb.dao.TendenzaManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.EventoCostiero;
import it.epocaricerca.geologia.model.MacroArea;
import it.epocaricerca.geologia.model.Fenomeno;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.Altezza;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Tendenza;
import it.epocaricerca.geologia.model.TipoAllegato;

@Stateless
@Remote
public class PrevisioneMeteoManagerImpl extends GenericManager implements PrevisioneMeteoManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	@EJB
	private TendenzaManager tendenzaCRUD;
	
	@EJB
	private AltezzaManager altezzaCRUD;
	
	@EJB
	private ProvenienzaManager provenienzaCRUD;
	
	@EJB
	private MacroAreaManager macroAreaCRUD;
	
	@EJB
	private FenomenoManager fenomenoCRUD;
	
	@EJB
	private TipoAllegatoManager tipoAllegatoCRUD;
	
	
	public Long create(PrevisioneMeteo previsione, String provenienzaVentoPianura, String provenienzaVentoPedemontana, String tendenza, String altezza, String direzione) throws EntityNotFoundException {
		
		// GESTIONE DEI NULLI
		Altezza altezzaObj = null;
		if(altezza!=null) {
			altezzaObj = altezzaCRUD.findItemByName(altezza);
		}
			
		Provenienza provenienzaObj = null;
		if(direzione!=null) {
			provenienzaObj = provenienzaCRUD.findItemByName(direzione);
		}
		
		Tendenza tendenzaObj = null;
		if(tendenza!=null) {
			tendenzaObj = tendenzaCRUD.findItemByName(tendenza);
		}
		
		// GESTIONE DEI NULLI		
		Provenienza provenienzaPianura = null;
		if(provenienzaVentoPianura!=null)
			provenienzaPianura = provenienzaCRUD.findItemByName(provenienzaVentoPianura);
		
		// GESTIONE DEI NULLI		
		Provenienza provenienzaPedemontana = null;
		if(provenienzaVentoPedemontana!=null)
			provenienzaPedemontana = provenienzaCRUD.findItemByName(provenienzaVentoPedemontana);
		
		if(tendenzaObj == null)
			throw new EntityNotFoundException("tendenzaObj == null");
			
		previsione.setTendenza(tendenzaObj);
		previsione.setPedemontanaDirezioneVento(provenienzaPedemontana);
		previsione.setPianuraDirezioneVento(provenienzaPianura);
		previsione.setDirezioneProvStimataOnda(provenienzaObj);
		previsione.setAltezzaStimataOnda(altezzaObj);
		
		em.persist(previsione);
				
		return previsione.getId();
	}
	
	public Long update(PrevisioneMeteo previsione, String provenienzaVentoPianura, String provenienzaVentoPedemontana, String tendenza, String altezza, String direzione) throws EntityNotFoundException {
		
	
		if(this.findItemById(previsione.getId()) == null)
			throw new EntityNotFoundException("Previsione "+previsione.getId());
		
		
		// GESTIONE DEI NULLI
		Altezza altezzaObj = null;
		if(altezza!=null) {
			altezzaObj = altezzaCRUD.findItemByName(altezza);
		}
		
		Provenienza provenienzaObj = null;
		if(direzione!=null) {
			provenienzaObj = provenienzaCRUD.findItemByName(direzione);
		}
		
		Tendenza tendenzaObj = null;
		if(tendenza!=null) {
			tendenzaObj = tendenzaCRUD.findItemByName(tendenza);
		}
		
		// GESTIONE DEI NULLI
		Provenienza provenienzaPianura = null;
		if(provenienzaVentoPianura!=null)
			provenienzaPianura = provenienzaCRUD.findItemByName(provenienzaVentoPianura);
		
		// GESTIONE DEI NULLI
		Provenienza provenienzaPedemontana = null;
		if(provenienzaVentoPedemontana!=null)
			provenienzaPedemontana = provenienzaCRUD.findItemByName(provenienzaVentoPedemontana);
		

		if(tendenzaObj == null)
			throw new EntityNotFoundException("tendenzaObj == null");
			
		previsione.setTendenza(tendenzaObj);
		previsione.setPedemontanaDirezioneVento(provenienzaPedemontana);
		previsione.setPianuraDirezioneVento(provenienzaPianura);
		previsione.setDirezioneProvStimataOnda(provenienzaObj);
		previsione.setAltezzaStimataOnda(altezzaObj);
		
		em.merge(previsione);
		return previsione.getId();
	}
	
	
	public Long remove(long idPrevisione) throws EntityNotFoundException {
		
		PrevisioneMeteo previsione = this.findItemById(idPrevisione);
		if(previsione == null)
			throw new EntityNotFoundException("Previsione "+idPrevisione);
		
		List<Allegato> allegati = previsione.getAllegati();
		for (Iterator iterator = allegati.iterator(); iterator.hasNext();) {
			Allegato allegato = (Allegato) iterator.next();
			em.remove(allegato);
			
		}
		allegati.clear();
		
		List<EventoCostiero> eventiCostieri = previsione.getEventiCostieri();
		for (EventoCostiero eventoCostiero : eventiCostieri) {
			em.remove(eventoCostiero);
		}
		
		em.remove(previsione);
		
		return previsione.getId();
	}
	
	

	public PrevisioneMeteo findItemById(long id) {
		return super.findItemById(PrevisioneMeteo.class,em, id);
	}

	public Long insertAllegato(long id, Allegato allegato, String tipoAllegato) throws EntityNotFoundException {
		PrevisioneMeteo previsione = this.findItemById(id);
		TipoAllegato tipoAllegatoObj = tipoAllegatoCRUD.findItemByName(tipoAllegato);
		
		if(previsione == null || tipoAllegatoObj == null)
			throw new EntityNotFoundException("previsione == null || tipoAllegatoObj == null");
		
		allegato.setTipo(tipoAllegatoObj);
		em.persist(allegato);
		previsione.getAllegati().add(allegato);
		em.merge(previsione);
		
		return allegato.getId();
		
	}

	public List<Allegato> getAllegati(long previsioneId) throws EntityNotFoundException {
		if(this.findItemById(previsioneId) == null)
			throw new EntityNotFoundException("Previsione "+previsioneId);
		
		Query q = em.createQuery("SELECT pm.allegati FROM PrevisioneMeteo pm WHERE pm.id = ?1");
		q.setParameter(1, previsioneId);
		return q.getResultList();
	}

	
	public Long removeAllegato(long idPrevisioneMeteo, long idAllegato) throws EntityNotFoundException {
		
		PrevisioneMeteo previsione = this.findItemById(idPrevisioneMeteo);
		if(previsione == null)
			throw new EntityNotFoundException("Previsione "+idPrevisioneMeteo);
		
		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);
		
		boolean removed = previsione.getAllegati().remove(allegato);
		
		if(removed)
		{
			em.merge(previsione);
			em.remove(allegato);
			return allegato.getId();
		}
		
		return -1L;
	}


	public Long insertEventoCostiero(long idPrevisione, EventoCostiero eventoCostiero,String direzioneEventoCostiero,String macroArea,String fenomeno) throws EntityNotFoundException {
		PrevisioneMeteo previsione = this.findItemById(idPrevisione);
		
		logger.info("insertEventoCostiero: "+direzioneEventoCostiero);
		
		Provenienza provenienzaObj = null;
		if(direzioneEventoCostiero!=null)
			provenienzaObj = provenienzaCRUD.findItemByName(direzioneEventoCostiero);
		
		MacroArea macroAreaObj = macroAreaCRUD.findItemByName(macroArea);
		Fenomeno fenomenoObj = fenomenoCRUD.findItemByName(fenomeno);
		
		if(previsione == null || macroAreaObj == null || fenomenoObj == null)
			throw new EntityNotFoundException("previsione == null || macroAreaObj == null || fenomenoObj == null");
		
		eventoCostiero.setPrevisione(previsione);
		eventoCostiero.setDirezione(provenienzaObj);
		eventoCostiero.setMacroArea(macroAreaObj);
		eventoCostiero.setFenomeno(fenomenoObj);
		
		em.persist(eventoCostiero);
		
		previsione.getEventiCostieri().add(eventoCostiero);
		em.merge(previsione);
		
		return eventoCostiero.getId();
	}


	public Long removeEventoCostiero(long idPrevisione, long idEventoCostiero) throws EntityNotFoundException {
		PrevisioneMeteo previsione = this.findItemById(idPrevisione);
		EventoCostiero eventoCostiero =  super.findItemById(EventoCostiero.class, em, idEventoCostiero);
		if(eventoCostiero == null || previsione == null )
			throw new EntityNotFoundException("eventoCostiero == null || previsione == null ");
		
		boolean removed = previsione.getEventiCostieri().remove(eventoCostiero);
		
		if(removed)
		{
			em.merge(previsione);
			em.remove(eventoCostiero);
			return eventoCostiero.getId();
		}
		
		return -1L;
		
		
	}


	public List<EventoCostiero> getEventiCostieri(long idPrevisione)throws EntityNotFoundException {
		if(this.findItemById(idPrevisione) == null)
			throw new EntityNotFoundException("Previsione "+idPrevisione);
		
		Query q = em.createQuery("SELECT pm.eventiCostieri FROM PrevisioneMeteo pm WHERE pm.id = ?1");
		q.setParameter(1, idPrevisione);
		return q.getResultList();
	}

	

}
