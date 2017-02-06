package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

//MRG_TIPI_ALLEGATO

@Entity
@Table(name="MRG_TIPI_ALLEGATO")
public class TipoAllegato implements Serializable{
	//Bollettino_ARPA, Quotidiano,Immagine,Cartografia
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7902011680316861117L;

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
