package it.epocaricerca.geologia.web.util.file;

import it.epocaricerca.geologia.model.DatoSensore;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;

public class CSVMareografoReader {

	public static List<DatoSensore> readData(File file, Date inizio, Date fine) throws UnexpectedInputException, ParseException, Exception
	{
		FlatFileItemReader<DatoSensore> reader = FlatFileReadearFactory.createCsvReader(file.getAbsolutePath());

		List<DatoSensore> results = new ArrayList<DatoSensore>();

		DatoSensore dato = null;
		reader.open(new ExecutionContext());

		while( (dato = reader.read())!= null)
		{
			if(!(dato.getValue() == Double.NaN) && dato.getTimestamp().after(inizio) && dato.getTimestamp().before(fine))
				results.add(dato);
		}

		return results;
	}
}
