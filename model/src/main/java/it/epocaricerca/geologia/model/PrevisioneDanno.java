package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="MRG_PREVISIONE_DANNI")
public class PrevisioneDanno implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 863550134832921356L;
	
	@Id
	@GeneratedValue
	private Long id;
	
	@ManyToOne(optional=false)
	private MacroArea macroArea;
	
	@ManyToOne(optional=false)
	private LivelloCriticita livello_criticita;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable( name="MRG_PREVISIONE_DANNI_TIPIDANNO" )
	private Set<TipoDanno> tipiDanno;

	@Lob
	private String commento;
	
	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.EAGER )
	@JoinTable( name="MRG_PREVISIONE_DANNI_ALLEGATI" )
	private List<Allegato> allegati;

	public Long getId() {
		return id;
	}

	public MacroArea getMacroArea() {
		return macroArea;
	}


	public String getCommento() {
		return commento;
	}

	public List<Allegato> getAllegati() {
		if(allegati == null)
			this.allegati = new ArrayList<Allegato>();
		
		return allegati;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setMacroArea(MacroArea macroArea) {
		this.macroArea = macroArea;
	}
	
	
	public void setCommento(String commento) {
		this.commento = commento;
	}

	public void setAllegati(List<Allegato> allegati) {
		this.allegati = allegati;
	}

	public Set<TipoDanno> getTipiDanno() {
		return tipiDanno;
	}

	public void setTipiDanno(Set<TipoDanno> tipiDanno) {
		this.tipiDanno = tipiDanno;
	}

	public LivelloCriticita getLivello_criticita() {
		return livello_criticita;
	}

	public void setLivello_criticita(LivelloCriticita livello_criticita) {
		this.livello_criticita = livello_criticita;
	}


}
