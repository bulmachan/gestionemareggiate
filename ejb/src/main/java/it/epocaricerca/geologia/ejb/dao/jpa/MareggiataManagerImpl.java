package it.epocaricerca.geologia.ejb.dao.jpa;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneImpattoManager;
import it.epocaricerca.geologia.ejb.dao.PrevisioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.TipoAllegatoManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.PrevisioneMeteo;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
@Remote
public class MareggiataManagerImpl extends GenericManager implements MareggiataManager {


	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private TipoAllegatoManager tipoAllegatoCRUD;

	@EJB
	private PrevisioneMeteoManager previsioneMeteoCRUD;
	
	@EJB
	private PrevisioneImpattoManager previsioneImpattoCRUD;
	
	@EJB
	private CondizioneMeteoManager condizioneMeteoCRUD;
	
	@EJB
	private ImpattoRealeManager impattoRealeCRUD;

	public Long create(Mareggiata mareggiata) {
		em.persist(mareggiata);


		return mareggiata.getId();	
	}

	public Long remove(long idMareggiata) throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		if(mareggiata == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		List<Allegato> allegati = mareggiata.getAllegati();
		for (Iterator iterator = allegati.iterator(); iterator.hasNext();) {
			Allegato allegato = (Allegato) iterator.next();
			em.remove(allegato);

		}
		allegati.clear();
		
		
		List<RelazioneSTB> relazioniSTB = mareggiata.getRelazioniSTB();
		for (RelazioneSTB relazioneSTB : relazioniSTB) {
			relazioneSTB.getMareggiata().remove(mareggiata);
			em.merge(relazioneSTB);
		}
		
		
		List<CondizioneMeteo> condizioniMeteo = mareggiata.getCondizioniMeteo();
		for (CondizioneMeteo condizioneMeteo : condizioniMeteo) {
			condizioneMeteo.setMareggiata(null);
			em.merge(condizioneMeteo);
		}
		
		List<PrevisioneMeteo> previsioniMeteo = mareggiata.getPrevisioniMeteo();
		for (PrevisioneMeteo previsioneMeteo : previsioniMeteo) {
			previsioneMeteo.setMareggiata(null);
			em.merge(previsioneMeteo);
		}
		
		List<PrevisioneImpatto> previsioniImpatti = mareggiata.getPrevisioniImpatti();
		for (PrevisioneImpatto previsioneImpatto : previsioniImpatti) {
			previsioneImpatto.setMareggiataPrevisioneImpatto(null);
			em.merge(previsioneImpatto);
		}
		
		List<ImpattoReale> impattiReali = mareggiata.getImpattiReali();
		for (ImpattoReale impattoReale : impattiReali) {
			impattoReale.setMareggiataImpattoReale(null);
			em.merge(impattoReale);
		}

		em.remove(mareggiata);

		return mareggiata.getId();
	}

	public Mareggiata findItemById(long id) {
		return super.findItemById(Mareggiata.class,em, id);
	}


	/** Gestione Allegati Mareggiate **/

	public Long insertAllegato(long idMareggiata, Allegato allegato,String tipoAllegato) throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		TipoAllegato tipoAllegatoObj = tipoAllegatoCRUD.findItemByName(tipoAllegato);

		if(mareggiata == null || tipoAllegatoObj == null)
			throw new EntityNotFoundException("mareggiata == null || tipoAllegatoObj == null");

		allegato.setTipo(tipoAllegatoObj);
		em.persist(allegato);
		mareggiata.getAllegati().add(allegato);
		em.merge(mareggiata);

