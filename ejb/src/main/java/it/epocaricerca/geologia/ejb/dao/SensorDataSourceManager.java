package it.epocaricerca.geologia.ejb.dao;

import it.epocaricerca.geologia.model.DatoSensore;

import java.util.Date;
import java.util.List;

public interface SensorDataSourceManager {

	List<DatoSensore> getDataFromVariabileStazione(long idStazione,long idVariabile,Date start,Date end) throws Exception;
}
