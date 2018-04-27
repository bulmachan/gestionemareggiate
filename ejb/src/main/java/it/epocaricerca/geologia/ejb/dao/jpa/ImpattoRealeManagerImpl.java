package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.vividsolutions.jts.io.ParseException;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.FonteManager;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.PrevisioneImpatto;
import it.epocaricerca.geologia.model.Fonte;

@Stateless
@Remote
public class ImpattoRealeManagerImpl extends GenericManager implements ImpattoRealeManager {

	private static Logger logger = Logger.getLogger(ImpattoRealeManagerImpl.class);
	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private FonteManager fonteCRUD;
	
	@EJB
	private DannoManager dannoCRUD;

	public Long create(ImpattoReale impatto, String fonte) throws EntityNotFoundException {

		// GESTIONE DEI NULLI		
		Fonte fonteObj = null;
		if(fonte!=null)
			fonteObj = fonteCRUD.findItemByName(fonte);
	
		impatto.setFonte(fonteObj);
	
		em.persist(impatto);
		return impatto.getId();
	}

	public ImpattoReale findItemById(long id) {
		return super.findItemById(ImpattoReale.class, em, id);
	}

	
	public Long update(ImpattoReale impatto, String fonte) throws EntityNotFoundException {
	
		// GESTIONE DEI NULLI		
		Fonte fonteObj = null;
		if(fonte!=null)
			fonteObj = fonteCRUD.findItemByName(fonte);
		
		impatto.setFonte(fonteObj);
		
		em.merge(impatto);
		return impatto.getId();
	}
	
	public Long remove(long idImpattoReale) throws Exception {
		ImpattoReale impattoReale = this.findItemById(idImpattoReale);
		if(impattoReale == null)
			throw new EntityNotFoundException(""+idImpattoReale);
		
		List<Danno> danni = impattoReale.getDanni();
		
		for (Danno danno : danni) {
			dannoCRUD.remove(danno.getId());
		}
		
		danni.clear();
		
		em.remove(impattoReale);
		return idImpattoReale;


	}
	
	public Long insertAllegato(Long idImpatto, Allegato allegato) throws EntityNotFoundException {
		ImpattoReale impattoReale = this.findItemById(idImpatto);
		if(impattoReale == null)
			throw new EntityNotFoundException(""+idImpatto);
		
		impattoReale.getAllegati().add(allegato);
		em.merge(impattoReale);
		return allegato.getId();
	}

	public void removeAllegato(Long idImpatto, Long idAllegato) throws EntityNotFoundException {
		ImpattoReale impattoReale = this.findItemById(idImpatto);
		if(impattoReale == null)
			throw new EntityNotFoundException(""+idImpatto);
		
		Allegato allegato = super.findItemById(Allegato.class, em , idAllegato);
		if(allegato == null)
			throw new EntityNotFoundException("Allegato "+idAllegato);
		
		boolean removed = impattoReale.getAllegati().remove(allegato);
		
		if(removed)
		{
			em.merge(impattoReale);
			em.remove(allegato);
		}
		
	}

	public Long insertDanno(long idImpattoReale, Danno danno,String nomeLocalita, String tipoDanno, String geometryText) throws ParseException, Exception {

		ImpattoReale impattoReale = this.findItemById(idImpattoReale);
		if(impattoReale == null)
			throw new EntityNotFoundException("impattoReale == null");

		Long idDanno = dannoCRUD.create(danno, nomeLocalita, tipoDanno,geometryText);
		
		impattoReale.getDanni().add(dannoCRUD.findItemById(idDanno));
		em.merge(impattoReale);

		return idDanno;
	}

	public List<Danno> getDanni(long idImpattoReale) throws EntityNotFoundException {
		if(this.findItemById(idImpattoReale) == null)
			throw new EntityNotFoundException("ImpattoReale "+idImpattoReale);

		Query q = em.createQuery("SELECT ir.danni FROM ImpattoReale ir WHERE ir.id = ?1");
		q.setParameter(1, idImpattoReale);
		return q.getResultList();
	}

	public Long removeDanno(long idImpattoReale, long idDanno)throws Exception {
		ImpattoReale impattoReale = this.findItemById(idImpattoReale);
		if(impattoReale == null)
			throw new EntityNotFoundException("ImpattoReale "+idImpattoReale);

		Danno danno = super.findItemById(Danno.class, em, idDanno);
		if(danno == null)
			throw new EntityNotFoundException("Danno "+idDanno);

		boolean removed = impattoReale.getDanni().remove(danno);

		if(removed)
		{
			em.merge(impattoReale);
			dannoCRUD.remove(idDanno);
			return danno.getId();
		}

		return -1L;
	}

	


}
