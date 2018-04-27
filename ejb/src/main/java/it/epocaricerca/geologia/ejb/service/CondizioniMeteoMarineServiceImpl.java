package it.epocaricerca.geologia.ejb.service;

import it.epocaricerca.geologia.ejb.dao.CondizioneMeteoManager;
import it.epocaricerca.geologia.ejb.dao.RilevazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.ejb.tdo.CondizioneMeteoBean;
import it.epocaricerca.geologia.ejb.tdo.DatiArpaBean;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Rilevazione;
import it.epocaricerca.geologia.model.VariabileStazione;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import org.apache.log4j.Logger;

@Stateless
@Remote
public class CondizioniMeteoMarineServiceImpl implements CondizioniMeteoMarineService {


	private static Logger logger = Logger.getLogger(CondizioniMeteoMarineServiceImpl.class);


	@EJB
	private CondizioneMeteoManager meteoCRUD;

	@EJB
	private RilevazioneManager rilevazioneManager;

	@EJB
	private VariabileManager variabileCRUD;


	public void salvaCondizioneMeteo(boolean editMode, CondizioneMeteoBean meteoBeanEdit,List<Rilevazione> rilevazioni,
			Map<DatiArpaBean, Boolean> checkedDatiArpaBean, Map<DatiArpaBean, Boolean> datiMareografoBeanToRemove,
			Map<String,DatiArpaBean> datiMareografo) throws EntityNotFoundException{




		Long idCondizioneMeteo = null;


		CondizioneMeteo condizione = new CondizioneMeteo();
		condizione.setDescrizione(meteoBeanEdit.getDescrizione());
		condizione.setInizioValidita(meteoBeanEdit.getInizioValidita());
		condizione.setFineValidita(meteoBeanEdit.getFineValidita());

		condizione.setMaxAltezzaMarea(meteoBeanEdit.getMaxAltezzaMarea());
		condizione.setMaxAltezzaOnda(meteoBeanEdit.getMaxAltezzaOnda());
		condizione.setDurataSopraSoglia(meteoBeanEdit.getDurataSopraSoglia());
		condizione.setMaxAltezzaOndaSignificativa(meteoBeanEdit.getMaxAltezzaOndaSignificativa());
		condizione.setMaxIntensitaVentoPrevalente(meteoBeanEdit.getMaxIntensitaVentoPrevalente());
		condizione.setMaxIntensitaVentoRaffica(meteoBeanEdit.getMaxIntensitaVentoRaffica());
		condizione.setDurataSopraSoglia(meteoBeanEdit.getDurataSopraSoglia());

		if(editMode){
			condizione.setId(meteoBeanEdit.getId());
			condizione.setDescrizione(meteoBeanEdit.getDescrizione());
			condizione.setInizioValidita(meteoBeanEdit.getInizioValidita());
			condizione.setFineValidita(meteoBeanEdit.getFineValidita());
			condizione.setRilevazioni(rilevazioni);
			idCondizioneMeteo = meteoCRUD.update(condizione, meteoBeanEdit.getProvenienzaOnda(),meteoBeanEdit.getEstensione(),meteoBeanEdit.getDirezioneVentoPrevalente());


		}
		else
		{
			idCondizioneMeteo = meteoCRUD.create(condizione, meteoBeanEdit.getProvenienzaOnda(),meteoBeanEdit.getEstensione(),meteoBeanEdit.getDirezioneVentoPrevalente());
		}

		meteoBeanEdit.getDatiArpa();



		//salvo i dati del Mareografo e ARPA

		if(editMode){

			Set<DatiArpaBean> keys = checkedDatiArpaBean.keySet();

			for (DatiArpaBean datiArpaBean : keys) {
				if( checkedDatiArpaBean.get(datiArpaBean) == false){
					logger.info("Rimuovo idCondizioneMeteo "+idCondizioneMeteo+" rilevazione "+datiArpaBean.getId());
					meteoCRUD.removeRilevazione(idCondizioneMeteo, datiArpaBean.getId());
				}
				else
				{
					rilevazioneManager.update(datiArpaBean.getId(), datiArpaBean.getDatiSensore(), datiArpaBean.getMinValue(), datiArpaBean.getMaxValue());
				}
			}


			Set<DatiArpaBean> keysMareografo = datiMareografoBeanToRemove.keySet();

			for (DatiArpaBean dato : keysMareografo) {
				meteoCRUD.removeRilevazione(idCondizioneMeteo, dato.getId() );
			}

			Collection<DatiArpaBean> mareografoToUpdate  = datiMareografo.values();

			for (DatiArpaBean datiArpaBean : mareografoToUpdate) {
				rilevazioneManager.update(datiArpaBean.getId(), datiArpaBean.getDatiSensore(), datiArpaBean.getMinValue(), datiArpaBean.getMaxValue());

			}

		}
		else{

			VariabileStazione variabileMarea = variabileCRUD.findItemByCodice("var1");
			Collection<DatiArpaBean> datiCsv =  datiMareografo.values();

			for (DatiArpaBean dato : datiCsv) {
				long stazioneId = dato.getStazioneId();
				meteoCRUD.insertRilevazione(idCondizioneMeteo, dato.getDatiSensore(), variabileMarea.getId(), dato.getStazioneId(),dato.getMinValue(), dato.getMaxValue());
			}

			Set<DatiArpaBean> keys = checkedDatiArpaBean.keySet();

			for (DatiArpaBean datiArpaBean : keys) {
				if( checkedDatiArpaBean.get(datiArpaBean))
					meteoCRUD.insertRilevazione(idCondizioneMeteo, datiArpaBean.getDatiSensore(), datiArpaBean.getVariabileId(), datiArpaBean.getStazioneId(),datiArpaBean.getMinValue(), datiArpaBean.getMaxValue());

			}
		}

	}
}
