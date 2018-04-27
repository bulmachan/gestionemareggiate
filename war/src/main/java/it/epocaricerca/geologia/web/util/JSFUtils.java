/**
 *  <p>Copyright © 2009-2010 Epoca srl</p>
 *
 * <p>This file is part of FatturazioneElettronica. FatturazioneElettronica is free 
 * software: you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version. Fatturazione 
 * Elettronica is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with 
 * FatturazioneElettronica. If not, see <http://www.gnu.org/licenses/>.</p>
 * 
 * 
 */
package it.epocaricerca.geologia.web.util;

import java.util.Map;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * @author fulvio di marco
 * 
 * <p>Copyright © 2009-2010 Epoca srl</p>
 *
 * <p>This file is part of FatturazioneElettronica. FatturazioneElettronica is free 
 * software: you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation, either 
 * version 3 of the License, or (at your option) any later version. Fatturazione 
 * Elettronica is distributed in the hope that it will be useful, but WITHOUT ANY 
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A 
 * PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with 
 * FatturazioneElettronica. If not, see <http://www.gnu.org/licenses/>.</p>
 * 
 * Static helper methods for JavaServer Faces.
 *
 */
public class JSFUtils {
	
	//private static Logger logger = Logger.getLogger(JSFUtils.class);
	
	/** 
	 * 
	 * @return the environment-specific object instance for the current request.
	 * Servlet: This must be the current request's javax.servlet.http.HttpServletRequest instance.
	 * Portlet: This must be the current request's javax.portlet.PortletRequest instance, which will be either an ActionRequest or a RenderRequest depending upon when this method is called.
	 * 
	 */
	public static HttpServletRequest getHttpServletRequest() {
		HttpServletRequest request = 
			(HttpServletRequest) FacesContext
				.getCurrentInstance()
					.getExternalContext()
						.getRequest();
		if (request == null) {
			//logger.error("Got a null HttpServletRequest from FacesContext.");
			throw new RuntimeException("Got a null HttpServletRequest from FacesContext");
		}
		return request;
	}
	
	/**
	 * Stores an attribute in this request. Attributes are reset between requests. This method is most often used in conjunction with RequestDispatcher.
	 * Attribute names should follow the same conventions as package names. Names beginning with java.*, javax.*, and com.sun.*, are reserved for use by Sun Microsystems.
	 * If the object passed in is null, the effect is the same as calling removeAttribute(java.lang.String).
	 * It is warned that when the request is dispatched from the servlet resides in a different web application by RequestDispatcher, the object set by this method may not be correctly retrieved in the caller servlet. 
	 * 
	 * @param name - a String specifying the name of the attribute
	 * @param value - the Object to be stored
	 */
	public static void setAttribute(String name, Object value) {
		HttpServletRequest request = getHttpServletRequest();
		request.setAttribute(name,value);
	}
	
	/**
	 * Returns the value of the named attribute as an Object, or null if no attribute of the given name exists.
	 * 
	 * Attributes can be set two ways. The servlet container may set attributes to make available custom information about a request. 
	 * For example, for requests made using HTTPS, the attribute javax.servlet.request.X509Certificate can be used to retrieve 
	 * information on the certificate of the client. Attributes can also be set programatically using ServletRequest#setAttribute. 
	 * This allows information to be embedded into a request before a RequestDispatcher call.
	 * Attribute names should follow the same conventions as package names. This specification reserves names matching java.*, javax.*, and sun.*. 
	 * 
	 * @param - a String specifying the name of the attribute 
	 * @return an Object containing the value of the attribute, or null if the attribute does not exist
	 */
	public static Object getAttribute(String name) {
		HttpServletRequest request = getHttpServletRequest();
		return request.getAttribute(name);
		
	}
	
	/**
	 * Returns the value of the parameter from the underlying request by a key.
	 * 
	 * @param key
	 * @return
	 */
	public static String getRequestParameterByKey(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> parameters = context.getExternalContext().getRequestParameterMap();
		return parameters.get(key);
	}
	
	
	/**
	 * Returns the value of the header from the underlying request by a key.
	 * 
	 * @param key
	 * @return
	 */
	public static String getRequestHeaderByKey(String key) {
		FacesContext context = FacesContext.getCurrentInstance();
		Map<String, String> parameters = context.getExternalContext().getRequestHeaderMap();
		return parameters.get(key);
	}
	
	
	
	/*public static PortletResponse getPortletResponse() {
		return (PortletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
	}*/
	
//	public static Object getBean(String beanName){
//		ELContext elContext = FacesContext.getCurrentInstance().getELContext();
//	    return FacesContext.getCurrentInstance().getApplication().getELResolver().getValue(elContext, null, "beanName");
//	}
	
	public static Object getManagedBean(String name) {
		FacesContext fc = FacesContext.getCurrentInstance();
		if (fc == null) {
			System.out.println("Faces Context Application null");
		}
		return fc.getApplication().getELResolver().getValue(fc.getELContext(), null, name);
	}
	
	
	/**
	 * Loads a localized message from a given bundle, using the current ViewRoot Locale.
	 * 
	 * @return
	 */
	public static String getBundledMessage (String bundleName,String messageName){
		FacesContext context = FacesContext.getCurrentInstance();
		ResourceBundle bundle = ResourceBundle.getBundle(
				bundleName, context.getViewRoot().getLocale());
		if (bundle == null)
			return "";
		
		String msg = bundle.getString(messageName);
		if (msg ==  null)
			return "";
		else 
			return msg;
	}

}
