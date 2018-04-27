package it.epocaricerca.geologia.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="MRG_F_LOCALITA_LIN")
public class MicroLocalita implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2026501766852781558L;
	
	@Id
	@Column(name="OBJECTID")
	private long id;
	
	private String nome;
	
	
	@ManyToOne(optional=false)
	@JoinColumn(name = "ID_LOCALITA")  
	private Localita localita;


	public long getId() {
		return id;
	}


	public String getNome() {
		return nome;
	}


	public Localita getLocalita() {
		return localita;
	}


	public void setId(long id) {
		this.id = id;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public void setLocalita(Localita localita) {
		this.localita = localita;
	}


}
