package it.epocaricerca.geologia.web.controller.filter;


import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

// import org.ajax4jsf.exception.FileUploadException;
import org.apache.log4j.Logger;

public class FileUploadSizeFilter implements Filter {


	public static final String MULTIPART = "multipart/";
	public static final String AJAX4JSF_FILTER = "org.ajax4jsf.Filter";
	public static final String PARAM_MAX_REQUEST_SIZE = "maxRequestSize";


	private int uploadLimit = 0;

	private static Logger logger = Logger.getLogger(FileUploadSizeFilter.class);

	public void init(FilterConfig config) throws ServletException {
		String maxRequestSizeParam = config.getInitParameter(PARAM_MAX_REQUEST_SIZE);
		if (maxRequestSizeParam != null) {
			try {
				uploadLimit = Integer.parseInt(maxRequestSizeParam);
			} catch (NumberFormatException nfe) {
			}
		}
	}

	public void doFilter(ServletRequest resourceRequest, ServletResponse resourceResponse, FilterChain filterChain) 
			throws IOException, ServletException {


		// Handle File Upload
		if (isMultipartRequest((HttpServletRequest)resourceRequest)) {

			logger.info("isMultipartContent ");
			HttpServletRequest request = (HttpServletRequest) resourceRequest;
			String contentLength = request.getHeader("Content-Length");
			int contentLengthInt = Integer.parseInt(contentLength);
			if (contentLength != null && uploadLimit > 0
					&& contentLengthInt > uploadLimit) {
				throw new IOException("Multipart request is larger than allowed size");
			}
		}

		filterChain.doFilter(resourceRequest, resourceResponse);
	}

	private boolean isMultipartRequest(HttpServletRequest request) {
		if (!"post".equals(request.getMethod().toLowerCase())) {
			return false;
		}

		String contentType = request.getContentType();
		if (contentType == null) {
			return false;
		}

		if (contentType.toLowerCase().startsWith(MULTIPART)) {
			return true;
		}

		return false;
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

}
