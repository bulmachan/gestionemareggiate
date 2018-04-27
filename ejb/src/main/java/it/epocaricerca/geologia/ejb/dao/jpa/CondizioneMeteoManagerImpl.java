package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.Iterator;
import java.util.List;
import java.util.ListResourceBundle;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.EstensioneManager;
import it.epocaricerca.geologia.ejb.dao.ProvenienzaManager;
import it.epocaricerca.geologia.ejb.dao.RilevazioneManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Estensione;
import it.epocaricerca.geologia.model.Provenienza;
import it.epocaricerca.geologia.model.Rilevazione;



@Stateless
@Remote
public class CondizioneMeteoManagerImpl extends GenericManager  implements CondizioneMeteoManager {

	@EJB
	private RilevazioneManager rilevazioneCRUD;

	@EJB
	private EstensioneManager estensioneCRUD;

	@EJB
	private ProvenienzaManager provenienzaCRUD;

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	public Long create(CondizioneMeteo condizioneMeteo,String provenienza, String estensioneTerritoriale,String direzioneVentoPrevalente) throws EntityNotFoundException {
		
		
		
		Provenienza provenienzaObj = null;
		if(provenienza!=null)
			provenienzaObj = provenienzaCRUD.findItemByName(provenienza);
		
		
		Estensione estensioneObj = estensioneCRUD.findItemByName(estensioneTerritoriale);

		Provenienza direzioneVentoPrevalenteObj = null;
		if(direzioneVentoPrevalente!=null)
			direzioneVentoPrevalenteObj = provenienzaCRUD.findItemByName(direzioneVentoPrevalente);

		//if(estensioneObj == null || provenienzaObj == null || direzioneVentoPrevalenteObj == null)
		if(estensioneObj == null)
			throw new EntityNotFoundException("estensioneObj == null || provenienzaObj == null || direzioneVentoPrevalenteObj == null");

		condizioneMeteo.setOnda(provenienzaObj);
		condizioneMeteo.setEstensioneTerritoriale(estensioneObj);
		condizioneMeteo.setDirezioneVentoPrevalente(direzioneVentoPrevalenteObj);

		em.persist(condizioneMeteo);

		return condizioneMeteo.getId();
	}


	public Long update(CondizioneMeteo condizioneMeteo,String provenienza, String estensioneTerritoriale,String direzioneVentoPrevalente) throws EntityNotFoundException {
		
		Provenienza provenienzaObj = null;
		if(provenienza != null)
			provenienzaObj = provenienzaCRUD.findItemByName(provenienza);
		
		Estensione estensioneObj = estensioneCRUD.findItemByName(estensioneTerritoriale);
		
		Provenienza direzioneVentoPrevalenteObj = null;
		if(direzioneVentoPrevalente !=null)
			direzioneVentoPrevalenteObj = provenienzaCRUD.findItemByName(direzioneVentoPrevalente);


		//if(estensioneObj == null || provenienzaObj == null || direzioneVentoPrevalenteObj == null)
		if(estensioneObj == null)
			throw new EntityNotFoundException("estensioneObj == null");

		//throw new EntityNotFoundException("estensioneObj == null || provenienzaObj == null || direzioneVentoPrevalenteObj == null");

		condizioneMeteo.setOnda(provenienzaObj);
		condizioneMeteo.setEstensioneTerritoriale(estensioneObj);
		condizioneMeteo.setDirezioneVentoPrevalente(direzioneVentoPrevalenteObj);

		
		em.merge(condizioneMeteo);
		return condizioneMeteo.getId();
	}

	public CondizioneMeteo findItemById(long id) {
		return super.findItemById(CondizioneMeteo.class, em, id);
	}

	public Long remove(long idCondizioneMeteo) throws EntityNotFoundException {
		CondizioneMeteo condizioneMeteo = this.findItemById(idCondizioneMeteo);

		if(condizioneMeteo == null)
			throw new EntityNotFoundException("CondizioneMeteo "+idCondizioneMeteo);

		List<Rilevazione> rilevazione = condizioneMeteo.getRilevazioni();

		for (Iterator iterator = rilevazione.iterator(); iterator.hasNext();) {
			Rilevazione tem = (Rilevazione) iterator.next();
			rilevazioneCRUD.remove(tem.getId());
		}



		rilevazione.clear();

		em.remove(condizioneMeteo);

		return condizioneMeteo.getId();
	}


	/** Gestione Rilevazioni Marea 
	 * @throws EntityNotFoundException **/

	

	public Long insertRilevazione(long idCondizioneMeteo,List<DatoSensore> dati, long idVariabile, long idStazione,double min, double max)throws EntityNotFoundException {

		CondizioneMeteo condizioneMeteo = this.findItemById(idCondizioneMeteo);

		if(condizioneMeteo == null)
			throw new EntityNotFoundException("CondizioneMeteo "+idCondizioneMeteo);

		Long idRilevazione = rilevazioneCRUD.create(dati, idVariabile, idStazione,min,max);


		condizioneMeteo.getRilevazioni().add(rilevazioneCRUD.findItemById(idRilevazione));
		em.merge(condizioneMeteo);

		return idRilevazione;
	}
	

	public List<Rilevazione> getRilevazioni(long idCondizioneMeteo) throws EntityNotFoundException {

		if(this.findItemById(idCondizioneMeteo) == null)
			throw new EntityNotFoundException("CondizioneMeteo "+idCondizioneMeteo);

		Query q = em.createQuery("SELECT cm.rilevazioni FROM CondizioneMeteo cm WHERE cm.id = ?1");
		q.setParameter(1, idCondizioneMeteo);
		return q.getResultList();
	}


	public Long removeRilevazione(long idCondizioneMeteo,long idRilevazioneMarea) throws EntityNotFoundException {
		CondizioneMeteo meteo = this.findItemById(idCondizioneMeteo);
		if(meteo == null)
			throw new EntityNotFoundException("CondizioneMeteo "+idCondizioneMeteo);

		Rilevazione rilevazione = rilevazioneCRUD.findItemById(idRilevazioneMarea);
		if(rilevazione == null)
			throw new EntityNotFoundException("Rilevazione "+rilevazione);

		boolean removed = meteo.getRilevazioni().remove(rilevazione);

		if(removed)
		{
			em.merge(meteo);
			rilevazioneCRUD.remove(idRilevazioneMarea);
			return rilevazione.getId();
		}

		return -1L;
	}








}
