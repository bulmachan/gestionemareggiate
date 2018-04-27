package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.MacroAreaManager;
import it.epocaricerca.geologia.model.MacroArea;


@Stateless
@Remote
public class MacroAreaManagerImpl extends GenericManager implements MacroAreaManager {

	
	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public MacroArea findItemById(long id) {
		return super.findItemById(MacroArea.class, em, id);
	}

	public MacroArea findItemByName(String name) {
		Query q = em.createQuery("SELECT ma FROM MacroArea ma WHERE ma.nome = ?1");
		q.setParameter(1, name);
		return (MacroArea) q.getSingleResult();
	}

	public List<MacroArea> selectAll() {
		Query q = em.createQuery("SELECT ma FROM MacroArea ma ORDER BY ma.nome");
		return q.getResultList();
	}

}