		return allegato.getId();
	}

	public List<Allegato> getAllegati(long idMareggiata)throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.allegati FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removeAllegato(long idMareggiata, long idAllegato) throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		if(mareggiata == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);

		boolean removed = mareggiata.getAllegati().remove(allegato);

		if(removed)
		{
			em.merge(mareggiata);
			em.remove(allegato);
			return allegato.getId();
		}

		return -1L;
	}


	/** Gestione PrevisioniMeteo Mareggiate **/

	public Long addPrevisioneMeteo(long idMareggiata, long idPrevisioneMeteo) throws EntityNotFoundException {

		Mareggiata mareggiata = this.findItemById(idMareggiata);
		PrevisioneMeteo previsioneMeteo = super.findItemById(PrevisioneMeteo.class,em,idPrevisioneMeteo);

		if(mareggiata == null || previsioneMeteo == null)
			throw new EntityNotFoundException("mareggiata == null || previsioneMeteo == null");

		previsioneMeteo.setMareggiata(mareggiata);
		em.merge(previsioneMeteo);
		
		mareggiata.getPrevisioniMeteo().add(previsioneMeteo);
		em.merge(mareggiata);

		return previsioneMeteo.getId();
	}

	public List<PrevisioneMeteo> getPrevisioniMeteo(long idMareggiata) throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.previsioniMeteo FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removePrevisioneMeteo(long idMareggiata, long idPrevisioneMeteo) throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		PrevisioneMeteo previsioneMeteo = super.findItemById(PrevisioneMeteo.class, em, idPrevisioneMeteo);

		if(mareggiata == null || previsioneMeteo == null)
			throw new EntityNotFoundException("mareggiata == null || previsioneMeteo == null");

		boolean removed = mareggiata.getPrevisioniMeteo().remove(previsioneMeteo);

		if(removed)
		{
			previsioneMeteo.setMareggiata(null);
			em.merge(previsioneMeteo);
			em.merge(mareggiata);
			return previsioneMeteo.getId();
		}

		return -1L;
	}

	
	/** Gestione PrevisioniImpatto Mareggiate **/
	
	public Long addPrevisioneImpatto(long idMareggiata, long idPrevisioneImpatto) throws EntityNotFoundException {
		
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		PrevisioneImpatto previsioneImpatto = super.findItemById(PrevisioneImpatto.class,em,idPrevisioneImpatto);

		if(mareggiata == null || previsioneImpatto == null)
			throw new EntityNotFoundException("mareggiata == null || previsioneImpatto == null");

		previsioneImpatto.setMareggiataPrevisioneImpatto(mareggiata);
		em.merge(previsioneImpatto);
		
		mareggiata.getPrevisioniImpatti().add(previsioneImpatto);
		em.merge(mareggiata);

		return previsioneImpatto.getId();
	}

	public List<PrevisioneImpatto> getPrevisioneImpatto(long idMareggiata) throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.previsioniImpatti FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removePrevisioneImpatto(long idMareggiata,long idPrevisioneImpatto) throws EntityNotFoundException {
		
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		PrevisioneImpatto previsioneImpatto = super.findItemById(PrevisioneImpatto.class, em, idPrevisioneImpatto);

		if(mareggiata == null || previsioneImpatto == null)
			throw new EntityNotFoundException("mareggiata == null || previsioneImpatto == null");

		boolean removed = mareggiata.getPrevisioniImpatti().remove(previsioneImpatto);

		if(removed)
		{
			previsioneImpatto.setMareggiataPrevisioneImpatto(null);
			em.merge(previsioneImpatto);
			em.merge(mareggiata);
			return previsioneImpatto.getId();
		}

		return -1L;
	}

	
	/** Gestione CondizioniMeteo Mareggiate **/
	
	public Long addCondizioneMeteo(long idMareggiata, long idCondizioneMeteo)throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		CondizioneMeteo condizioneMeteo = super.findItemById(CondizioneMeteo.class, em, idCondizioneMeteo);

		if(mareggiata == null || condizioneMeteo == null)
			throw new EntityNotFoundException("mareggiata == null || condizioneMeteo == null");

		condizioneMeteo.setMareggiata(mareggiata);
		em.merge(condizioneMeteo);
		mareggiata.getCondizioniMeteo().add(condizioneMeteo);
		em.merge(mareggiata);

		return condizioneMeteo.getId();
	}

	public List<CondizioneMeteo> getCondizioniMeteo(long idMareggiata)throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.condizioniMeteo FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removeCondizioneMeteo(long idMareggiata, long idCondizioneMeteo)throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		CondizioneMeteo condizioneMeteo = super.findItemById(CondizioneMeteo.class, em, idCondizioneMeteo);

		if(mareggiata == null || condizioneMeteo == null)
			throw new EntityNotFoundException("mareggiata == null || condizioneMeteo == null");

		boolean removed = mareggiata.getCondizioniMeteo().remove(condizioneMeteo);

		if(removed)
		{
			condizioneMeteo.setMareggiata(null);
			em.merge(condizioneMeteo);
			em.merge(mareggiata);
			return condizioneMeteo.getId();
		}

		return -1L;
	}

	
	/** Gestione Impatto Reale Mareggiate **/
	
	public Long addImpattoReale(long idMareggiata, long idImpattoReale)throws EntityNotFoundException {
		
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		ImpattoReale impattoReale = super.findItemById(ImpattoReale.class, em, idImpattoReale);

		if(mareggiata == null || impattoReale == null)
			throw new EntityNotFoundException("mareggiata == null || impattoReale == null");

		impattoReale.setMareggiataImpattoReale(mareggiata);
		em.merge(impattoReale);
		
		mareggiata.getImpattiReali().add(impattoReale);
		em.merge(mareggiata);

		return impattoReale.getId();
	}

	public List<ImpattoReale> getImpattiReali(long idMareggiata)throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.impattiReali FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removeImpattoReale(long idMareggiata, long idImpattoReale)throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		ImpattoReale impattoReale = super.findItemById(ImpattoReale.class, em, idImpattoReale);

		if(mareggiata == null || impattoReale == null)
			throw new EntityNotFoundException("mareggiata == null || impattoReale == null");

		boolean removed = mareggiata.getImpattiReali().remove(impattoReale);

		if(removed)
		{
			impattoReale.setMareggiataImpattoReale(null);
			em.merge(impattoReale);
			em.merge(mareggiata);
			return impattoReale.getId();
		}

		return -1L;
	}
	
	/** Gestione Relazioni STB  Mareggiate **/
	

	public Long addRelazioneSTB(long idMareggiata, long idRelazioneSTB) throws EntityNotFoundException {

		Mareggiata mareggiata = this.findItemById(idMareggiata);
		RelazioneSTB relazioneSTB = super.findItemById(RelazioneSTB.class, em, idRelazioneSTB);

		if(mareggiata == null || relazioneSTB == null)
			throw new EntityNotFoundException("mareggiata == null || relazioneSTB == null");

		relazioneSTB.getMareggiata().add(mareggiata);
		em.merge(relazioneSTB);
		
		mareggiata.getRelazioniSTB().add(relazioneSTB);
		em.merge(mareggiata);

		return relazioneSTB.getId();
	}

	public List<RelazioneSTB> getRelazioniSTB(long idMareggiata) throws EntityNotFoundException {
		if(this.findItemById(idMareggiata) == null)
			throw new EntityNotFoundException("Mareggiata "+idMareggiata);

		Query q = em.createQuery("SELECT m.relazioniSTB FROM Mareggiata m WHERE m.id = ?1");
		q.setParameter(1, idMareggiata);
		return q.getResultList();
	}

	public Long removeRelazioneSTB(long idMareggiata, long idRelazioneSTB) throws EntityNotFoundException {
		Mareggiata mareggiata = this.findItemById(idMareggiata);
		RelazioneSTB relazioneSTB = super.findItemById(RelazioneSTB.class, em, idRelazioneSTB);

		if(mareggiata == null || relazioneSTB == null)
			throw new EntityNotFoundException("mareggiata == null || relazioneSTB == null");

		boolean removed = mareggiata.getRelazioniSTB().remove(relazioneSTB);

		if(removed)
		{
			relazioneSTB.getMareggiata().remove(mareggiata);
			em.merge(relazioneSTB);
			em.merge(mareggiata);
			return relazioneSTB.getId();
		}

		return -1L;
	}
	
	
	
	
	
	
	

}
