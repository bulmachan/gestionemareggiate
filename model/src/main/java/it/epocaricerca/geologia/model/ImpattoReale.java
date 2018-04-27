package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity 
@DiscriminatorValue(value="Reale")
public class ImpattoReale extends Impatto implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4044425663666403548L;
	
	@OneToMany(/*cascade={CascadeType.ALL}*/)
	private List<Danno> danni;
	
	@ManyToOne
	@Basic(optional=false)
	private Fonte fonte;
	
	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.EAGER)
	@JoinTable( name="MRG_IMPATTIREL_ALLEGATI" )
	private Set<Allegato> allegati;
	
	
	@ManyToOne
	private Mareggiata mareggiataImpattoReale;

	public Mareggiata getMareggiataImpattoReale() {
		return mareggiataImpattoReale;
	}

	public void setMareggiataImpattoReale(Mareggiata mareggiataImpattoReale) {
		this.mareggiataImpattoReale = mareggiataImpattoReale;
	}
	
	public Fonte getFonte() {
		return fonte;
	}

	public void setFonte(Fonte fonte) {
		this.fonte = fonte;
	}
	
	public List<Danno> getDanni() {
		if(this.danni == null)
			this.danni = new ArrayList<Danno>();
		
		return danni;
	}

	public void setDanni(List<Danno> danni) {
		this.danni = danni;
	}

	public Set<Allegato> getAllegati() {
		if(this.allegati == null)
			this.allegati = new HashSet<Allegato>();
		return allegati;
	}

	public void setAllegati(Set<Allegato> allegati) {
		this.allegati = allegati;
	}

}
