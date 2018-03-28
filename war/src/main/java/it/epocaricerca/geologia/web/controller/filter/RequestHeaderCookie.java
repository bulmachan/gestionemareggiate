package it.epocaricerca.geologia.web.controller.filter;

import java.io.IOException;
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


public class RequestHeaderCookie implements Filter {
	
	private static Logger logger = Logger.getLogger(RequestHeaderFilter.class);

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
					
		rewriteCookieToHeader((HttpServletRequest) request, (HttpServletResponse) response);
		
		RequestHeaderWrapper modifiedRequest = new RequestHeaderWrapper((HttpServletRequest) request);		
		filterChain.doFilter(modifiedRequest, response);
	}

	private void rewriteCookieToHeader(HttpServletRequest request, HttpServletResponse response) {
		String sessionid = request.getSession().getId();
		String contextPath = request.getContextPath();
		String secure = "";
		//if (request.isSecure()) {
			secure = "; Secure"; 
		//}
		//logger.info("response.setHeader(\"SET-COOKIE\", \"JSESSIONID=\"" + sessionid + "; Path=\"" + contextPath + "\"; HttpOnly" + secure);
		response.setHeader("SET-COOKIE", "JSESSIONID=" + sessionid
						 + "; Path=" + contextPath + "; HttpOnly" + secure);
	}
	
	public void destroy() {
		// TODO Auto-generated method stub
		
	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub
		
	}
	
}
