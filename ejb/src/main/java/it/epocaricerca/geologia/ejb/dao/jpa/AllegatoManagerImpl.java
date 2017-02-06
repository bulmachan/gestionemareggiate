package it.epocaricerca.geologia.ejb.dao.jpa;

import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import it.epocaricerca.geologia.ejb.dao.AllegatoManager;
import it.epocaricerca.geologia.model.Allegato;


@Stateless
@Remote
public class AllegatoManagerImpl extends GenericManager implements AllegatoManager {

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;
	
	
	public byte[] getAllegatoAttachment(Long allegatoId) {
		Allegato allegato = (Allegato) super.findItemById(Allegato.class, em, allegatoId);
		return allegato.getFile();
	}

}
