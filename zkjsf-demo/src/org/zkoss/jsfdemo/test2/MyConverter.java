package org.zkoss.jsfdemo.test2;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class MyConverter implements Converter {
	
	SimpleDateFormat format = new SimpleDateFormat("yyyy/dd/MM");
	
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String value)
			throws ConverterException {
		
			return value;

	}

	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2)
			throws ConverterException {
		if(arg2 !=null)return arg2.toString();
		return null;
	}

}
