package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity 
@DiscriminatorValue(value="Previsione")
public class PrevisioneImpatto extends Impatto  implements Serializable{


	private static final long serialVersionUID = -4741650716809688200L;
	
	@ManyToOne
	private Mareggiata mareggiataPrevisioneImpatto;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinTable( name="MRG_IMPATTI_PREVISIONEDANNI" )
	private List<PrevisioneDanno> danni;
	
	@OneToMany(cascade={CascadeType.ALL},fetch = FetchType.EAGER )
	@JoinTable( name="MRG_IMPATTIPREVISIONE_ALLEGATI" )
	private Set<Allegato> allegati;

	public Mareggiata getMareggiataPrevisioneImpatto() {
		return mareggiataPrevisioneImpatto;
	}

	public void setMareggiataPrevisioneImpatto(Mareggiata mareggiataPrevisioneImpatto) {
		this.mareggiataPrevisioneImpatto = mareggiataPrevisioneImpatto;
	}

	public List<PrevisioneDanno> getDanni() {
		return danni;
	}

	public void setDanni(List<PrevisioneDanno> danni) {
		this.danni = danni;
	}

	public Set<Allegato> getAllegati() {
		if(allegati == null)
			 allegati = new HashSet<Allegato>();
		return allegati;
	}
	

	public void setAllegati(Set<Allegato> allegati) {
		this.allegati = allegati;
	}


}
