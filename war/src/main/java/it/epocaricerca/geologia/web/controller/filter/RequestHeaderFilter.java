package it.epocaricerca.geologia.web.controller.filter;

import java.io.IOException;
import java.util.Map;

import org.apache.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Cookie;


public class RequestHeaderFilter implements Filter {
	
	private static Logger logger = Logger.getLogger(RequestHeaderFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

		RequestHeaderWrapper modifiedRequest = new RequestHeaderWrapper((HttpServletRequest) request);

		modifiedRequest.addHeader("codicefiscale", "FNTDMN85E29F157B");
		modifiedRequest.addHeader("nome", "fontana");
		modifiedRequest.addHeader("cognome", "damiano");
		modifiedRequest.addHeader("email", "damiano.fontana@epocaricerca.it");
		modifiedRequest.addHeader("trustlevel", "Alto");
		modifiedRequest.addHeader("policylevel", "Alto");
		modifiedRequest.addHeader("authenticatingauthority", "rer");
		modifiedRequest.addHeader("authenticationmethod", "openId");

		filterChain.doFilter(modifiedRequest, response);
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
