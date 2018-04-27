package it.epocaricerca.geologia.ejb.dao.network;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Stateless;
import javax.annotation.*;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.springframework.http.client.CommonsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

/*import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;*/







import it.epocaricerca.geologia.ejb.dao.SensorDataSourceManager;
import it.epocaricerca.geologia.ejb.dao.StazioneManager;
import it.epocaricerca.geologia.ejb.dao.VariabileManager;
import it.epocaricerca.geologia.ejb.dao.exception.EntityNotFoundException;
import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.model.Stazione;
import it.epocaricerca.geologia.model.VariabileStazione;

@Stateless
@Remote
public class ArpaDataSource implements SensorDataSourceManager {

	@EJB
	private StazioneManager stazioneManager;

	@EJB
	private VariabileManager variabileManager;

	//data format 2007-05-23 09:00:00
	//private final String url = "http://www.smr.arpa.emr.it/services/arkiweb/data?datasets[]={dataset}&postprocess=json&query=area: VM2,{stazione}; product: VM2,{variabile}; reftime: > {startDate}, < {endDate}";

	private final String url = "http://www.smr.arpa.emr.it/services/arkiweb/data?datasets[]={dataset}&query=reftime: > {startDate}, < {endDate}; area:VM2,{stazione}; product:VM2,{variabile}&postprocess=json";

	
	
	private RestTemplate restTemplate;


	private static Logger logger = Logger.getLogger(ArpaDataSource.class);

	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormatJson = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	private String username = "mareggiate";
	private String password = "kaiseoH8";

	@PostConstruct
	void postConstruct()
	{
		HttpClient client = new HttpClient();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username,password);
		client.getState().setCredentials( new AuthScope("www.smr.arpa.emr.it", 80, AuthScope.ANY_REALM),credentials);
		CommonsClientHttpRequestFactory commons = new CommonsClientHttpRequestFactory(client);

		restTemplate = new RestTemplate(commons);
	}

	public List<DatoSensore> getDataFromVariabileStazione(long idStazione,long idVariabile,Date start,Date end) throws Exception {

		Stazione stazione = stazioneManager.findItemById(idStazione);

		VariabileStazione variabile = variabileManager.findItemById(idVariabile);


		if(stazione == null || variabile == null)
			throw new EntityNotFoundException("stazione == null || variabile == null");
		List<DatoSensore> results = new ArrayList<DatoSensore>();

		try{

			String jsonResponse = restTemplate.getForObject(url, String.class, stazione.getRete(),dateFormat.format(start),dateFormat.format(end),stazione.getCodice(),variabile.getCodice());

			UriTemplate uriTemplate = new UriTemplate(url);
			
			java.net.URI expanded = uriTemplate.expand(stazione.getRete(),dateFormat.format(start),dateFormat.format(end),stazione.getCodice(),variabile.getCodice());
			
			logger.info(""+expanded);
			

			JsonFactory f = new MappingJsonFactory();
			JsonParser jp = f.createJsonParser(jsonResponse);


			findFeaturesObject(jp);

			JsonToken token= null;

			while ((token = jp.nextToken()) != JsonToken.END_ARRAY) {
				String fieldname = jp.getCurrentName();
				if(fieldname!= null && fieldname.equalsIgnoreCase("properties"))
				{
					DatoSensore dato = new DatoSensore();
					/*while ((token = jp.nextToken()) != JsonToken.END_OBJECT) {
						String namefield = jp.getCurrentName();
						jp.nextToken();
						if ("value".equals(namefield)) {
							dato.setValue(jp.getValueAsDouble());
						} 

						if ("datetime".equals(namefield)) {
							System.out.println(namefield+" "+jp.getText());
							dato.setTimestamp(dateFormatJson.parse(jp.getText()));
						} 
					}*/
					
					token = jp.nextToken();
					
					JsonNode node = jp.readValueAsTree();
					dato.setValue(node.get("value").asDouble());
					dato.setTimestamp(dateFormatJson.parse(node.get("datetime").asText()));
					
					results.add(dato);
				}
				else if(token == JsonToken.START_OBJECT){
					skipObject(jp);
				}
			}

			logger.info("Find "+results.size()+" value for "+stazione.getCodice()+" variabile "+variabile.getCodice());
		}catch(Exception e)
		{
			e.printStackTrace();
			logger.error(e.getMessage());
			throw new Exception(e.getMessage());
		}
		return results;

	}

	protected void skipObject(JsonParser jp) throws JsonParseException, IOException
	{
		while (jp.nextToken() != JsonToken.END_OBJECT) 
		{

		}
	}

	protected void findFeaturesObject(JsonParser jp) throws JsonParseException, IOException
	{
		JsonToken token;
		//skip finche' non incontro start array e features
		while ((token = jp.nextToken()) != JsonToken.START_ARRAY || !jp.getCurrentName().equalsIgnoreCase("features")) 
		{

		}
		

	}

}
