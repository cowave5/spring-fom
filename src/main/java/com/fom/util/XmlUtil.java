package com.fom.util;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

/**
 * 
 * @author shanhm
 *
 */
public class XmlUtil {

	/**
	 * 
	 * @param el
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static final String getString(Element el, String key, String defaultValue){
		Element e = el.element(key);
		if(e == null){
			return defaultValue;
		}
		String value = e.getTextTrim();
		if(StringUtils.isBlank(value)){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * @param el
	 * @param key
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return
	 */
	public static final int getInt(Element el, String key, int defaultValue, int min, int max){
		Element e = el.element(key);
		if(e == null){
			return defaultValue;
		}
		int value = 0;
		try{
			value = Integer.parseInt(e.getTextTrim());
		}catch(Exception e1){
			return defaultValue;
		}

		if(value < min || value > max){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * @param el
	 * @param key
	 * @param defaultValue
	 * @param min
	 * @param max
	 * @return
	 */
	public static final long getLong(Element el, String key, long defaultValue, long min, long max){
		Element e = el.element(key);
		if(e == null){
			return defaultValue;
		}
		long value = 0;
		try{
			value = Long.parseLong(e.getTextTrim());
		}catch(Exception e1){
			return defaultValue;
		}

		if(value < min || value > max){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * @param el
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public static final boolean getBoolean(Element el, String key, boolean defaultValue){
		Element e = el.element(key);
		if(e == null){
			return defaultValue;
		}

		boolean value = false;
		try{
			value = Boolean.parseBoolean(e.getTextTrim());
		}catch(Exception e1){
			return defaultValue;
		}
		return value;
	}

	/**
	 * 
	 * @param doc
	 * @param xml
	 * @throws Exception
	 */
	public static final void writeDocToFile(Document doc, File xml) throws Exception { 
		OutputFormat formater=OutputFormat.createPrettyPrint();  
		formater.setEncoding("UTF-8");  
		FileOutputStream out = null;
		XMLWriter writer = null;
		try{
			out = new FileOutputStream(xml);
			writer=new XMLWriter(out,formater);
			writer.setEscapeText(false);
			writer.write(doc);  
			writer.flush();
			writer.close();
		}finally{
			IoUtil.close(out); 
		}
	}
}
