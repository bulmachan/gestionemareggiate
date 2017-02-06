package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="MRG_STATI_RELAZIONI")
public class StatoRelazione implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7916516859917359247L;
	

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
