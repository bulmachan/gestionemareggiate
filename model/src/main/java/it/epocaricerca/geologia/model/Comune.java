package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="MRG_COMUNI")
public class Comune implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8839028599431477484L;


	@Id
	@Column(name="ID_COMUNE")
	private long id;
	
	@Basic(optional=false)
	private String nome;
	
	@ManyToOne(optional=false)
	@JoinColumn(name = "ID_MACROAREA")  
	private MacroArea macroArea;
	
	
	@OneToMany(mappedBy="comune")
	private List<Localita> localita;
	
	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public MacroArea getMacroArea() {
		return macroArea;
	}


	public void setMacroArea(MacroArea macroArea) {
		this.macroArea = macroArea;
	}


	public List<Localita> getLocalita() {
		return localita;
	}


	public void setLocalita(List<Localita> localita) {
		this.localita = localita;
	}


	public long getId() {
		return id;
	}
	
	
}
