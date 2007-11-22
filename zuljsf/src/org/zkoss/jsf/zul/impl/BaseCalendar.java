/* BaseCalendar.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Aug 8, 2007 5:48:27 PM     2007, Created by Dennis.Chen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.jsf.zul.impl;

import java.text.SimpleDateFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.zkoss.zk.ui.Component;


/**
 * The Base implementation of Calendar. 
 * This component should be declared nested under {@link org.zkoss.jsf.zul.Page}.
 * @author Dennis.Chen
 *
 */
abstract public class BaseCalendar extends BranchInput{


	/**
	 * Override method, add a {@link DateFormatConverter} as the default converter if no converter is assigned.
	 */
	protected void afterZULComponentComposed(Component zulcomp) {
		
		//add default converter before super process attribute.
		Converter converter = this.getConverter();
		if(converter==null){
			converter = new DateFormatConverter();
			this.setConverter(converter);
		}
		
		super.afterZULComponentComposed(zulcomp);
	}
	
	
	/**
	 * this implementation return java.lang.Date which is parsed from value;
	 * @see ValueHolderSupport
	 */
	public Object transferValueForAttribute(String value){
		if(value==null) return null;
		return getConverter().getAsObject(getFacesContext(),this,value);
	}
	
	
	/**
	 * Converter String to Date, or Date to String by a {@link java.text.SimpleDateFormat}.
	 * @author Dennis.Chen
	 *
	 */
	static public class DateFormatConverter extends FormateHolder implements Converter{

		private SimpleDateFormat _formater;

		public DateFormatConverter() {
			formatChanged();
		}

		public DateFormatConverter(String format) {
			super(format);
			formatChanged();
		}

		protected void formatChanged() {
			if(_formater==null){
				_formater = new SimpleDateFormat();
			}
			if(_format==null){
				_formater.applyPattern("yyyy/MM/dd");
			}else{
				_formater.applyPattern(_format);
			}
		}
		/* (non-Javadoc)
		 * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
		 */
		public Object getAsObject(FacesContext context, UIComponent component, String value) {
			try {
				if(value==null|| "".equals(value.trim()))
					return null;
				return _formater.parse(value);
			} catch (Exception e) {
				throw new ConverterException(e.getMessage());
			}
		}
		/* (non-Javadoc)
		 * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
		 */
		public String getAsString(FacesContext context, UIComponent component, Object value) {
			if(value==null) return null;
			try{
				return _formater.format(value);
			}catch (Exception e) {
				throw new ConverterException(e.getMessage(),e);
			}
		}
	}
	

}
