/* DateConverter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 13, 2007 2:36:36 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package foo;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 * @author Dennis.Chen
 *
 */
public class DateConverter implements Converter {

	SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
	 */
	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		if(value==null || "".equals(value.trim())) return null;
		try{
			return format.parse(value);
		}catch(Exception x){
			throw new ConverterException("Error Format");
		}
	}

	/* (non-Javadoc)
	 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
	 */
	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		if(value!=null) return format.format((Date)value);
		return "";
	}

}
