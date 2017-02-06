package it.epocaricerca.geologia.web.util;

public class StringUtils {

	public static boolean notEmpty(String s) {
		return (s != null && s.length() > 0);
	}
	
	public static String convertNumberToTwoDigits(String value) {
		if (value.length() == 1)
			return "0"+value;
		else return value;
	}
	
	public static String convertNumberToTwoDigits(int value) {
		String result = String.valueOf(value);
		if (result.length() == 1)
			return "0"+result;
		else return result;
	}

}
