/* INumberInputElement.java

	Purpose:

	Description:

	History:
		Tue Oct 26 14:19:24 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.json.JSONValue;
import org.zkoss.lang.Library;
import org.zkoss.util.Locales;
import org.zkoss.zk.ui.sys.ContentRenderer;

/**
 * Immutable {@link org.zkoss.zul.impl.NumberInputElement} interface.
 * <p>A skeletal implementation for number-type input component.</p>
 *
 * <h2>Per-component Locale</h2>
 * <p>You can add a locale per component for all of the INumberInputElement.
 * <br><br>
 * For example,
 * </p>
 * @author katherine
 * @see org.zkoss.zul.impl.NumberInputElement
 */
public interface INumberInputElement<I extends INumberInputElement, ValueType> extends IFormatInputElement<I, ValueType> {

	/** Returns the locale associated with this number input component
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred</p>
	 */
	@Nullable
	Locale getLocale();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code locale}.
	 *
	 * <p>Sets the locale used to identify the symbols of this number input component.
	 * <p> If the format of {@link #getFormat()} is null, the format is assumed from
	 * {@link #getDefaultFormat()}.
	 *
	 * @param locale The preferred locale
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withLocale(@Nullable Locale locale);

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code locale}.
	 *
	 * <p>Sets the locale used to identify the symbols of this number input component.
	 * <p> If the format of {@link #getFormat()} is null, the format is assumed from
	 * {@link #getDefaultFormat()}.
	 *
	 * @param locale The preferred locale
	 * <p>Default: {@code null}, if {@link Locales#getCurrent} is preferred.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default I withLocale(String locale) {
		return withLocale(locale != null && locale.length() > 0 ? Locales.getLocale(locale) : null);
	}

	/**
	 * Return a default format for the number input element when the locale is specified. (Internal use)
	 * <p> Default: <code>##,##0.##</code>, you can overwrite this by specifying
	 * the following setting in zk.xml
	 * <pre><code>
	 *	<library-property>
	 *		<name>org.zkoss.zul.numberFormat</name>
	 *		<value>##,##0.##</value>
	 *	</library-property>
	 * </code></pre>
	 * @see #withLocale(Locale)
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default String getDefaultFormat() {
		return Library.getProperty("org.zkoss.zul.numberFormat", "##,##0.##");
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default I checkLocale() {
		final Locale locale = getLocale();
		if (locale != null && getFormat() == null) {
			return withFormat(getDefaultFormat());
		}
		return (I) this;
	}

	/** Returns the cols which determines the visible width, in characters.
	 * <p>Default: {@code 11} (non-positive means the same as browser's default).
	 */
	default int getCols() { return 11; }

	/** Returns the rounding mode.
	 * <p>Default: {@link BigDecimal#ROUND_HALF_EVEN}.
	 */
	default int getRoundingMode() {
		return BigDecimal.ROUND_HALF_EVEN;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code roundingMode}.
	 *
	 * <p>Sets the rounding mode.
	 *
	 * @param roundingMode The rounding mode. Allowed value:
	 * {@link BigDecimal#ROUND_CEILING}, {@link BigDecimal#ROUND_DOWN},
	 * {@link BigDecimal#ROUND_FLOOR}, {@link BigDecimal#ROUND_HALF_DOWN},
	 * {@link BigDecimal#ROUND_HALF_UP}, {@link BigDecimal#ROUND_HALF_EVEN},
	 * {@link BigDecimal#ROUND_UNNECESSARY} and {@link BigDecimal#ROUND_UP}
	 *
	 * <p>Default: {@link BigDecimal#ROUND_HALF_EVEN}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	I withRoundingMode(int roundingMode);

	/**
	 * Returns if the "locale:" in {@link #getFormat()} is presented. (Internal use)
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default boolean isLocaleFormat() {
		String format = getFormat();
		return format != null && format.startsWith("locale:");
	}

	/** Returns the default locale, either {@link #getLocale} or
	 * {@link Locales#getCurrent} (never null). (Internal use)
	 * It is useful when you want to get a locale for this input.
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Locale getDefaultLocale() {
		Locale _locale = getLocale();
		if (isLocaleFormat())
			return Locales.getLocale(getFormat().substring(7), '-');
		return _locale != null ? _locale : Locales.getCurrent();
	}

	/** Returns the real symbols according to the current locale. (Internal use)
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default String getRealSymbols() {
		if (getLocale() != null || isLocaleFormat()) {
			Locale usedLocale = getDefaultLocale();
			String localeName = usedLocale.toString();
				final DecimalFormatSymbols symbols = new DecimalFormatSymbols(usedLocale);
				Map<String, String> map = new HashMap<String, String>();
				map.put("GROUPING", String.valueOf(symbols.getGroupingSeparator()));
				map.put("DECIMAL", String.valueOf(symbols.getDecimalSeparator()));
				map.put("PERCENT", String.valueOf(symbols.getPercent()));
				map.put("PER_MILL", String.valueOf(symbols.getPerMill()));
				map.put("MINUS", String.valueOf(symbols.getMinusSign()));
				return JSONValue.toJSONString(new Object[] { localeName, map });
		}
		return null;
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IFormatInputElement.super.renderProperties(renderer);

		int _rounding = getRoundingMode();
		if (_rounding != BigDecimal.ROUND_HALF_EVEN)
			renderer.render("_rounding", _rounding);
		if (getLocale() != null)
			renderer.render("localizedSymbols", getRealSymbols());
	}
}