package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.STBManager;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.ejb.dao.StatoRelazioneManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.STB;
import it.epocaricerca.geologia.model.Fonte;
import it.epocaricerca.geologia.model.StatoRelazione;


@Stateless
@Remote
public class RelazioneSTBManagerImpl extends GenericManager implements RelazioneSTBManager  {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	@EJB
	private StatoRelazioneManager statoRelazioneCRUD;
	
	@EJB
	private STBManager stbCRUD;
	
	@EJB
	private FonteManager fonteCRUD;
	
	@EJB
	private DannoSTBManager dannoSTBCRUD;
	
	@EJB
	private MareggiataManager mareggiataCRUD;
	
	public Long create(RelazioneSTB relazioneSTB, String STB, String fonte) throws EntityNotFoundException {
		
		StatoRelazione statoObj = statoRelazioneCRUD.findItemByName("Bozza");
		STB stbObj = stbCRUD.findItemByName(STB);
		
		// GESTIONE DEI NULLI		
		Fonte fonteObj = null;
		if(fonte!=null)
			fonteObj = fonteCRUD.findItemByName(fonte);
		
		if(stbObj == null || statoObj == null)
			throw new EntityNotFoundException("stbObj == null || statoObj == null");
		
		relazioneSTB.setStato(statoObj);
		relazioneSTB.setStb(stbObj);
		relazioneSTB.setFonte(fonteObj);
		
		em.persist(relazioneSTB);
		
		return relazioneSTB.getId();
	}
	
	
	public Long update(RelazioneSTB relazione, String STB, String fonte) throws EntityNotFoundException {
		
		STB stbObj = stbCRUD.findItemByName(STB);
		
		// GESTIONE DEI NULLI		
		Fonte fonteObj = null;
		if(fonte!=null)
			fonteObj = fonteCRUD.findItemByName(fonte);
		
		if(relazione == null || stbObj == null)
			throw new EntityNotFoundException("relazione == null || stbObj == null");
		
		if(relazione.getStato().getNome().equals("Protocollato") || relazione.getStato().getNome().equals("Definitivo"))
			return -1L;
		
		relazione.setStb(stbObj);
		relazione.setFonte(fonteObj);
		em.merge(relazione);
		
		return relazione.getId();
	}


	public Long updateStato(long idRelazione, String statoRelazione) throws EntityNotFoundException {
		
		RelazioneSTB relazione = findItemById(idRelazione);
		
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
	
	public RelazioneSTB findItemById(long idRelazione) {
		return super.findItemById(RelazioneSTB.class,em, idRelazione);
	}

	public Long remove(long idRelazione) throws Exception {
		RelazioneSTB relazione = this.findItemById(idRelazione);
		if(relazione == null)
			throw new EntityNotFoundException("RelazioneSTB "+idRelazione);
		
		String statoRelazione = relazione.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		List<DannoSTB> danni = relazione.getDanni();
		for (DannoSTB danno: danni) {
			dannoSTBCRUD.remove(danno.getId());
			
		}
		danni.clear();
		
		List<Mareggiata> mareggiate =  relazione.getMareggiata();
		
		for (Mareggiata mareggiata : mareggiate) {
			mareggiataCRUD.removeRelazioneSTB(mareggiata.getId(), idRelazione);
		}
		
		em.remove(relazione);
		
		return relazione.getId();
	}

	
	public Long insertAllegato(Long idRelazione, Allegato allegato) throws EntityNotFoundException {
		RelazioneSTB relazioneSTB = this.findItemById(idRelazione);
		if(relazioneSTB == null)
			throw new EntityNotFoundException(""+idRelazione);
		
		relazioneSTB.getAllegati().add(allegato);
		em.merge(relazioneSTB);
		return allegato.getId();
	}

	public void removeAllegato(Long idRelazione, Long idAllegato) throws EntityNotFoundException {
		RelazioneSTB relazioneSTB = this.findItemById(idRelazione);
		if(relazioneSTB == null)
			throw new EntityNotFoundException(""+relazioneSTB);
		
		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);
		
		boolean removed = relazioneSTB.getAllegati().remove(allegato);
		
		if(removed)
		{
			em.merge(relazioneSTB);
			em.remove(allegato);
		}
		
	}


	public Long insertDannoSTB(long idRelazione, DannoSTB danno, String localita, String geometryText)throws Exception {
		RelazioneSTB relazione = this.findItemById(idRelazione);
		if(relazione == null)
			throw new EntityNotFoundException("RelazioneSTB "+idRelazione);
		
		String statoRelazione = relazione.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		
		Long idDannoSTB = dannoSTBCRUD.create(danno, localita, geometryText);
		
		relazione.getDanni().add(dannoSTBCRUD.findItemById(idDannoSTB));
		em.merge(relazione);
		return idDannoSTB;
		
	}

	public Long removeDannoSTB(long idRelazione, long idDanno) throws Exception {
		RelazioneSTB relazione = this.findItemById(idRelazione);
		DannoSTB dannoStb = super.findItemById(DannoSTB.class, em, idDanno);
		if(relazione == null || dannoStb == null)
			throw new EntityNotFoundException("relazione == null || dannoStb == null");
		
		String statoRelazione = relazione.getStato().getNome();
		if(statoRelazione.equals("Protocollato") || statoRelazione.equals("Definitivo"))
			return -1L;
		
		boolean removed = relazione.getDanni().remove(dannoStb);
		
		if(removed)
		{
			
			em.merge(relazione);
			dannoSTBCRUD.remove(idDanno);
			return dannoStb.getId();
		}
		
		return -1L;
	}

	public List<DannoSTB> getDanniSTB(long idRelazione) throws EntityNotFoundException {
		if(this.findItemById(idRelazione) == null)
			throw new EntityNotFoundException("RelazioneSTB "+idRelazione);
		
		Query q = em.createQuery("SELECT pm.danni FROM RelazioneSTB pm WHERE pm.id = ?1");
		q.setParameter(1, idRelazione);
		return q.getResultList();
	}
	
	
	
	


}
