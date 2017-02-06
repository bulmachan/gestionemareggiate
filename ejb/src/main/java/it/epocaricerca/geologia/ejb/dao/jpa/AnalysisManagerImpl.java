package it.epocaricerca.geologia.ejb.dao.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import it.epocaricerca.geologia.ejb.dao.AnalysisManager;
import it.epocaricerca.geologia.ejb.dao.TipoDannoManager;
import it.epocaricerca.geologia.ejb.tdo.AggregationType;
import it.epocaricerca.geologia.ejb.utils.JNDIUtils;
import it.epocaricerca.geologia.model.CondizioneMeteo;
import it.epocaricerca.geologia.model.Danno;
import it.epocaricerca.geologia.model.Mareggiata;
import it.epocaricerca.geologia.model.TipoDanno;

@Stateless
@Remote
public class AnalysisManagerImpl extends GenericManager  implements AnalysisManager {


	private static Logger logger = Logger.getLogger(AnalysisManagerImpl.class);

	@PersistenceContext(unitName="SMPersistenceLayer")
	private EntityManager em;

	@EJB
	private TipoDannoManager tipoDannoManager;

	public List<Object[]> getMareggiateConSenzaImpatto(Date start_date, Date end_date, AggregationType aggregationType) {

		Query query_aggrgazione= null;
		Query query_mareggiate_con_impatti = null;
		Query query_mareggiate_senza_impatti = null;

		if(aggregationType == AggregationType.MONTH){
			query_aggrgazione = em.createQuery("SELECT DISTINCT STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita)) FROM Mareggiata m WHERE m.inizioValidita BETWEEN :start_date AND :end_date  ORDER BY STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita))");

			query_mareggiate_con_impatti = em.createQuery("SELECT  STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita)), count(m) FROM Mareggiata m WHERE m.impattiReali IS NOT EMPTY AND  m.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(m.inizioValidita))||'-'||str(MONTH(m.inizioValidita)) ORDER BY STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita))");

			query_mareggiate_senza_impatti = em.createQuery("SELECT  STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita)), count(m) FROM Mareggiata m WHERE m.impattiReali IS EMPTY AND  m.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(m.inizioValidita))||'-'||str(MONTH(m.inizioValidita)) ORDER BY STR(YEAR(m.inizioValidita))||'-'||STR(MONTH(m.inizioValidita))");

		}

		else 
		{
			query_aggrgazione = em.createQuery("SELECT DISTINCT STR(YEAR(m.inizioValidita)) FROM Mareggiata m WHERE m.inizioValidita BETWEEN :start_date AND :end_date  ORDER BY STR(YEAR(m.inizioValidita))");

			query_mareggiate_con_impatti = em.createQuery("SELECT  STR(YEAR(m.inizioValidita)), count(m) FROM Mareggiata m WHERE m.impattiReali IS NOT EMPTY AND  m.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(m.inizioValidita)) ORDER BY STR(YEAR(m.inizioValidita))");

			query_mareggiate_senza_impatti = em.createQuery("SELECT  STR(YEAR(m.inizioValidita)), count(m) FROM Mareggiata m WHERE m.impattiReali IS EMPTY AND  m.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(m.inizioValidita)) ORDER BY STR(YEAR(m.inizioValidita))");

		}

		query_aggrgazione.setParameter("start_date", start_date);
		query_aggrgazione.setParameter("end_date", end_date);

		query_mareggiate_con_impatti.setParameter("start_date", start_date);
		query_mareggiate_con_impatti.setParameter("end_date", end_date);

		query_mareggiate_senza_impatti.setParameter("start_date", start_date);
		query_mareggiate_senza_impatti.setParameter("end_date", end_date);



		List<String> aggregazioni = query_aggrgazione.getResultList();


		if(aggregazioni.size() == 0)
			return new LinkedList<Object[]>();


			List<Object[]> mareggiate_con_impatti =  query_mareggiate_con_impatti.getResultList();


			List<Object[]> mareggiate_senza_impatti =  query_mareggiate_senza_impatti.getResultList();


			Map<String,Object[]> result = new LinkedHashMap<String, Object[]>();

			for (String aggregazione : aggregazioni) {
				result.put(aggregazione, new Object[]{aggregazione,0L,0L});
			}

			for (Object[] objects : mareggiate_con_impatti) {
				Object[] value = result.get(objects[0]);
				value[1] = (Long) objects[1];
				result.put((String) objects[0], value);
			}

			for (Object[] objects : mareggiate_senza_impatti) {
				Object[] value = result.get(objects[0]);
				value[2] = (Long) objects[1];
				result.put((String) objects[0], value);
			}

			return new LinkedList<Object[]>(result.values());


	}

