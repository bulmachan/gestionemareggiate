package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

@Entity
@Table(name="MRG_VARIABILI")
public class VariabileStazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2335781696072761876L;

	@Id
	@GeneratedValue
	private long id;
	
	private String codice;
	
	private String descrizione;

	@ManyToMany(mappedBy="variabili")
	private List<Stazione> stazioni;
	
	public long getId() {
		return id;
	}

	public String getCodice() {
		return codice;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public List<Stazione> getStazioni() {
		return stazioni;
	}

	public void setStazioni(List<Stazione> stazioni) {
		this.stazioni = stazioni;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codice == null) ? 0 : codice.hashCode());
		result = prime * result
				+ ((descrizione == null) ? 0 : descrizione.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		VariabileStazione other = (VariabileStazione) obj;
		if (codice == null) {
			if (other.codice != null)
				return false;
		} else if (!codice.equals(other.codice))
			return false;
		if (descrizione == null) {
			if (other.descrizione != null)
				return false;
		} else if (!descrizione.equals(other.descrizione))
			return false;
		if (id != other.id)
			return false;
		return true;
	}

	
	
	
	
}
