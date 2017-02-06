package it.epocaricerca.geologia.web.util;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import com.sun.facelets.FaceletViewHandler;

import java.net.URISyntaxException;  
import java.net.URI;  

import org.apache.log4j.Logger;
import org.apache.commons.lang.StringUtils;
import java.io.*;

/**
 * Replaces the context path in the links generated
 * by JSF 
 */
public class ReverseProxyViewHandler extends FaceletViewHandler {
	private static Logger logger = Logger
	.getLogger(ReverseProxyViewHandler.class);
	private ViewHandler defaultHandler;

	public ReverseProxyViewHandler(ViewHandler defaultHandler) {
		super(defaultHandler);
		this.defaultHandler = defaultHandler;
	}

	@Override
	public String getActionURL(final FacesContext context, final String viewId) {
		//logger.info("ReverseProxyViewHandler:getActionURL original - "+this.defaultHandler.getActionURL(context, viewId));
		return getRelativeURL(context, this.defaultHandler.getActionURL(context, viewId));
	}

	@Override
	public String getResourceURL(final FacesContext context, final String path) {
		//logger.info("ReverseProxyViewHandler:getResourceURL original - "+this.defaultHandler.getResourceURL(context, path));
		return getRelativeURL(context, this.defaultHandler.getResourceURL(context, path));
	}

	/**
	* Transform the given URL to a relative URL <b>in the context of the current
	* faces request</b>. If the given URL is not absolute do nothing and return
	* the given url. The returned relative URL is "equal" to the original url but
	* will not start with a '/'. So the browser can request the "same" resource
	* but in a relative way and this is important behind reverse proxies!
	*
	* @param context
	* @param theURL
	* @return
	*/
	private String getRelativeURL(final FacesContext context, final String theURL) {
		final HttpServletRequest request = ((HttpServletRequest) context.getExternalContext().getRequest());
		String result = theURL;
		if (theURL.startsWith("/")) {
			String path = getPath(request);
			int sub = path.startsWith("/GestioneMareggiate")  ? 1 : 0 ;
			int subpath = StringUtils.countMatches(path, "/") - sub ;
			String pathPrefix = "";
			if (subpath > 0) {
				while (subpath > 0) {
					pathPrefix += "/..";
					subpath--;
				}
				pathPrefix = StringUtils.removeStart(pathPrefix, "/");
			}
			result = pathPrefix + result;
		}
		
		/*String contextPath = context.getExternalContext().getRequestContextPath();
		
		contextPath = contextPath == null ? "/GestioneMareggiate" : contextPath;
		 
		logger.info("ReverseProxyViewHandler:contextPath - " + contextPath);
		result =  result.replaceFirst(contextPath, ""); 
		
		result = StringUtils.removeStart(result, "/");*/
		
		//logger.info("ReverseProxyViewHandler:getRelativeURL - " + result+"\n");
		return result;
	}

	/**
	* Get the url-path from the given request.
	*
	* @param request
	* @return clean path
	*/
	private String getPath(final HttpServletRequest request) {
		try {
		// TODO handle more than two '/'
			//logger.info("ReverseProxyViewHandler:getPath - " + new URI(request.getRequestURI()).getPath());
			return StringUtils.replace(new URI(request.getRequestURI()).getPath(), "//", "/");
		} catch (final URISyntaxException e) {
		// XXX URISyntaxException ignored
			return StringUtils.EMPTY;
		}
	}
}
