package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.ejb.tdo.AggregationType;
import it.epocaricerca.geologia.model.Danno;

import java.util.Date;
import java.util.List;

public interface AnalysisManager {

	List<Object[]> getMareggiateConSenzaImpatto(Date start_date, Date end_date, AggregationType aggregationType);

	List<Object[]> getAvvisiMeteoConSenzaImpatto(Date start_date, Date end_date, AggregationType aggregationType);

	List<Object[]> getDatiMeteo(Date start_date, Date end_date);
	
	List<Object[]> getNumDanniPerLocalita(Date start_date, Date end_date);
	
	List<Object[]> getNumDanniTotali(Date start_date, Date end_date);
	
	List<Danno> getDanniByData(Date start_date, Date end_date);
}