	public List<Object[]> getAvvisiMeteoConSenzaImpatto(Date start_date, Date end_date, AggregationType aggregationType) {

		Query query_aggregazione= null;
		Query query_avvisi_meteo_con_impatti = null;
		Query query_avvisi_meteo_senza_impatti = null;
		Query query_avvisi_meteo_senza_mareggiate = null;

		if(aggregationType == AggregationType.MONTH){
			query_aggregazione = em.createQuery("SELECT DISTINCT STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita)) FROM PrevisioneMeteo p WHERE p.inizioValidita BETWEEN :start_date AND :end_date  ORDER BY STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita))");

			query_avvisi_meteo_con_impatti = em.createQuery("SELECT  STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NOT NULL AND p.mareggiata.impattiReali IS NOT EMPTY AND  p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita))||'-'||str(MONTH(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita))");

			query_avvisi_meteo_senza_impatti = em.createQuery("SELECT  STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NOT NULL AND p.mareggiata.impattiReali IS EMPTY AND  p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita))||'-'||str(MONTH(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita))");

			query_avvisi_meteo_senza_mareggiate = em.createQuery("SELECT  STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NULL AND p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita))||'-'||str(MONTH(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))||'-'||STR(MONTH(p.inizioValidita))");

		}

		else 
		{
			query_aggregazione = em.createQuery("SELECT DISTINCT STR(YEAR(p.inizioValidita)) FROM PrevisioneMeteo p WHERE p.inizioValidita BETWEEN :start_date AND :end_date  ORDER BY STR(YEAR(p.inizioValidita))");

			query_avvisi_meteo_con_impatti = em.createQuery("SELECT  STR(YEAR(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NOT NULL AND p.mareggiata.impattiReali IS NOT EMPTY AND  p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))");

			query_avvisi_meteo_senza_impatti = em.createQuery("SELECT  STR(YEAR(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NOT NULL AND p.mareggiata.impattiReali IS EMPTY AND  p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))");

			query_avvisi_meteo_senza_mareggiate = em.createQuery("SELECT  STR(YEAR(p.inizioValidita)), count(p) FROM PrevisioneMeteo p WHERE p.mareggiata IS NULL AND  p.inizioValidita BETWEEN :start_date AND :end_date GROUP BY STR(YEAR(p.inizioValidita)) ORDER BY STR(YEAR(p.inizioValidita))");

		}

		query_aggregazione.setParameter("start_date", start_date);
		query_aggregazione.setParameter("end_date", end_date);

		query_avvisi_meteo_con_impatti.setParameter("start_date", start_date);
		query_avvisi_meteo_con_impatti.setParameter("end_date", end_date);

		query_avvisi_meteo_senza_impatti.setParameter("start_date", start_date);
		query_avvisi_meteo_senza_impatti.setParameter("end_date", end_date);

		query_avvisi_meteo_senza_mareggiate.setParameter("start_date", start_date);
		query_avvisi_meteo_senza_mareggiate.setParameter("end_date", end_date);



		List<String> aggregazioni = query_aggregazione.getResultList();


		if(aggregazioni.size() == 0)
			return new LinkedList<Object[]>();


			List<Object[]> avvisi_meteo_con_impatti =  query_avvisi_meteo_con_impatti.getResultList();

			List<Object[]> avvisi_meteo_senza_impatti =  query_avvisi_meteo_senza_impatti.getResultList();

			List<Object[]> avvisi_meteo_senza_mareggiate =  query_avvisi_meteo_senza_mareggiate.getResultList();


			Map<String,Object[]> result = new LinkedHashMap<String, Object[]>();

			for (String aggregazione : aggregazioni) {
				result.put(aggregazione, new Object[]{aggregazione,0L,0L,0L});
			}

			for (Object[] objects : avvisi_meteo_con_impatti) {
				Object[] value = result.get(objects[0]);
				value[1] = (Long) objects[1];
				result.put((String) objects[0], value);
			}

			for (Object[] objects : avvisi_meteo_senza_impatti) {
				Object[] value = result.get(objects[0]);
				value[2] = (Long) objects[1];
				result.put((String) objects[0], value);
			}

			for (Object[] objects : avvisi_meteo_senza_mareggiate) {
				Object[] value = result.get(objects[0]);
				value[3] = (Long) objects[1];
				result.put((String) objects[0], value);
			}

			return new LinkedList<Object[]>(result.values());
	}

