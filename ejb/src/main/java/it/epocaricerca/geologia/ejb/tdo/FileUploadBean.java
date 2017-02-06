package it.epocaricerca.geologia.ejb.tdo;



import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.richfaces.model.UploadItem;


public class FileUploadBean implements Serializable {


	
	//Allegati
	private int MAX_UPLOADS;
	private int uploadsAvailable;
	private Map<String, File> uploadedFiles = new LinkedHashMap<String, File>();
	private String fileToClear;
	private boolean tipoAllegatiBoxRendered;
	
	//Mappa che contiene come chiave il nome del file e come valore il tipo
	private Map<String, String> tipiAllegatiUploadedFiles;
	private List<String> uploadedFilesKeys;
	
	
	public FileUploadBean(int max_upload)
	{
		MAX_UPLOADS = max_upload;
		uploadsAvailable = max_upload;
	}
	
	
	public void addAllegato(UploadItem item)
	{
		getUploadedFiles().put(item.getFileName(), item.getFile());
		getTipiAllegatiUploadedFiles().put(item.getFileName(), null);
		uploadsAvailable--;
	}
	
	public void removeAllegato(String fileToClear)
	{
		getUploadedFiles().remove(fileToClear);
		getTipiAllegatiUploadedFiles().remove(fileToClear);
		uploadsAvailable++;
	}
	
	public void clear()
	{
		getTipiAllegatiUploadedFiles().clear();
		getUploadedFiles().clear();
		setUploadsAvailable(MAX_UPLOADS);
	}
	
	public boolean checkTipiAllegatiPresenti() {
		for (Map.Entry<String, String> entry : getTipiAllegatiUploadedFiles().entrySet()) {
			if (null==entry.getValue()) {
				return false;
			}	
		}
		return true;

	}


	public int getUploadsAvailable() {
		return uploadsAvailable;
	}

	public String getFileToClear() {
		return fileToClear;
	}


	public boolean isTipoAllegatiBoxRendered() {
		if (uploadsAvailable < MAX_UPLOADS)
			return true;
		else
			return false;
	}


	public Map<String, String> getTipiAllegatiUploadedFiles() {
		if (null == tipiAllegatiUploadedFiles )
			tipiAllegatiUploadedFiles = new LinkedHashMap<String, String>();
		return tipiAllegatiUploadedFiles;
	}


	public List<String> getUploadedFilesKeys() {
		List<String> uploadedFilesKeys = new ArrayList<String>();
		uploadedFilesKeys.addAll(getTipiAllegatiUploadedFiles().keySet());
		return uploadedFilesKeys;
	}


	public void setUploadsAvailable(int uploadsAvailable) {
		this.uploadsAvailable = uploadsAvailable;
	}


	public void setUploadedFiles(Map<String, File> uploadedFiles) {
		this.uploadedFiles = uploadedFiles;
	}


	public void setFileToClear(String fileToClear) {
		this.fileToClear = fileToClear;
	}


	public void setTipoAllegatiBoxRendered(boolean tipoAllegatiBoxRendered) {
		this.tipoAllegatiBoxRendered = tipoAllegatiBoxRendered;
	}


	public void setTipiAllegatiUploadedFiles(
			Map<String, String> tipiAllegatiUploadedFiles) {
		this.tipiAllegatiUploadedFiles = tipiAllegatiUploadedFiles;
	}



	public void setUploadedFilesKeys(List<String> uploadedFilesKeys) {
		this.uploadedFilesKeys = uploadedFilesKeys;
	}
	
	public Map<String, File> getUploadedFiles() {
		if (null == uploadedFiles)
			uploadedFiles = new LinkedHashMap<String, File>();
		return uploadedFiles;
	}

	
	
	

}
