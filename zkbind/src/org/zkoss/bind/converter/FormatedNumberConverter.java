/* FormatedNumberConverter.java

	Purpose:
		
	Description:
		
	History:
		Jun 22, 2011 11:22:20 AM, Created by henrichen

Copyright (C) 2011 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.Binding;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Converter to convert String to Number.
 * @author henrichen
 * @since 6.0.0
 */
public class FormatedNumberConverter implements Converter,Serializable {
	private static final long serialVersionUID = 1463169907348730644L;
	/**
	 * Convert Number to String.
	 * @param val number to be converted
	 * @param comp associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted String
	 */
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		//user sets format in annotation of binding or args when calling binder.addPropertyBinding()  
		final String formatPtn = (String) ctx.getConverterArg("format");
		if(formatPtn==null) throw new NullPointerException("format attribute not found");
		return val == null ? null : 
			getLocalizedDecimalFormat(formatPtn).format((Number) val);
	}
	
	/**
	 * Convert String to Number.
	 * @param val number in string form
	 * @param comp associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted Number
	 */
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		final String format = (String) ctx.getConverterArg("format");
		if(format==null) throw new NullPointerException("format attribute not found");
		try {
			return val == null ? null : 
				getLocalizedDecimalFormat(format).parse((String) val);
		} catch (ParseException e) {
			throw UiException.Aide.wrap(e);
		}
	}
	
	private static DecimalFormat getLocalizedDecimalFormat(String pattern){
		final DecimalFormat df = 
			(DecimalFormat)NumberFormat.getInstance(Locales.getCurrent());
		df.applyLocalizedPattern(pattern);
		return df;
	}
}
