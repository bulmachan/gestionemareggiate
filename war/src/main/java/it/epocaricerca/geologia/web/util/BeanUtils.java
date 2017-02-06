package it.epocaricerca.geologia.web.util;

import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.DannoSTBBean;
import it.epocaricerca.geologia.ejb.tdo.DatiArpaBean;
import it.epocaricerca.geologia.ejb.tdo.FileAllegatoBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.DatoSensore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

public class BeanUtils {

	
	public static List<DannoSTBBean> convert(List<DannoSTB> danni,DannoSTBManager manager) throws Exception
	{
		List<DannoSTBBean> result = new ArrayList<DannoSTBBean>();
		
		for (DannoSTB dannoSTB : danni) {
			DannoSTBBean temp = new DannoSTBBean();
			temp.setId(dannoSTB.getId());
			temp.setDescrizione(dannoSTB.getDescrizione());
			temp.setLunghezzaTratto(dannoSTB.getLunghezzaTratto());
			temp.setLocalita(dannoSTB.getLocalita());
			temp.setFileAllegati(convertAllegati(manager.getAllegati(dannoSTB.getId())));
			temp.setGeometryText(getGeometryString(manager.getGeometry(dannoSTB.getId())));
			
			temp.setErosioni(dannoSTB.isErosioni());
			temp.setTracimazioni(dannoSTB.isTracimazioni());
			temp.setInondazioni(dannoSTB.isInondazioni());
			temp.setDanniDifese(dannoSTB.isDanniDifese());
			temp.setDanniInfrastrutture(dannoSTB.isDanniInfrastrutture());
			
			temp.setStimaCostiRipascimenti(dannoSTB.getStimaCostiRipascimenti());
			temp.setStimaCostiDanni(dannoSTB.getStimaCostiDanni());
			
			result.add(temp);
		}
		
		return result;
	}
	
	
	public static List<FileAllegatoBean> convertAllegati(List<Allegato> allegati)
	{
		List<FileAllegatoBean> result = new ArrayList<FileAllegatoBean>();
		
		for (Allegato allegato : allegati) {
			FileAllegatoBean temp = new FileAllegatoBean();
			temp.setFileName(allegato.getNome());
			temp.setTipoAllegato(allegato.getTipo().getNome());
			result.add(temp);
		}
		
		return result;
	}
	
	
	public static MinMaxHolder computeMinMax(List<DatoSensore> in)
	{
		double minOut= Double.MAX_VALUE;
		double maxOut = Double.MIN_VALUE;
		
		
		for (DatoSensore datoSensore : in) {
			if(datoSensore.getValue() < minOut)
				minOut = datoSensore.getValue();
			
			if(datoSensore.getValue() > maxOut)
				maxOut =  datoSensore.getValue();
		}
		
		MinMaxHolder holder = new MinMaxHolder();
		holder.max = maxOut;
		holder.min = minOut;
		
		return holder;
	
	
	}
	
	
	public static float computeMax(List<DatiArpaBean> datiArpa, String variabile )
	{
		
		double maxOut = Double.MIN_VALUE;
		
		for (DatiArpaBean datoArpaBean : datiArpa) {
			if(datoArpaBean.getVariabile().equalsIgnoreCase(variabile))
			{
				if(datoArpaBean.getMaxValue() > maxOut)
					maxOut =  datoArpaBean.getMaxValue();
			}
		}
		
		
		return (float) maxOut;
	}
	
	
	public static String getGeometryString(List<Geometry> geometries)
	{
		String selectedDannoGeometries = "";
		for (Iterator<Geometry> iterator = geometries.iterator(); iterator.hasNext();) {
			selectedDannoGeometries += iterator.next().toText() + (iterator.hasNext() ? ";" : "");
	    }
		
		return selectedDannoGeometries;
	}
	
	
}
