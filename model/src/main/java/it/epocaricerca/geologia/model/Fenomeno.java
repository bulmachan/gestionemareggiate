package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_FENOMENI")
public class Fenomeno implements Serializable{

	//Livello del mare sopra soglia , Livello dell’onda sopra soglia , Combinazione onda/livello sopra soglia

	
	/**
	 * 
	 */
	//private static final long serialVersionUID = -6314957873974568743L;
	
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