package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.DannoSTBBean;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.util.List;
import java.util.Map;

import com.vividsolutions.jts.io.ParseException;

public interface RelazioniTecnicheSTBService {

	public void salvaRelazioneTecnica(boolean editMode, 
			RelazioneSTB relazioneSTB, String stb, String fonte, List<DannoSTBBean> tipologieDannoToRemoveList,
			List<DannoSTBBean> tipiDannoTemp, Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati, List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, ParseException, Exception;
}
