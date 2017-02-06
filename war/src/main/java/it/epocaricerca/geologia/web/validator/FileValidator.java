package it.epocaricerca.geologia.web.validator;

/*import java.lang.String*/
import java.io.File;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.apache.tika.mime.MimeTypes;


public class FileValidator {
	
private static Logger logger = Logger
			.getLogger(FileValidator.class);

	public Boolean Validate(File file, String filename) throws IOException, TikaException {
		
		String mimeType = "Undetermined"; 
		Tika tika = new Tika();
		
		if (file!=null) {
			mimeType = tika.detect(file);
			logger.info("fileUploadListener File " + filename + " Detected MIME type = " + mimeType);
			
			if(!mimeType.contains("application/vnd.oasis.opendocument.text") && !mimeType.contains("application/pdf") && !mimeType.contains("application/xhtml+xml") 
				&& !mimeType.contains("image/png") &&  !mimeType.contains("image/jpg") && !mimeType.contains("image/jpeg") && !mimeType.contains("text/plain")) {
				logger.info("FORM_ERROR_IOFILE_NOTVALID"); 			
				return false;				
			} else {
				return true;
			}
		}
		return false;
	}

}
