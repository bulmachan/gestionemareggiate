package it.epocaricerca.geologia.ejb.tdo;

import java.io.File;
import java.io.Serializable;

public class FileAllegatoBean implements Serializable {
	
	private File file;
	private byte[] bytes;
	private String tipoAllegato;
	private String fileName;
	
	public FileAllegatoBean(File file, String tipoAllegato, String fileName) {
		super();
		this.file = file;
		this.tipoAllegato = tipoAllegato;
		this.fileName = fileName;
	}
	
	
	public FileAllegatoBean(byte[] bytes, String tipoAllegato, String fileName) {
		this.setBytes(bytes);
		this.tipoAllegato = tipoAllegato;
		this.fileName = fileName;
	}


	public FileAllegatoBean() {
	}


	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public String getTipoAllegato() {
		return tipoAllegato;
	}
	public void setTipoAllegato(String tipoAllegato) {
		this.tipoAllegato = tipoAllegato;
	}


	public String getFileName() {
		return fileName;
	}


	public void setFileName(String fileName) {
		this.fileName = fileName;
	}


	public byte[] getBytes() {
		return bytes;
	}


	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

}
