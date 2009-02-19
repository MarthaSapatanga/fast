package eu.morfeoproject.fast.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * Provide several functions to format the string representation of a date
 * into a Date and vice versa.
 * @author irivera
 */
public class FormatterUtil {
	
	static Logger logger = Logger.getLogger(FormatterUtil.class);
	
	public  static SimpleDateFormat ISO8601FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
	
	/**
	 * 
	 * @param date
	 * @return
	 */
	public static String formatDateISO8601(Date date) {
		return ISO8601FORMAT.format(date);
	}
	
	/**
	 * 
	 * @param text
	 * @return
	 */
	public static Date parseDateISO8601(String text) {
		try {
			return ISO8601FORMAT.parse(text);
		} catch (ParseException e) {
			logger.warn("Cannot parse date with value: "+text);
			return null;
		}
	}
	
}
