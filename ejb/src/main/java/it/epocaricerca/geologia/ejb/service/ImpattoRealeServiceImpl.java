package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.DannoManager;
import it.epocaricerca.geologia.ejb.dao.ImpattoRealeManager;
import it.epocaricerca.geologia.ejb.tdo.FileAllegatoBean;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.ejb.tdo.TipologiaDannoBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.ImpattoReale;
import it.epocaricerca.geologia.model.TipoAllegato;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.vividsolutions.jts.io.ParseException;

@Stateless
@Remote
public class ImpattoRealeServiceImpl implements ImpattoRealeService {

	private static Logger logger = Logger.getLogger(ImpattoRealeServiceImpl.class);

	@EJB
	private ImpattoRealeManager impattoRealeManager;

	@EJB
	private DannoManager dannoManager;


	public void salvaImpattoReale(boolean editMode, ImpattoReale impattoReale, String fonte,
			List<TipologiaDannoBean> danniToAdd,
			List<TipologiaDannoBean> danniToRemove,
			Map<String, TipoAllegato> tipiAllegato, FileUploadBean allegati, 
			List<Allegato> attachmentsToRemoveList) throws ParseException, Exception {

		Long impattoRealeId = null;

		if (editMode) {
			impattoRealeId = impattoRealeManager.update(impattoReale, fonte);
		} 
		else {
			impattoRealeId = impattoRealeManager.create(impattoReale, fonte);
		}


		// GESTIONE ALLEGATI
		if (!allegati.getUploadedFiles().isEmpty()) {
			Allegato allegato;
			String nomeFile;
			String nomeTipoAllegato;
			TipoAllegato tipoAllegato;
			for (Map.Entry<String, File> entry : allegati.getUploadedFiles().entrySet()) {
				nomeFile = entry.getKey();
				allegato = new Allegato();
				allegato.setNome(nomeFile);
				nomeTipoAllegato = allegati.getTipiAllegatiUploadedFiles().get(nomeFile);
				tipoAllegato = tipiAllegato.get(nomeTipoAllegato);
				allegato.setTipo(tipoAllegato);

				allegato.setFile(FileUtils.readFileToByteArray(entry.getValue()));


				impattoRealeManager.insertAllegato(impattoRealeId, allegato);
			}
		}

		if(editMode && !attachmentsToRemoveList.isEmpty())
		{
			for (Allegato allegato : attachmentsToRemoveList) {
				impattoRealeManager.removeAllegato(impattoRealeId, allegato.getId());
			}
		}


		logger.info("Inserito Impatto Reale "+impattoRealeId);
		// 2. insert danni
		// Se sono in editMode prima rimuovo quelli segnati per la rimozione
		if (editMode && null!=danniToRemove && !danniToRemove.isEmpty()) {
			for (TipologiaDannoBean tdbToRemove : danniToRemove) {
				impattoRealeManager.removeDanno(impattoRealeId, tdbToRemove.getId());
			}
		}
		Danno danno;
		for (TipologiaDannoBean tdb : danniToAdd) {
			danno = new Danno();
			// skippo quelli che hanno un id non null perchè sono già presenti su db
			if ( null == tdb.getId()) {
				logger.info("Salvo  Danno "+tdb.getGeometryText()+" "+tdb.getLocalita());

				Long dannoId = impattoRealeManager.insertDanno(impattoRealeId, danno, tdb.getLocalita().getNome(), tdb.getTipoDanno(), tdb.getGeometryText());
				// 3. insert allegati
				// TODO: eventuale gestione allegati e geometrie in editMode
				for ( FileAllegatoBean fab : tdb.getFileAllegati() ) {
					Allegato allegato = new Allegato();
					allegato.setFile(FileUtils.readFileToByteArray(fab.getFile()));
					allegato.setNome(fab.getFileName());
					dannoManager.insertAllegato(dannoId, allegato, fab.getTipoAllegato());
				}
			}
			else {
				// TODO: eventuale gestione allegati e geometrie in editMode
				// il danno è già su db ma devo controllare i suoi allegati o la sua geometria
			}

		}


	}

}
