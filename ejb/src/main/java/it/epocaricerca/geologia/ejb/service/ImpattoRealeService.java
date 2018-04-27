package it.epocaricerca.geologia.ejb.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.io.ParseException;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.TipologiaDannoBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.TipoAllegato;


public interface ImpattoRealeService {

	void salvaImpattoReale(boolean editMode, ImpattoReale impattoReale, String fonte,
						 List<TipologiaDannoBean> impattiLocali,List<TipologiaDannoBean> tipologieDannoToRemoveList, Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati, List<Allegato> attachmentsToRemoveList) 
								 throws IOException, EntityNotFoundException, ParseException, Exception;
}
