package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="MRG_TIPI_DANNO")
public class TipoDanno implements Serializable {
	//Erosione, Danni_stabilimenti_balneari, Tracimazione_Canali,Danni_Opere_Portuali
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -489497966516480701L;

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
