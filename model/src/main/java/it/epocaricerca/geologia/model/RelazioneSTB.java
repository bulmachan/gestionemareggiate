package it.epocaricerca.geologia.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="MRG_RELAZIONI_STB")
public class RelazioneSTB implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7913678362109431105L;
	
	@Id
	@GeneratedValue
	private long id;
	
	@Basic(optional=false)
	private String codice;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date inizioValidita;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Basic(optional=false)
	private Date fineValidita;
	
	@ManyToOne(optional=false)
	private StatoRelazione stato;
	
	@ManyToOne
	@Basic(optional=false)
	private STB stb;
	
	@ManyToOne
	@Basic(optional=false)
	private Fonte fonte;
		
	@Lob
	private String descrizione;
	
	@OneToMany(/*cascade={CascadeType.ALL}*/)
	@JoinTable( name="MRG_RELAZIONISTB_DANNI_STB" )
	private List<DannoSTB> danni;
	
	@ManyToMany(mappedBy="relazioniSTB")
	private List<Mareggiata> mareggiata;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	private RelazioneGeneraleSTB relazioneGeneraleSTB;
	
	
	@OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
	@JoinTable( name="MRG_RELAZIONI_STB_ALLEGATI" )
	private Set<Allegato> allegati;
	
	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}
	public Date getInizioValidita() {
		return inizioValidita;
	}

	public void setInizioValidita(Date inizioValidita) {
		this.inizioValidita = inizioValidita;
	}

	public Date getFineValidita() {
		return fineValidita;
	}

	public void setFineValidita(Date fineValidita) {
		this.fineValidita = fineValidita;
	}

	public StatoRelazione getStato() {
		return stato;
	}

	public void setStato(StatoRelazione stato) {
		this.stato = stato;
	}

	public STB getStb() {
		return stb;
	}

	public void setStb(STB stb) {
		this.stb = stb;
	}
	
	public Fonte getFonte() {
		return fonte;
	}

	public void setFonte(Fonte fonte) {
		this.fonte = fonte;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public long getId() {
		return id;
	}

	public List<DannoSTB> getDanni() {
		if(this.danni == null)
			this.danni = new ArrayList<DannoSTB>();
		return danni;
	}

	public void setDanni(List<DannoSTB> danni) {
		this.danni = danni;
	}

	public RelazioneGeneraleSTB getRelazioneGeneraleSTB() {
		return relazioneGeneraleSTB;
	}

	public void setRelazioneGeneraleSTB(RelazioneGeneraleSTB relazioneGeneraleSTB) {
		this.relazioneGeneraleSTB = relazioneGeneraleSTB;
	}

	public List<Mareggiata> getMareggiata() {
		return mareggiata;
	}

	public void setMareggiata(List<Mareggiata> mareggiata) {
		this.mareggiata = mareggiata;
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
