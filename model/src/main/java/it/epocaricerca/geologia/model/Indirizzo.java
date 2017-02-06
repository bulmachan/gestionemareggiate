package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_INDIRIZZI")
public class Indirizzo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	

	@Id
	private long id;

	@Basic(optional=false)
	private String nome;
	
	@Basic(optional=false)
	private String indirizzo;

	public Indirizzo(){
		super();
	}
	
	public Indirizzo(String nome, String indirizzo) {
		super();
		this.nome = nome;
		this.indirizzo = indirizzo;
	}
	
	public long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getIndirizzo() {
		return indirizzo;
	}
	
	
}
