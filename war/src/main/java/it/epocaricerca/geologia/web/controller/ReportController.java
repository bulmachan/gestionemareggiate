package it.epocaricerca.geologia.web.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import it.epocaricerca.geologia.ejb.dao.MareggiataManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.dao.jpa.NestedCriterion;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Localita;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.web.controller.common.FilterableHandler;
import it.epocaricerca.geologia.web.util.JSFUtils;

public class ReportController extends FilterableHandler<Mareggiata> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 2216005271459511512L;

	private static Logger logger = Logger.getLogger(ReportController.class);

	private List<SelectItem> localitaSelect;

	private String localita;

	// FILTRO DI RICERCA
	private int filtro_anno = Calendar.getInstance().get(Calendar.YEAR);

	private int anno_minValue = 1975;

	private int anno_maxValue = Calendar.getInstance().get(Calendar.YEAR);

	private Mareggiata selectedItem;
	
	private String type_filtro;



	public void report() {
		updateDataModel();
	}

	private void cleanForm() {

	}


	/** FilterableHandler methods**/

	@Override
	public void resetSearchParameters() {

	}


	@Override
	protected List<Criterion> determineRestrictions() {
		List<Criterion> criterions = new ArrayList<Criterion>();
		if(filtro_anno>0 && type_filtro != null && type_filtro.equalsIgnoreCase("anno"))
		{
			Calendar min = Calendar.getInstance();
			min.set(filtro_anno, Calendar.JANUARY, 1);

			Calendar max = Calendar.getInstance();
			max.set(filtro_anno, Calendar.DECEMBER, 31);

			criterions.add(Restrictions.and(
					Restrictions.ge("inizioValidita", min.getTime()),
					Restrictions.le("fineValidita", max.getTime())));
		}
		else if(type_filtro != null && type_filtro.equalsIgnoreCase("localita") && localita != null)
		{

			NestedCriterion celle = new NestedCriterion(
												"impattiReali",
												new NestedCriterion("danni", 
												new NestedCriterion("localita",Restrictions.eq("nome", localita ))) 
												);
			criterions.add(celle);
		}
		return criterions;
	}

	protected List<Order> determineOrder()
	{
		ArrayList<Order> orders = new ArrayList<Order>();
		orders.add( Order.desc("inizioValidita") );
		orders.add( Order.desc("fineValidita") );
		return orders;
	}

	public String viewItemDetails() {
		
		logger.info("viewItemDetails");
		this.selectedItem = (Mareggiata) dataModel.getRowData();

		MareggiataManager mareggiataManager = JNDIUtils.retrieveEJB(JNDIUtils.MareggiataManagerName, JNDIUtils.MareggiataManagerName);

		try {
			this.selectedItem.setAllegati(mareggiataManager.getAllegati(this.selectedItem.getId()));
			this.selectedItem.setCondizioniMeteo(mareggiataManager.getCondizioniMeteo(this.selectedItem.getId()));
			this.selectedItem.setImpattiReali(mareggiataManager.getImpattiReali(this.selectedItem.getId()));
			this.selectedItem.setPrevisioniImpatti(mareggiataManager.getPrevisioneImpatto(this.selectedItem.getId()));
			this.selectedItem.setPrevisioniMeteo(mareggiataManager.getPrevisioniMeteo(this.selectedItem.getId()));
			this.selectedItem.setRelazioniSTB(mareggiataManager.getRelazioniSTB(this.selectedItem.getId()));

			MareggiateController mareggiateController = (MareggiateController)JSFUtils.getManagedBean("MareggiateController");
			mareggiateController.setSelectedItem(this.selectedItem);

		} catch (EntityNotFoundException e) {
			//TODO gestione errore
			e.printStackTrace();
			updateDataModel();
			return "";
		}


		return "mareggiataDetails";
	}



	private void updateLocalita()
	{
		localitaSelect = new ArrayList<SelectItem>();
		List<Localita> localitaFromDB = JNDIUtils.getLocalitaManager().selectAll();
		localitaSelect.add(new SelectItem(null,"-"));
		for (Localita t : localitaFromDB) {
			localitaSelect.add(new SelectItem(t.getNome()));
		}
	}


	/** Getter and Setter **/

	public int getFiltro_anno() {
		return filtro_anno;
	}

	public void setFiltro_anno(int filtro_anno) {
		this.filtro_anno = filtro_anno;
	}

	public int getAnno_minValue() {
		return anno_minValue;
	}

	public void setAnno_minValue(int anno_minValue) {
		this.anno_minValue = anno_minValue;
	}

	public int getAnno_maxValue() {
		return anno_maxValue;
	}

	public void setAnno_maxValue(int anno_maxValue) {
		this.anno_maxValue = anno_maxValue;
	}


	public List<SelectItem> getLocalitaSelect() {
		if (null == localitaSelect || localitaSelect.isEmpty())
			updateLocalita();
		return localitaSelect;
	}

	public void setLocalitaSelect(List<SelectItem> localitaSelect) {
		this.localitaSelect = localitaSelect;
	}

	public String getLocalita() {
		return localita;
	}

	public void setLocalita(String localita) {
		this.localita = localita;
	}

	public String getType_filtro() {
		return type_filtro;
	}

	public void setType_filtro(String type_filtro) {
		this.type_filtro = type_filtro;
	}


}