	public List<Object[]> getDatiMeteo(Date start_date, Date end_date) {

		Query query_mareggiate = em.createQuery("SELECT m FROM Mareggiata m WHERE m.condizioniMeteo IS NOT EMPTY AND m.inizioValidita BETWEEN :start_date AND :end_date ORDER BY m.inizioValidita");

		query_mareggiate.setParameter("start_date", start_date);
		query_mareggiate.setParameter("end_date", end_date);

		List<Mareggiata> mareggiate =  query_mareggiate.getResultList();

		LinkedList<Object[]> result = new LinkedList<Object[]>();

		for (Mareggiata mareggiata : mareggiate) {
			Object[] row = new Object[]{mareggiata.getCodice(), mareggiata.getCondizioniMeteo().get(0).getMaxAltezzaMarea(), 
					mareggiata.getCondizioniMeteo().get(0).getMaxAltezzaOnda(), 0.8, 3};
			result.add(row);
		}

		return result;
	}

	public List<Object[]> getNumDanniPerLocalita(Date start_date, Date end_date) {

		List<TipoDanno> tipiDanno = this.tipoDannoManager.selectAll();

		Query query_num_danni_per_localita = em.createQuery("SELECT d.localita.nome, d.tipoDanno.nome, COUNT(d) FROM ImpattoReale i INNER JOIN i.danni d WHERE i.inizioValidita BETWEEN :start_date AND :end_date GROUP BY d.localita.nome, d.tipoDanno.nome");
		query_num_danni_per_localita.setParameter("start_date", start_date);
		query_num_danni_per_localita.setParameter("end_date", end_date);
		
		List<Object[]> num_danni_per_localita =  query_num_danni_per_localita.getResultList();

		LinkedHashMap<String, LinkedHashMap<String,Long>> partial_result = new LinkedHashMap<String, LinkedHashMap<String,Long>>();

		List<String> headers = new ArrayList<String>();
		headers.add("Località");

		for (TipoDanno tipoDanno : tipiDanno) {
			headers.add(tipoDanno.getNome());
		}

		LinkedList<Object[]> result = new LinkedList<Object[]>();
		result.add(headers.toArray(new Object[]{}));

		for (Object[] objects : num_danni_per_localita) {
			if(partial_result.containsKey(objects[0])){
				LinkedHashMap<String,Long> tipiDannoMap = partial_result.get(objects[0]);
				tipiDannoMap.put((String)objects[1], (Long)objects[2]);
			} else {
				LinkedHashMap<String,Long> tipiDannoMap = new LinkedHashMap<String,Long>();
				for (TipoDanno tipoDanno : tipiDanno) {
					tipiDannoMap.put(tipoDanno.getNome(), 0L);
				}
				tipiDannoMap.put((String)objects[1], (Long)objects[2]);
				partial_result.put((String)objects[0], tipiDannoMap);
			}
		}
		
		for (String key : partial_result.keySet()) {
			LinkedHashMap<String,Long> tipiDannoMap = partial_result.get(key);
			
			List<Object> lineResult = new ArrayList(tipiDannoMap.values());
			lineResult.add(0, key);
			result.add(lineResult.toArray(new Object[]{}));
		}
		
		return result;
	}

	public List<Object[]> getNumDanniTotali(Date start_date, Date end_date) {
		
		List<TipoDanno> tipiDanno = this.tipoDannoManager.selectAll();
		
		Map<String, Object[]> results = new LinkedHashMap<String, Object[]>();
		for (TipoDanno tipoDanno : tipiDanno) {
			results.put(tipoDanno.getNome(), new Object[]{tipoDanno.getNome(),0L});
		}

		Query query_num_danni_totali = em.createQuery("SELECT d.tipoDanno.nome, COUNT(d) FROM ImpattoReale i INNER JOIN i.danni d WHERE i.inizioValidita BETWEEN :start_date AND :end_date GROUP BY d.tipoDanno.nome");
		query_num_danni_totali.setParameter("start_date", start_date);
		query_num_danni_totali.setParameter("end_date", end_date);
		
		List<Object[]> num_danni_per_localita =  query_num_danni_totali.getResultList();
		
		for (Object[] objects : num_danni_per_localita) {
			results.get(objects[0])[1] = objects[1];
		}
		
		return new LinkedList<Object[]>(results.values());
	}

	public List<Danno> getDanniByData(Date start_date, Date end_date) {
		
		Query query_danni_by_data = em.createQuery("SELECT d FROM ImpattoReale i INNER JOIN i.danni d WHERE i.inizioValidita BETWEEN :start_date AND :end_date");
		query_danni_by_data.setParameter("start_date", start_date);
		query_danni_by_data.setParameter("end_date", end_date);
		
		return query_danni_by_data.getResultList();
	}

}
