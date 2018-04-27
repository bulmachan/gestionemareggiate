package it.epocaricerca.geologia.web.validator;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class TimeValidator implements Validator, Serializable {
	
	
	private static final long serialVersionUID = 1L;
	private static final String TIME_REGEX = "[0-9][0-9]:[0-9][0-9]";

	public void validate(FacesContext fc, UIComponent uiComponent, Object value) throws ValidatorException {
		
			/* Create the correct mask */
	      Pattern mask =  Pattern.compile(TIME_REGEX);	    

	      /* Get the string value of the current field */
	      String timeField = (String)value; 
			 	
	      /* Check to see if the value is a time in the format hh:mm */
	      Matcher matcher = mask.matcher(timeField);
		     
	    if (!matcher.matches()){
	       FacesMessage message = new FacesMessage();
	       message.setDetail("L'ora deve essere nel formato hh:mm");
	       message.setSummary("L'ora deve essere nel formato hh:mm");
	       message.setSeverity(FacesMessage.SEVERITY_ERROR);
	       throw new ValidatorException(message);
	    }

	}

}
