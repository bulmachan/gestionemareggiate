package it.epocaricerca.geologia.web.converter;

import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Localita;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class LocalitaConverter implements Converter {

	public static final String separator = ";";
	public Object getAsObject(FacesContext context, UIComponent component, String newValue) {
		Localita localita = null;
		try {
			if(newValue!=null && newValue.contains(separator))
			{
				String[] splitted =  newValue.split(separator);
				String name = splitted[0];
				localita  = JNDIUtils.getLocalitaManager().findItemByName(name);
			}
			else
				return null;
			
		}catch(Throwable ex) {
			FacesMessage msg = new FacesMessage("Errore di conversione "+newValue);
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
		return localita;
	}

	public String getAsString(FacesContext context, UIComponent component, Object value) {
		String val = null;
		try {
			if(value!= null && value instanceof Localita){
				Localita l  =  (Localita) value;
				val = l.getNome()+separator+l.getCoordinate();
			}
			
			
		}catch(Throwable ex) {
			FacesMessage msg = new FacesMessage("Errore di conversione ");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			throw new ConverterException(msg);
		}
		return val;
	}

}