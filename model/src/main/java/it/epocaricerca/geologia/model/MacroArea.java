package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;




@Entity
@Table(name="MRG_TIPI_MACRO_AREE")
public class MacroArea implements Serializable {

	//D,B1,B2, B3
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2493677013539194540L;
	
	
	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String nome;
	
	@OneToMany(mappedBy="macroArea")
	private List<Comune> comuni;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getId() {
		return id;
	}

	public List<Comune> getComuni() {
		return comuni;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((nome == null) ? 0 : nome.hashCode());
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
		MacroArea other = (MacroArea) obj;
		if (id != other.id)
			return false;
		if (nome == null) {
			if (other.nome != null)
				return false;
		} else if (!nome.equals(other.nome))
			return false;
		return true;
	}
	
}
