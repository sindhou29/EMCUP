package com.emc.settlement.common;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

public class ParamUtil {
	private static final ObjectMapper mapper = new ObjectMapper();
	public static String toJson(Object value) throws JsonGenerationException, JsonMappingException, IOException {
		String mapperValue = mapper.writeValueAsString(value);
		return mapper.writeValueAsString(value);
		
	}
	public static Object read(String json, String path) {
		return JsonPath.read(json, path);
	}
	public static Object readDate(String json, String path) {
		Long dateLongVal = JsonPath.read(json, path);
		return new Date(dateLongVal);
	}
	
	public static Object readStrDate(String json, String path) {
		String dateStrVal = JsonPath.read(json, path);
   	 	Date dateVal = null;
   	 	SimpleDateFormat UTC_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        try {
        	if(dateStrVal != null)
        	{
       	 		dateVal = UTC_DATE_FORMATTER.parse(dateStrVal);
        	}
        } catch (ParseException e) {	
            e.printStackTrace();
        }
		return dateVal;
	}
	public static List<Date> readDateList(String json) throws JsonParseException, JsonMappingException, IOException {
		List<Long> list = mapper.readValue(json, List.class);
		List<Date> result = new ArrayList<Date>();
		for(Long dtLong: list) {
			result.add(new Date(dtLong));
		}
		return result;
	}

	public static <T> T read(String json, String path, Class<T> targetType) {
		Object obj = JsonPath.read(json, path);//this is actually a HashMap
		//this step converts the HashMap to your POJO   
		final T pojo = mapper.convertValue(obj, targetType);
		return pojo;
	}
	public static <T> T toObject(String json, Class<T> targetType) throws JsonParseException, JsonMappingException, IOException {
		return mapper.readValue(json, targetType);
	}
	public static Object[] wsParams(Object... params) {
		return params;
	}
	/*public static XMLGregorianCalendar newTradingDate() throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(new Date());
		XMLGregorianCalendar tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return tradingDate;
	}*/
	
	public static XMLGregorianCalendar toXMLGregorianCalendar(String json, String path) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		Long dateLongVal = JsonPath.read(json, path);
		c.setTime(new Date(dateLongVal));
		XMLGregorianCalendar tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		return tradingDate;
	}
	public static XMLGregorianCalendar toXMLGregorianCalendar(Date source) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(source);
		XMLGregorianCalendar tradingDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
		
		return tradingDate;
	}
}
