package it.epocaricerca.geologia.ejb.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import it.epocaricerca.geologia.ejb.dao.DannoSTBManager;
import it.epocaricerca.geologia.ejb.dao.RelazioneSTBManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.DannoSTBBean;
import it.epocaricerca.geologia.ejb.tdo.FileAllegatoBean;
import it.epocaricerca.geologia.ejb.tdo.FileUploadBean;
import it.epocaricerca.geologia.model.Allegato;
import it.epocaricerca.geologia.model.DannoSTB;
import it.epocaricerca.geologia.model.RelazioneSTB;
import it.epocaricerca.geologia.model.TipoAllegato;

import org.apache.commons.io.FileUtils;

import com.vividsolutions.jts.io.ParseException;


@Stateless
@Remote
public class RelazioniTecnicheSTBServiceImpl implements RelazioniTecnicheSTBService {

	@EJB
	private RelazioneSTBManager relazioneSTBManager;
	
	@EJB
	private DannoSTBManager dannoSTBManager;

	
	public void salvaRelazioneTecnica(boolean editMode, 
										RelazioneSTB relazioneSTB, String stb, String fonte, List<DannoSTBBean> tipologieDannoToRemoveList,
										List<DannoSTBBean> tipiDannoTemp, Map<String, TipoAllegato> tipiAllegato, 
										FileUploadBean allegati, List<Allegato> attachmentsToRemoveList) throws EntityNotFoundException, ParseException, Exception {
					

				

				Long idRelazioneTecnica = null;

				// 1. insert RelazioneSTB
				if(!editMode){
				


					// per ora solo CREATE senza UPDATE
					idRelazioneTecnica = relazioneSTBManager.create(relazioneSTB, stb, fonte);
				}else
				{
					idRelazioneTecnica = relazioneSTBManager.update(relazioneSTB, stb, fonte);
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


						relazioneSTBManager.insertAllegato(idRelazioneTecnica, allegato);
					}
				}

				if(editMode && !attachmentsToRemoveList.isEmpty())
				{
					for (Allegato allegato : attachmentsToRemoveList) {
						relazioneSTBManager.removeAllegato(idRelazioneTecnica, allegato.getId());
					}
				}

				/* LOGICA PER CREATE + EDIT MODE*/
				// 2. insert danni
				// Se sono in editMode prima rimuovo quelli segnati per la rimozione
				if (editMode && null!=tipologieDannoToRemoveList && !tipologieDannoToRemoveList.isEmpty()) {
					for (DannoSTBBean tdbToRemove : tipologieDannoToRemoveList) {
						relazioneSTBManager.removeDannoSTB(idRelazioneTecnica, tdbToRemove.getId());
				
					}
				}
		

				DannoSTB danno;
				for (DannoSTBBean tdb : tipiDannoTemp) {
					danno = new DannoSTB();

					if ( null == tdb.getId()){
						danno.setVolumiErosi(tdb.getVolumiErosi());
						danno.setLunghezzaTratto(tdb.getLunghezzaTratto());
						danno.setStimaCostiDanni(tdb.getStimaCostiDanni());
						danno.setStimaCostiRipascimenti(tdb.getStimaCostiRipascimenti());
						danno.setErosioni(tdb.isErosioni());
						danno.setTracimazioni(tdb.isTracimazioni());
						danno.setInondazioni(tdb.isInondazioni());
						danno.setDanniDifese(tdb.isDanniDifese());
						danno.setDanniInfrastrutture(tdb.isDanniInfrastrutture());
			
						danno.setDescrizione(tdb.getDescrizione());

						Long dannoId = relazioneSTBManager.insertDannoSTB(idRelazioneTecnica, danno, tdb.getLocalita().getNome(), tdb.getGeometryText());

						// 3. insert allegati
						// TODO: eventuale gestione allegati e geometrie in editMode
						for ( FileAllegatoBean fab : tdb.getFileAllegati() ) {
							Allegato allegato = new Allegato();
							allegato.setFile(FileUtils.readFileToByteArray(fab.getFile()));
							allegato.setNome(fab.getFileName());
							dannoSTBManager.insertAllegato(dannoId, allegato, fab.getTipoAllegato());
						}
					}
					else {
						// TODO: eventuale gestione allegati e geometrie in editMode
						// il danno è già su db ma devo controllare i suoi allegati o la sua geometria
					}

				}




	}

}
