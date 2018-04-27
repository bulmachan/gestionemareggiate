package it.epocaricerca.geologia.web.util.file;



import it.epocaricerca.geologia.model.DatoSensore;

import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.FileSystemResource;

public class FlatFileReadearFactory {


	public static final char csvDelimiter = ';';
	
	
	
	public static FlatFileItemReader<DatoSensore> createCsvReader(String csvFilePath)
	{
		FlatFileItemReader<DatoSensore> itemReader = new FlatFileItemReader<DatoSensore>();
		itemReader.setResource(new FileSystemResource(csvFilePath));
		itemReader.setLinesToSkip(5);
		itemReader.setEncoding("UTF-8");
		DefaultLineMapper<DatoSensore> lineMapper = new DefaultLineMapper<DatoSensore>();

		final DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
		tokenizer.setDelimiter(csvDelimiter);
		

		lineMapper.setLineTokenizer(tokenizer);
		lineMapper.setFieldSetMapper(new MareografoFieldMapper());
		itemReader.setLineMapper(lineMapper);
		itemReader.setSkippedLinesCallback(new LineCallbackHandler() {
			
			public void handleLine(String line) {
				tokenizer.setNames(line.split(""+csvDelimiter));
				
			}
		});
		
		return itemReader;
	}
	
	
}
