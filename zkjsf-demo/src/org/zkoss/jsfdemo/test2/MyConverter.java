package org.zkoss.jsfdemo.test2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class MyConverter implements Converter {
	
	
	
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if(value==null || "".equals(value.trim())) return null;
		try{
			return value.toString();
		}catch(Exception x){
			x.printStackTrace();
			throw new ConverterException("Error Format");
		}
	}


	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if(value!=null) return value.toString();
		return "";
	}

}
