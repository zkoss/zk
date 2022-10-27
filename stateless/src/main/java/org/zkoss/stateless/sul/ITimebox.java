/* ITimebox.java

	Purpose:

	Description:

	History:
		Wed Nov 10 17:03:14 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.text.DateFormats;
import org.zkoss.util.Locales;
import org.zkoss.util.TimeZones;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Timebox;

/**
 * Immutable {@link Timebox} component.
 *
 * <p>An input box for holding a time (a java.util.Date Object), but only
 * Hour {@literal &} Minute are used.</p>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be {@code no empty}.</p>
 *
 * <h2>Format</h2>
 * <p>Use a to signify it is am or pm. The input string follows the formatting of
 * the {@link SimpleDateFormat}.</p>
 * <p>Below is an example of using a within the format.</p>
 * <pre>
 * <code>
 * ITimebox.ofCols(20).withFormat("a hh:mm:ss");
 * </code>
 * </pre>
 * <p>24 hours mode:</p>
 * <pre>
 * <code>
 * ITimebox.ofCols(8).withFormat("HH:mm:ss");
 * </code>
 * </pre>
 * <p>In addition to specifying the format explicitly, you could specify the styling.
 * There are four different types of styling: {@code short}, {@code medium},
 * {@code long} and {@code full} (representing the styling of {@link java.text.DateFormat}).
 * <br><br>
 * For example, you could specify the styling rather than the real format as follows.</p>
 * <pre>
 * <code>
 * ITimebox.ofFormat("short");
 * ITimebox.ofFormat("long");
 * </code>
 * </pre>
 *
 * <h2>Locale</h2>
 * <p>By default, the real format depends on the current locale (i.e., {@link Locales#getCurrent()}.
 * However, you could specify the locale for an individual instance such as:</p>
 * <pre>
 * <code>
 * ITimebox.ofFormat("medium").withLocale("de_DE");
 * ITimebox.ofFormat("long").withLocale("fr");
 * </code>
 * </pre>
 *
 * @author katherine
 * @see Timebox
 */
@StatelessStyle
public interface ITimebox extends IDateTimeFormatInputElement<ITimebox>, IAnyGroup<ITimebox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ITimebox DEFAULT = new ITimebox.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Timebox> getZKType() {
		return Timebox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.db.Timebox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.db.Timebox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default ITimebox updateValue() {
		if (getValue() == null) {
			if (getValueInZonedDateTime() != null) {
				return new ITimebox.Builder().from(this).setValue(toDate(getValueInZonedDateTime())).build();
			} else if (getValueInLocalDateTime() != null) {
				return new ITimebox.Builder().from(this).setValue(toDate(getValueInLocalDateTime())).build();
			} else if (getValueInLocalTime() != null) {
				return new ITimebox.Builder().from(this).setValue(toDate(getValueInLocalTime())).build();
			} else if (getValueInLocalDate() != null) {
				throw new UnsupportedOperationException("need time");
			}
		}
		return this;
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	@Nullable
	default String getUnformater() {
		if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(Executions.getCurrent().getDesktop(),
				"org.zkoss.zul.Timebox.unformater.isSent")) {
			return Library.getProperty("org.zkoss.zul.Timebox.unformater");
		}
		return null;
	}

