
package it.epocaricerca.geologia.web.controller.common;

import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.web.controller.ValutazioneImpattiController;
import it.epocaricerca.geologia.web.model.FilterablePagedDataModel;
import it.epocaricerca.geologia.web.util.GenericUtils;
import it.epocaricerca.geologia.web.util.JSFUtils;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.DataModelEvent;
import javax.faces.model.DataModelListener;

import org.apache.log4j.Logger;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

/**
 * Handler web generico con logica di filtraggio e ordinamento.
 * Contiene un Datamodel generico, sulle cui colonne è possibile effettuare filtri e ordinamenti.
 * 
 * Implementa due metodi opportuni, rispettivamente per il sort e il filtering dei dati del datamodel.
 * Entrambi i metodi fanno riferimento al metodo updateDatamodel(), che è effettivamente responsabile di invocare l'aggiornamento del datamodel.
 *  
 * Il datamodel (di tipo FilterablePagedDatamodel) è generico e, per mezzo dei Criteria di Hibernate e lavorando in collaborazione 
 * con un CrudManger generico, è in grado di generare query dinamicamente sulla base dei parametri di filtro impostati lato web. 
 * 
 * La logica di gestione dei parametri di filtro è la sola cosa che va ridefinita nelle sottoclassi: 
 * in particolare ciascuna sottoclasse dovrà implementare il metodo determineDetachedCriteria() per generare i criteri di filtro sulla base
 * della logica web.
 * 
 *   
 * @author Stefano Monti
 *
 */
public abstract class FilterableHandler<T> implements Serializable, DataModelListener{


		private static Logger logger = Logger.getLogger(FilterableHandler.class);

		private static final long serialVersionUID = 1L;
		public static final String ASC = "asc";
		public static final String DEC = "dec";
		private static final String ORDER = "order";
		private static final String BY = "by";
		
		protected int scrollerPage;
		protected int rowsPerPage = 10;
		protected int dataSize = 0;
		protected DataModel dataModel;
		protected String order;

		private Class<T> clazz;
		
		public FilterableHandler()
		{
			this.clazz =  (Class<T>) GenericUtils.getTypeArguments(FilterableHandler.class, this.getClass()).get(0);
		}
		
		public String getOrder() {
			return order;
		}

		public void setOrder(String order) {
			this.order = order;
		}

		public String getBy() {
			return by;
		}

		public void setBy(String by) {
			this.by = by;
		}

		protected String by;
		
		public int getDataSize() {
			if (dataSize == 0)
				updateDataModelSize();
			return dataSize;
		}

		public void setDataSize(int dataSize) {
			this.dataSize = dataSize;
		}
		public int getRowsPerPage() {
			return rowsPerPage;
		}

		public void setRowsPerPage(int rowsPerPage) {
			this.rowsPerPage = rowsPerPage;
		}

		public DataModel getDataModel() {
			if (dataModel == null) {
				updateDataModel();
			}
			return dataModel;
		}
		
		public int getScrollerPage() {
			return scrollerPage;
		}
		public void setScrollerPage(int scrollerPage) {
			this.scrollerPage = scrollerPage;
		}

	
		/**
		 * Il componente web deve passare 2 parametri ("order" per l'ordinamento (asc o dec) e "by" per il campo su cui ordinare ) 
		 * e chiamare questo metodo sort();
		 * 
		 * @return
		 */
		public void sort() {
			this.order = JSFUtils.getRequestParameterByKey(ORDER);
			this.by = JSFUtils.getRequestParameterByKey(BY);
			updateDataModel();
		}
		
		
		
		public void filter(){
			updateDataModel();
			scrollerPage = 0;
		}
		
		public String resetSearch() {
			resetSearchParameters();
			updateDataModel();
			return "";
		}
		
		/**
		 * Determina l'ordinamento da usare nelle query
		 * @return 
		 * 
		 * @return
		 */
		/*public Order determineOrder() {
			if ((this.by == null) || (this.by.equals("")))
					return null;
			else if ( (order!= null) && (order.equalsIgnoreCase(ASC)) && (by!= null))
				if (by.contains(".")){
					String inner = by.substring(by.indexOf(".")+1);
					String outer = by.substring(0, by.indexOf("."));
					return new NestedOrder(outer,inner,true);
				}
				else
					return Order.asc(by);
			else if ( (order!= null) && (order.equalsIgnoreCase(DEC)) && (by!= null))
				if (by.contains(".")){
					String inner = by.substring(by.indexOf(".")+1);
					String outer = by.substring(0, by.indexOf("."));
					return new NestedOrder(outer,inner,false);
				}
				else
					return Order.desc(by);
			
			return null;
		}*/
	
		
		
		public void rowSelected(DataModelEvent event)
		{
			
		}
	
		public void updateDataModel(){
			updateDataModelSize();
			dataModel = new FilterablePagedDataModel(rowsPerPage, determineRestrictions(),determineOrder(), JNDIUtils.getPagingManager(), determineClass());
			dataModel.addDataModelListener(this);
		}
		
		public void updateDataModelSize(){
			this.dataSize = JNDIUtils.getPagingManager().countByCriteria(determineClass(), determineRestrictions());
		}
		
		/**
		 * Da implementare nelle sottoclassi con la logica effettiva per "resettare" i parametri di ricerca
		 */
		public abstract void resetSearchParameters() ;
		
		protected Class determineClass()
		{
			return this.clazz;
		}

		/**
		 * Determina i parametri effettivi di filtro lato applicativo (web). Va ridefinito nelle sottoclassi, in maniera da gestire 
		 * i parametri di filtro specifici per la portlet cui è associato l'handler.
		 * 
		 * @return
		 */
		//protected abstract Map <String, List<Criterion>> determineWebRestrictions();

		/**
		 * Metodo da implementare con la logica di determinazione dei filtri. 
		 * @return
		 */
		protected abstract List<Criterion> determineRestrictions();
		
		protected abstract List<Order> determineOrder();
		


	}