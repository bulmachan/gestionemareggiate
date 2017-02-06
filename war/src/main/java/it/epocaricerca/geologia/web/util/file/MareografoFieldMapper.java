package it.epocaricerca.geologia.web.util.file;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import it.epocaricerca.geologia.model.DatoSensore;

import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

public class MareografoFieldMapper  implements FieldSetMapper<DatoSensore> {

	
	public final String giornoLabel = "DATA";
	
	public final String oraLabel = "ORA";
	
	public final String valueLabel = "VALORE";
	
	protected SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HH:mm");
	
	public DatoSensore mapFieldSet(FieldSet fieldSet) throws BindException {

		DatoSensore datoSensore = new DatoSensore();

		String giorno = fieldSet.readString(giornoLabel);
		
		String ora = fieldSet.readString(oraLabel);
		
		String timestamp = giorno+" "+ora;
		
		try {
			datoSensore.setTimestamp(format.parse(timestamp));
		} catch (ParseException e) {
			e.printStackTrace();
			throw new BindException(datoSensore, "");
		}
		try {
			datoSensore.setValue(fieldSet.readDouble(valueLabel));
		}catch (Exception e) {
			datoSensore.setValue(Double.NaN);
		}
		return datoSensore;
	}

}
