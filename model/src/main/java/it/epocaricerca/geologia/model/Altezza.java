package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_ALTEZZE")
public class Altezza implements Serializable {
	// Da 1,25 a 2,5 (Molto Mosso) - Da 2,5 a 4 (Agitato) Da 4 a 6 (Molto Agitato)
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3377200637126543139L;
	
	
	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String nome;

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public long getId() {
		return id;
	}
}
