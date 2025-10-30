/* FormattedTemporalConverter.java

	Purpose:
		
	Description:
		
	History:
		Mon Aug 19 11:29:41 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.bind.converter.sys;

import java.io.Serializable;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;

import org.zkoss.bind.BindContext;
import org.zkoss.bind.Converter;
import org.zkoss.bind.sys.Binding;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * Converter to convert String to Temporal object.
 *
 * @author rudyhuang
 * @since 9.0.0
 */
public class FormattedTemporalConverter<T extends TemporalAccessor> implements Converter<String, T, Component>, Serializable {
	private static final long serialVersionUID = 5761640535398518021L;
	private final TemporalQuery<T> parseTo;

	/**
	 * Construct with a parseTo method.
	 *
	 * @param parseTo Temporal method parsed to
	 */
	public FormattedTemporalConverter(TemporalQuery<T> parseTo) {
		this.parseTo = parseTo;
	}

	/**
	 * Convert Temporal to String.
	 *
	 * @param beanProp temporal to be converted
	 * @param component associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted temporal as String
	 */
	@Override
	public String coerceToUi(T beanProp, Component component, BindContext ctx) {
		//user sets format in annotation of binding or args when calling binder.addPropertyBinding()
		final String format = (String) ctx.getConverterArg("format");
		if (format == null)
			throw new NullPointerException("format attribute not found");

		return beanProp == null ? null : getLocalizedDateTimeFormatter(format).format(beanProp);
	}

	/**
	 * Convert String to Temporal.
	 *
	 * @param compAttr temporal in string form
	 * @param component associated component
	 * @param ctx bind context for associate {@link Binding} and extra parameter (e.g. format)
	 * @return the converted Temporal
	 */
	@Override
	public T coerceToBean(String compAttr, Component component, BindContext ctx) {
		final String format = (String) ctx.getConverterArg("format");
		if (format == null)
			throw new NullPointerException("format attribute not found");
		try {
			return compAttr == null ? null : getLocalizedDateTimeFormatter(format).parse(compAttr, parseTo);
		} catch (DateTimeParseException e) {
			throw UiException.Aide.wrap(e);
		}
	}

	private static DateTimeFormatter getLocalizedDateTimeFormatter(String formatPtn) {
		return DateTimeFormatter
				.ofPattern(formatPtn, Locales.getCurrent())
				.withZone(TimeZones.getCurrent().toZoneId());
	}
}
