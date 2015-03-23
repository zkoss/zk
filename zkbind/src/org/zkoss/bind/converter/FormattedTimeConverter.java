/* FormatedTimeConverter.java

	Purpose:
		
	Description:
		
	History:
		Mar 20, 2015 12:30:00 PM, Created by jameschu

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.Binding;
import org.zkoss.bind.sys.PropertyBinding;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.xel.ExpressionX;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Converter to convert String to Time (Date Object).
 * @author JamesChu
 * @since 8.0.0
 */
public class FormattedTimeConverter implements Converter,Serializable {
	
	private static final long serialVersionUID = 3505731684878632094L;

	/**
	 * Convert Date to String.
	 * @param val date to be converted
	 * @param comp associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted String
	 */
	public Object coerceToUi(Object val, Component comp, BindContext ctx) {
		//user sets format in annotation of binding or args when calling binder.addPropertyBinding()  
		final String format = (String) ctx.getConverterArg("format");
		if(format==null) throw new NullPointerException("format attribute not found");
		final Date date = (Date) val;
		return date == null ? null : getLocalizedSimpleDateFormat(format).format(date);
	}
	
	/**
	 * Convert String to Date.
	 * @param val date in string form
	 * @param comp associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted Date
	 */
	public Object coerceToBean(Object val, Component comp, BindContext ctx) {
		final String format = (String) ctx.getConverterArg("format");
		if (format == null) throw new NullPointerException("format attribute not found");
		final String dateStr = (String) val;
		
		try {
			Date newDate = dateStr == null ? null : getLocalizedSimpleDateFormat(format).parse(dateStr);
			if (newDate != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTimeZone(TimeZones.getCurrent());
				cal.setTime(newDate);
				
				Binding b = ctx.getBinding();
				if (b instanceof PropertyBinding) {
					int h = cal.get(Calendar.HOUR_OF_DAY), m = cal.get(Calendar.MINUTE), s = cal.get(Calendar.SECOND)
						, ms = cal.get(Calendar.MILLISECOND);
					
					Object value = ctx.getBinder().getEvaluatorX().getValue(ctx, comp, ((PropertyBinding)b).getProperty());
					Date oldDate = new Date();
					if (value instanceof Date)
						oldDate = (Date) value;
					else if (value instanceof Long) {
						cal.setTimeInMillis((Long) value);
						oldDate = cal.getTime();
					}
					cal.setTime(oldDate);
					cal.set(Calendar.HOUR_OF_DAY, h);
					cal.set(Calendar.MINUTE, m);
					cal.set(Calendar.SECOND, s);
					cal.set(Calendar.MILLISECOND, ms);
					newDate = cal.getTime();
				}
			}
			return newDate;
		} catch (ParseException e) {
			throw UiException.Aide.wrap(e);
		}
	}
	
	private static SimpleDateFormat getLocalizedSimpleDateFormat(String formatPtn){
		SimpleDateFormat sdf = new SimpleDateFormat(formatPtn, Locales.getCurrent());
		sdf.setTimeZone(TimeZones.getCurrent());
		return sdf;
	}
}
