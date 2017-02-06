package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_PROVENIENZE")
public class Provenienza implements Serializable{

	//NNE, NE, ENE, E, ESE, SE, SSE,S,SSQ,SQ,WSW,W,WNW,NW,NNW,N
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6314957873974568743L;
	
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
