package it.epocaricerca.geologia.web.util;

import it.epocaricerca.geologia.ejb.dao.AllegatoManager;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.Allegato;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

public class AttachmentUtils {

	public static void downloadAttachment(Allegato allegato) {
		AllegatoManager allegatoManager = JNDIUtils.retrieveEJB(JNDIUtils.AllegatoManagerName, JNDIUtils.AllegatoManagerName);

		byte[] file = allegatoManager.getAllegatoAttachment(allegato.getId());

		FacesContext faces = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
		//response.setContentType("application/pdf");
		response.setContentLength(file.length);
		response.setHeader( "Content-disposition", "inline; filename=\""+allegato.getNome()+"\"");
		try {
			ServletOutputStream out;
			out = response.getOutputStream();
			out.write(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		faces.responseComplete();
	}


	public static void downloadFile(File file) {

		FacesContext faces = FacesContext.getCurrentInstance();
		if(faces != null){
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			response.setContentType("application/pdf");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			//response.setContentLength((int) FileUtils.sizeOf(file));
			response.setHeader( "Content-disposition", "attachment; filename="+file.getName());
			try {
				ServletOutputStream out;
				out = response.getOutputStream();

				IOUtils.copy(new FileInputStream(file),out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			faces.responseComplete();
		}
	}
}
