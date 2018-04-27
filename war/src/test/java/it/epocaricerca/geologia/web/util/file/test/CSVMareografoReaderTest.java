package it.epocaricerca.geologia.web.util.file.test;


import it.epocaricerca.geologia.model.DatoSensore;
import it.epocaricerca.geologia.web.util.file.CSVMareografoReader;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class CSVMareografoReaderTest {

	@Before
	public void setup() throws Exception {

	}


	@Test
	public void testgetDate() throws Exception 
	{
		String file = this.getClass().getResource("/ISPRA2619.csv").getFile();
		
		Calendar start = Calendar.getInstance();
		start.set(Calendar.YEAR, 2008);
		
		
		Calendar end = Calendar.getInstance();
		end.set(Calendar.YEAR, 2014);
		
		
		List<DatoSensore> dati = CSVMareografoReader.readData(new File(file), start.getTime(),end.getTime());
		System.out.println("dato "+dati.size());
		Assert.assertNotNull(dati);
		//Assert.assertTrue(dati.size() > 0 );
		
		
	}
}
