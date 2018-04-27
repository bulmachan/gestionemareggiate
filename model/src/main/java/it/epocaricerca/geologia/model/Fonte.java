package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_FONTI")
public class Fonte implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 747230923630143299L;
	
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