	/** Returns whether the button (on the right of the timebox) is visible.
	 * <p>Default: {@code true}.
	 */
	default boolean isButtonVisible() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code buttonVisible}.
	 *
	 * <p>Sets whether the button (on the right of the timebox) is visible.
	 *
	 * @param buttonVisible {@code false} to disable the button visibility.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ITimebox withButtonVisible(boolean buttonVisible);

	/** Returns the cols which determines the visible width, in characters.
	 * <p>Default: {@code 5} (non-positive means the same as browser's default).
	 */
	default int getCols() {
		return 5;
	}

	/**
	 * Returns the format of the timebox.
	 * <p>Default: {@code ""} (empty string).
	 */
	default String getFormat() {
		return "";
	}

	/** Returns the real format, i.e., the combination of the format patterns,
	 * such as hh:mm.
	 * <p>As described in {@link #withFormat}, a developer could specify
	 * an abstract name, such as short, or an empty string as the format,
	 * and this method will convert it to a real time format. (Internal use)
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default String getRealFormat() {
		Locale _locale = getLocale();
		final String format = getFormat();
		if (format == null || format.length() == 0)
			return DateFormats.getTimeFormat(DateFormat.DEFAULT, _locale, "HH:mm");

		int ts = Datebox.toStyle(format);
		return ts != -111 ? DateFormats.getTimeFormat(ts, _locale, "HH:mm") : format;
	}

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the time for the timebox.
	 */
	static ITimebox of(Date date) {
		return new ITimebox.Builder().setValue(date).build();
	}

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the time for the timebox.
	 */
	static ITimebox of(Temporal date) {
		if (date instanceof ZonedDateTime) return new ITimebox.Builder().setValueInZonedDateTime((ZonedDateTime) date).build();
		else if (date instanceof LocalDateTime) return new ITimebox.Builder().setValueInLocalDateTime((LocalDateTime) date).build();
		else if (date instanceof LocalTime) return new ITimebox.Builder().setValueInLocalTime((LocalTime) date).build();
		else throw new WrongValueException("unsupported date: " + date);
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static ITimebox ofCols(int cols) {
		return new Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The timebox constraint
	 */
	static ITimebox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given format.
	 * @param format The timebox format
	 */
	static ITimebox ofFormat(String format) {
		return new ITimebox.Builder().setFormat(format).build();
	}

	/**
	 * Returns the instance with the given locale.
	 * @param locale The timebox locale
	 */
	static ITimebox ofLocale(String locale) {
		if (locale != null && locale.length() > 0)
			return new ITimebox.Builder().setLocale(Locales.getLocale(locale)).build();
		else throw new WrongValueException("locale cannot be null or empty");
	}

	/**
	 * Returns the instance with the given locale.
	 * @param locale The timebox locale
	 */
	static ITimebox ofLocale(Locale locale) {
		return new ITimebox.Builder().setLocale(locale).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ITimebox ofId(String id) {
		return new ITimebox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IDateTimeFormatInputElement.super.renderProperties(renderer);

		Locale _locale = getLocale();
		TimeZone _tzone = getTimeZone();
		if (getRealFormat().indexOf("z") != -1) {
			final DateFormat df = new SimpleDateFormat("z", _locale != null ? _locale : Locales.getCurrent());
			final TimeZone tz = _tzone != null ? _tzone : TimeZones.getCurrent();
			df.setTimeZone(tz);
			String timezone = df.format(new Date());
			renderer.render("timezoneAbbr", timezone);
		}

		boolean _btnVisible = isButtonVisible();
		if (!_btnVisible)
			renderer.render("buttonVisible", _btnVisible);

		String unformater = getUnformater();
		if (!Strings.isBlank(unformater))
			renderer.render("unformater", unformater); // TODO: compress

		if (_locale != null) {
			final String localeName = _locale.toString();
			if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(Executions.getCurrent().getDesktop(),
					getClass().getName() + localeName)) {
				final Map<String, String[]> map = new HashMap<String, String[]>(2);
				final Calendar cal = Calendar.getInstance(_locale);

				SimpleDateFormat df = new SimpleDateFormat("a", _locale);
				cal.set(Calendar.HOUR_OF_DAY, 3);
				final String[] ampm = new String[2];
				ampm[0] = df.format(cal.getTime());
				cal.set(Calendar.HOUR_OF_DAY, 15);
				ampm[1] = df.format(cal.getTime());
				map.put("APM", ampm);
				renderer.render("localizedSymbols", new Object[] { localeName, map });
			} else renderer.render("localizedSymbols", new Object[] { localeName, null });
		}
	}

	/**
	 * Builds instances of type {@link ITimebox ITimebox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableITimebox.Builder {
	}

	/**
	 * Builds an updater of type {@link ITimebox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends ITimeboxUpdater {}
}