package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.List;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.epocaricerca.geologia.ejb.dao.EstensioneManager;
import it.epocaricerca.geologia.model.Estensione;

@Stateless
@Remote
public class EstensioneManagerImpl extends GenericManager implements EstensioneManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	public Estensione findItemById(long id) {
		return super.findItemById(Estensione.class, em, id);
	}

	public Estensione findItemByName(String name) {
		Query q = em.createQuery("SELECT e FROM Estensione e WHERE e.nome = ?1");
		q.setParameter(1, name);
		return (Estensione) q.getSingleResult();
	}

	public List<Estensione> selectAll() {
		Query q = em.createQuery("SELECT e FROM Estensione e ORDER BY e.nome");
		return q.getResultList();
	}

}
