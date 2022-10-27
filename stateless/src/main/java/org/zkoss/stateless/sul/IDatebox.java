/* IDatebox.java

	Purpose:

	Description:

	History:
		Tue Nov 09 09:50:31 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.sul;

import static org.zkoss.zul.Datebox.loadSymbols;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.lang.Library;
import org.zkoss.lang.Strings;
import org.zkoss.mesg.Messages;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.text.DateFormats;
import org.zkoss.util.Locales;
import org.zkoss.stateless.immutable.StatelessStyle;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.SmartUpdater;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Timebox;
import org.zkoss.zul.mesg.MZul;

/**
 * Immutable {@link Datebox} component
 * <p>An edit box for holding a date.
 *
 * <p> The default format ({@link #getFormat()}) depends on {@link DateFormats#getDateFormat(int, Locale, String)}
 * and the current user's locale (unless {@link #withLocale(Locale)} is assigned.
 * Please refer to {@link #withFormat(String)} for more details.
 *
 * <h3>Support {@literal @}Action</h3>
 * <table>
 *    <thead>
 *       <tr>
 *          <th>Name</th>
 *          <th>Action Type</th>
 *       </tr>
 *    </thead>
 *    <tbody>
 *       <tr>
 *          <td>onTimeZoneChange</td>
 *          <td>Denotes the time zone of the component is changed by user.</td>
 *       </tr>
 *    </tbody>
 * </table>
 *
 * <h2>Mouseless Entry</h2>
 * <ul>
 *     <li>Alt+DOWN to pop up the {@code calendar}.</li>
 *     <li>LEFT, RIGHT, UP and DOWN to change the selected day from the {@code calendar}.</li>
 *     <li>ENTER to activate the selection by copying the selected day to the {@code datebox} control.</li>
 *     <li>Alt+UP or ESC to give up the selection and close the {@code calendar}.</li>
 * </ul>
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no empty}, {@code no future}, {@code no past}, and {@code no today}.</p>
 * <p>It also supports an interval of dates. For example,</p>
 * <pre>
 * <code>
 * IDatebox.ofConstraint("between 20071225 and 20071203");
 * IDatebox.ofConstraint("before 20071225");
 * IDatebox.ofConstraint("after 20071225");
 * </code>
 * </pre>
 * <p><b>Notices:</b>
 * The format of the date in the constraint must be {@code yyyMMdd}. It is independent
 * of the {@code locale}. The date specified in the above constraints ({@code before/after/between})
 * is included. For example, {@code "before 20071225"} includes December 25, 2007
 * and any day before it, and {@code "after 20110305"} includes March 5, 2011 and
 * any day after it.
 *
 * <h2>Displayed Time Zones</h2>
 * <p>The image below shows the new Datebox functionality which allows the user
 * to change the time zone to other predefined time zones.</p>
 * <img src="doc-files/IDatebox_timezone.png"/>
 * <pre>
 * <code>
 * IDatebox.ofFormat("M/d/yy KK:mm:ss a").withWidth("150px")
 *     .withDisplayedTimeZones("GMT+12", "GMT+8").withTimeZone("GMT+8")
 *     .withTimeZonesReadonly(false)
 * </code>
 * </pre>
 *
 * <h2>Format</h2>
 * <p>You are able to format the field by providing specifying the attribute
 * with a formatting string. The default value is {@code null}.
 * When the formatting of the datebox is null, it means the date will be outputted
 * using the format {@code yyyy/MM/dd}.</p>
 * <pre><code>IDatebox.ofFormat("MM/dd/yyyy");</code></pre>
 *
 * <h2>Length Option</h2>
 * <p>In addition to specifying the format explicitly, you could specify the length option.
 * It supports four different length options mentioned at {@link java.text.DateFormat},
 * such as {@code short}, {@code medium}, {@code long}, and {@code full}.</p>
 * <p>In addition, you could specify the format for both date and time by using the syntax:</p>
 * <code>withFormat("option_for_date+option_for_time")</code>
 * For example,
 * <pre><code>IDatebox.ofFormat("medium+full")</code></pre>
 *
 * <h2>Multiple-Eras Calendar</h2>
 * <p>Datebox can display some multiple-eras calendar systems including:</p>
 * <ul>
 *     <li>ROC (Taiwan): <code>withLocale("zh-TW-u-ca-roc");</code></li>
 *     <li>Japan: <code>withLocale("ja-JP-u-ca-japanese");</code></li>
 *     <li>Buddhist: <code>withLocale("th-TH-u-ca-buddhist");</code></li>
 * </ul>
 * <img src="doc-files/IDatebox_multiple_eras.png"/>
 *
 * <h2>Position</h2>
 * <p>By default, the popup position is set to {@code after_start}, for other
 * possible popup positions please refer to {@link IPopup.Position}</p>
 *
 * <h2>The First Day of the Week</h2>
 * <p>The first day of the week is decided by the locale (actually the return value of
 * the {@code getFirstDayOfWeek} method in the {@link java.util.Calendar}).
 * You can also control the first day of the week by the use of the session attribute
 * and the library property. Please refer to <a href="https://www.zkoss.org/wiki/ZK_Developer%27s_Reference/Internationalization/The_First_Day_of_the_Week">The First Day of the Week</a> for details.</p>
 *
 * <h2>2DigitYearStart</h2>
 * <p>You can control the 2DigitYearStart by the use of the library property,
 * for example,</p>
 * <pre>
 * <code>{@code <}library-property{@code >}
 *     {@code <}name{@code >}org.zkoss.web.preferred.2DigitYearStart{@code <}/name{@code >}
 *     {@code <}value{@code >}1950{@code <}/value{@code >}
 * {@code <}/library-property{@code >}
 * </code>
 * </pre>
 * @author katherine
 * @see Datebox
 */
@StatelessStyle
public interface IDatebox extends IDateTimeFormatInputElement<IDatebox>, IAnyGroup<IDatebox> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	IDatebox DEFAULT = new IDatebox.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Datebox> getZKType() {
		return Datebox.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.db.Datebox"}</p>
	 */
	default String getWidgetClass() {
		return "zul.db.Datebox";
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default void checkSelectLevel() {
		String selectLevel = getSelectLevel();
		if (!"day".equals(selectLevel) && !"month".equals(selectLevel) && !"year".equals(selectLevel))
			throw new WrongValueException("Only allowed day, month, and year, not " + selectLevel);
	}

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Check
	default IDatebox updateValue() {
		if (getValue() == null) {
			if (getValueInZonedDateTime() != null) {
				return new IDatebox.Builder().from(this).setValue(toDate(getValueInZonedDateTime())).build();
			} else if (getValueInLocalDateTime() != null) {
				return new IDatebox.Builder().from(this).setValue(toDate(getValueInLocalDateTime())).build();
			} else if (getValueInLocalDate() != null) {
				return new IDatebox.Builder().from(this).setValue(toDate(getValueInLocalDate())).build();
			} else if (getValueInLocalTime() != null) {
				return new IDatebox.Builder().from(this).setValue(toDate(getValueInLocalTime())).build();
			}
		}
		return this;
	}

	/**
	 * Returns a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>Default: {@code null}
	 */
	@Nullable
	default List<TimeZone> getDisplayedTimeZones() {
		return null;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code displayedTimeZones}.
	 *
	 * <p>Sets a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>If the {@link #getTimeZone()} is null,
	 * the first time zone in the list is assumed.
	 *
	 * @param displayedTimeZones A list of the time zones to display.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withDisplayedTimeZones(@Nullable Iterable<? extends TimeZone> displayedTimeZones);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code displayedTimeZones}.
	 *
	 * <p>Sets a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>If the {@link #getTimeZone()} is null,
	 * the first time zone in the list is assumed.
	 *
	 * @param displayedTimeZones A list of the time zones to display.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IDatebox withDisplayedTimeZones(TimeZone... displayedTimeZones) {
		return withDisplayedTimeZones(Arrays.asList(displayedTimeZones));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code displayedTimeZones}.
	 *
	 * <p>Sets a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>If the {@link #getTimeZone()} is null,
	 * the first time zone in the list is assumed.
	 *
	 * @param displayedTimeZones A list of the time zones to display.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IDatebox withDisplayedTimeZones(String displayedTimeZones) {
		if (Strings.isEmpty(displayedTimeZones)) {
			return withDisplayedTimeZones((Iterable<? extends TimeZone>) null);
		}
		return withDisplayedTimeZones(displayedTimeZones.split(","));
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code displayedTimeZones}.
	 *
	 * <p>Sets a list of the time zones that will be displayed at the
	 * client and allow user to select.
	 * <p>If the {@link #getTimeZone()} is null,
	 * the first time zone in the list is assumed.
	 *
	 * @param displayedTimeZones A list of the time zones to display.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default IDatebox withDisplayedTimeZones(String... displayedTimeZones) {
		return withDisplayedTimeZones(Stream.of(displayedTimeZones).map(tz -> TimeZone.getTimeZone(tz.trim()))
				.filter(Objects::nonNull).collect(Collectors.toList()));
	}

	/**
	 * Returns the default datetime if the value is empty.
	 * <p>Default: {@code null} (means current datetime)
	 */
	@Nullable
	LocalDateTime getDefaultDateTime();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code defaultDateTime}.
	 *
	 * <p>Sets the default datetime if the value is empty.
	 *
	 * @param defaultDateTime Default datetime. null means current datetime.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withDefaultDateTime(@Nullable LocalDateTime defaultDateTime);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	@Nullable
	default String getUnformater() {
		if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(Executions.getCurrent().getDesktop(),
				"org.zkoss.zul.Datebox.unformater.isSent")) {
			return Library.getProperty("org.zkoss.zul.Datebox.unformater");
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
	IDatebox withButtonVisible(boolean buttonVisible);

	/**
	 * Returns whether date/time parsing is to be lenient or not.
	 *
	 * <p> With lenient parsing, the parser may use heuristics to interpret inputs
	 * that do not precisely match this object's format. With strict parsing,
	 * inputs must match this object's format.
	 *
	 * <p>Default: {@code true}</p>
	 */
	default boolean isLenient() {
		return true;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code lenient}.
	 *
	 * <p>Sets whether date/time parsing is to be lenient or not.
	 *
	 * @param lenient {@code false} to disable date/time parsing is to be lenient.
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withLenient(boolean lenient);

	/**
	 * Returns whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 * <p>Default: {@code false}</p>
	 */
	default boolean isTimeZonesReadonly() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code timeZonesReadonly}.
	 *
	 * <p>Sets whether the list of the time zones to display is readonly.
	 * If readonly, the user cannot change the time zone at the client.
	 *
	 * @param timeZonesReadonly {@code true} to make time zones options display as
	 * readonly.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withTimeZonesReadonly(boolean timeZonesReadonly);

	/**
	 * Returns whether enable to show the week number in the current calendar or not.
	 * <p>Default: {@code false}
	 */
	default boolean isWeekOfYear() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code weekOfYear}.
	 *
	 * <p>Sets whether enable to show the week number in the current calendar or
	 * not. (ZK EE only)
	 *
	 * @param weekOfYear {@code true} to show the week number in the current calendar.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withWeekOfYear(boolean weekOfYear);

	/**
	 * @return the datebox popup position
	 */
	default String getPosition() {
		return "after_start";
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code position}.
	 *
	 * <p>Position the popup datebox to the specified location.
	 *
	 * @param position Where to position. Default: <code>after_start</code>
	 * Allowed values:</br>
	 * <ul>
	 * <li><b>before_start</b><br/> the component appears above the anchor, aligned to the left.</li>
	 * <li><b>before_center</b><br/> the component appears above the anchor, aligned to the center.</li>
	 *  <li><b>before_end</b><br/> the component appears above the anchor, aligned to the right.</li>
	 *  <li><b>after_start</b><br/> the component appears below the anchor, aligned to the left.</li>
	 *  <li><b>after_center</b><br/> the component appears below the anchor, aligned to the center.</li>
	 *  <li><b>after_end</b><br/> the component appears below the anchor, aligned to the right.</li>
	 *  <li><b>start_before</b><br/> the component appears to the left of the anchor, aligned to the top.</li>
	 *  <li><b>start_center</b><br/> the component appears to the left of the anchor, aligned to the middle.</li>
	 *  <li><b>start_after</b><br/> the component appears to the left of the anchor, aligned to the bottom.</li>
	 *  <li><b>end_before</b><br/> the component appears to the right of the anchor, aligned to the top.</li>
	 *  <li><b>end_center</b><br/> the component appears to the right of the anchor, aligned to the middle.</li>
	 *  <li><b>end_after</b><br/> the component appears to the right of the anchor, aligned to the bottom.</li>
	 *  <li><b>overlap/top_left</b><br/> the component overlaps the anchor, with anchor and element aligned at top-left.</li>
	 *  <li><b>top_center</b><br/> the component overlaps the anchor, with anchor and element aligned at top-center.</li>
	 *  <li><b>overlap_end/top_right</b><br/> the component overlaps the anchor, with anchor and element aligned at top-right.</li>
	 *  <li><b>middle_left</b><br/> the component overlaps the anchor, with anchor and element aligned at middle-left.</li>
	 *  <li><b>middle_center</b><br/> the component overlaps the anchor, with anchor and element aligned at middle-center.</li>
	 *  <li><b>middle_right</b><br/> the component overlaps the anchor, with anchor and element aligned at middle-right.</li>
	 *  <li><b>overlap_before/bottom_left</b><br/> the component overlaps the anchor, with anchor and element aligned at bottom-left.</li>
	 *  <li><b>bottom_center</b><br/> the component overlaps the anchor, with anchor and element aligned at bottom-center.</li>
	 *  <li><b>overlap_after/bottom_right</b><br/> the component overlaps the anchor, with anchor and element aligned at bottom-right.</li>
	 *  <li><b>at_pointer</b><br/> the component appears with the upper-left aligned with the mouse cursor.</li>
	 *  <li><b>after_pointer</b><br/> the component appears with the top aligned with
	 *  	the bottom of the mouse cursor, with the left side of the component at the horizontal position of the mouse cursor.</li>
	 * </ul>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withPosition(String position);

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code position}.
	 *
	 * <p>Position the popup datebox to the specified location.
	 *
	 * @param position Where to position. Default: {@link IPopup.Position#AFTER_START}
	 * @return A modified copy of the {@code this} object
	 */
	default IDatebox withPosition(IPopup.Position position) {
		Objects.requireNonNull(position);
		return withPosition(position.value);
	}

	/**
	 * Returns whether date/time should be strict or not.
	 * <p>Default: {@code false}.
	 */
	default boolean isStrictDate() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code strictDate}.
	 *
	 * <p>Sets whether date/time should be strict or not.
	 * <p>If true, any invalid input like "Jan 0" or "Nov 31" would be refused.
	 * <p>If false, it won't be checked and let lenient parsing decide.
	 *
	 * @param strictDate {@code true} to make date/time be strict.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withStrictDate(boolean strictDate);

	/**
	 * Returns whether enable to show the link that jump to today in day view
	 * <p>Default: {@code false}
	 */
	default boolean getShowTodayLink() { return false; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code showTodayLink}.
	 *
	 * <p>Sets whether enable to show the link that jump to today in day view
	 *
	 * @param showTodayLink {@code true} to show a link to jump to today.
	 * <p>Default: {@code false}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withShowTodayLink(boolean showTodayLink);

	/**
	 * Returns the label of the link that jump to today in day view
	 * <p>Default: {@code "Today"}
	 */
	default String getTodayLinkLabel() {
		return Messages.get(MZul.CALENDAR_TODAY);
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code todayLinkLabel}.
	 *
	 * <p>Sets the label of the link that jump to today in day view.
	 *
	 * @param todayLinkLabel The label of the link to jump to today.
	 * <p>Default: {@code "Today"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withTodayLinkLabel(String todayLinkLabel);

	/**
	 * Returns the level that a user can select.
	 * <p> Default: {@code "day"}
	 */
	default String getSelectLevel() { return "day"; }

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code selectLevel}.
	 *
	 * <p>Sets the level that a user can select.
	 *
	 * @param selectLevel The level that a user can select.
	 * <p>Default: {@code "day"}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withSelectLevel(String selectLevel);

	/**
	 * Returns whether to auto close the datebox popup after changing the timezone.
	 * <p> Default: {@code true}
	 */
	default boolean getClosePopupOnTimezoneChange() { return true; };

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code closePopupOnTimezoneChange}.
	 *
	 * <p>Sets whether to auto close the datebox popup after changing the timezone.
	 *
	 * @param closePopupOnTimezoneChange Whether to auto close the datebox popup after changing the timezone
	 * <p>Default: {@code true}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withClosePopupOnTimezoneChange(boolean closePopupOnTimezoneChange);

	/** Returns the cols which determines the visible width, in characters.
	 * <p>Default: {@code 11} (non-positive means the same as browser's default).
	 */
	default int getCols() {
		return 11;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code format}.
	 *
	 * <p>Sets the date format.
	 * <p>You could specify one of the following reserved words,
	 * and {@link DateFormats#getDateFormat(int, Locale, String)} or {@link DateFormats#getDateTimeFormat}
	 * will be used to retrieve the real format.
	 * <table border=0 cellspacing=3 cellpadding=0>
	 * <tr>
	 * <td>short</td>
	 * <td>{@link DateFormats#getDateFormat(int, Locale, String)} with {@link DateFormat#SHORT}</td>
	 * </tr>
	 * <tr>
	 * <td>medium</td>
	 * <td>{@link DateFormats#getDateFormat(int, Locale, String)} with {@link DateFormat#MEDIUM}</td>
	 * </tr>
	 * <tr>
	 * <td>long</td>
	 * <td>{@link DateFormats#getDateFormat(int, Locale, String)} with {@link DateFormat#LONG}</td>
	 * </tr>
	 * <tr>
	 * <td>full</td>
	 * <td>{@link DateFormats#getDateFormat(int, Locale, String)} with {@link DateFormat#FULL}</td>
	 * </tr>
	 * </table>
	 *
	 * <p>To specify a date/time format, you could specify two reserved words, separated
	 * by a plus. For example, "medium+short" means
	 *        {@link DateFormats#getDateTimeFormat} with the medium date styling and
	 * the short time styling.
	 *
	 * <p>In additions, the format could be a combination of the following pattern letters:
	 * <table border=0 cellspacing=3 cellpadding=0>
	 *
	 * <tr bgcolor="#ccccff">
	 *     <th align=left>Letter
	 *     <th align=left>Date or Time Component
	 *     <th align=left>Presentation
	 *     <th align=left>Examples
	 * <tr>
	 *     <td><code>G</code>
	 *     <td>Era designator
	 *     <td><a href="#text">Text</a>
	 *     <td><code>AD</code>
	 *
	 * <tr bgcolor="#eeeeff">
	 *     <td><code>y</code>
	 *     <td>Year
	 *     <td><a href="#year">Year</a>
	 *     <td><code>1996</code>; <code>96</code>
	 * <tr>
	 *     <td><code>M</code>
	 *
	 *     <td>Month in year
	 *     <td><a href="#month">Month</a>
	 *     <td><code>July</code>; <code>Jul</code>; <code>07</code>
	 * <tr bgcolor="#eeeeff">
	 *     <td><code>w</code>
	 *     <td>Week in year (starting at 1)
	 *     <td><a href="#number">Number</a>
	 *
	 *     <td><code>27</code>
	 * <tr>
	 *     <td><code>W</code>
	 *     <td>Week in month (starting at 1)
	 *     <td><a href="#number">Number</a>
	 *     <td><code>2</code>
	 * <tr bgcolor="#eeeeff">
	 *
	 *     <td><code>D</code>
	 *     <td>Day in year (starting at 1)
	 *     <td><a href="#number">Number</a>
	 *     <td><code>189</code>
	 * <tr>
	 *     <td><code>d</code>
	 *     <td>Day in month (starting at 1)
	 *     <td><a href="#number">Number</a>
	 *
	 *     <td><code>10</code>
	 * <tr bgcolor="#eeeeff">
	 *     <td><code>F</code>
	 *     <td>Day of week in month
	 *     <td><a href="#number">Number</a>
	 *     <td><code>2</code>
	 * <tr>
	 *
	 *     <td><code>E</code>
	 *     <td>Day in week
	 *     <td><a href="#text">Text</a>
	 *     <td><code>Tuesday</code>; <code>Tue</code>
	 * </table>
	 *
	 * @param format The format of the datebox.
	 * <p>Default: {@link Datebox#DEFAULT_FORMAT}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	IDatebox withFormat(@Nullable String format);

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default String getRealFormat() {
		Locale _locale = getLocale();
		String format = getFormat();
		if (format != null && format.length() != 0) {
			int ds = format.indexOf(43);
			if (ds > 0) {
				int ts = toStyle(format.substring(ds + 1));
				if (ts != -111) {
					ds = toStyle(format.substring(0, ds));
					if (ds != -111) {
						return DateFormats.getDateTimeFormat(ds, ts, _locale,
								Datebox.DEFAULT_FORMAT + " "
										+ Timebox.DEFAULT_FORMAT);
					}
				}
			} else {
				ds = toStyle(format);
				if (ds != -111) {
					return DateFormats.getDateFormat(ds, _locale, Datebox.DEFAULT_FORMAT);
				}
			}

			return format;
		} else {
			return DateFormats.getDateFormat(DateFormat.DEFAULT, _locale, Datebox.DEFAULT_FORMAT);
		}
	}

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the value for datebox.
	 */
	static IDatebox of(Date date) {
		return new IDatebox.Builder().setValue(date).build();
	}

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the value for datebox.
	 */
	static IDatebox of(Temporal date) {
		if (date instanceof ZonedDateTime) return new IDatebox.Builder().setValueInZonedDateTime((ZonedDateTime) date).build();
		else if (date instanceof LocalDateTime) return new IDatebox.Builder().setValueInLocalDateTime((LocalDateTime) date).build();
		else if (date instanceof LocalDate) return new IDatebox.Builder().setValueInLocalDate((LocalDate) date).build();
		else if (date instanceof LocalTime) return new IDatebox.Builder().setValueInLocalTime((LocalTime) date).build();
		else throw new WrongValueException("unsupported date: " + date);
	}

	/**
	 * Return the instance of the given format and date.
	 * @param format The format of the datebox
	 * @param date The date to indicate the value for datebox.
	 */
	static IDatebox of(String format, Date date) {
		return new IDatebox.Builder().setFormat(format).setValue(date).build();
	}

	/**
	 * Return the instance of the given format and date.
	 * @param format The format of the datebox
	 * @param date The date to indicate the value for datebox.
	 */
	static IDatebox of(String format, Temporal date) {
		Builder builder = new IDatebox.Builder().setFormat(format);
		if (date instanceof ZonedDateTime) builder.setValueInZonedDateTime((ZonedDateTime) date);
		else if (date instanceof LocalDateTime) builder.setValueInLocalDateTime((LocalDateTime) date);
		else if (date instanceof LocalDate) builder.setValueInLocalDate((LocalDate) date);
		else if (date instanceof LocalTime) builder.setValueInLocalTime((LocalTime) date);
		else throw new WrongValueException("unsupported date: " + date);
		return builder.build();
	}

	/**
	 * Returns the instance with the given locale.
	 * @param locale The datebox locale
	 */
	static IDatebox ofLocale(String locale) {
		if (locale != null && locale.length() > 0)
			return new IDatebox.Builder().setLocale(Locales.getLocale(locale)).build();
		else throw new WrongValueException("locale cannot be null or empty");
	}

	/**
	 * Returns the instance with the given locale.
	 * @param locale The datebox locale
	 */
	static IDatebox ofLocale(Locale locale) {
		return new IDatebox.Builder().setLocale(locale).build();
	}

	/**
	 * Returns the instance with the given cols.
	 * @param cols The cols which determines the visible width
	 */
	static ITimebox ofCols(int cols) {
		return new ITimebox.Builder().setCols(cols).build();
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The datebox constraint
	 */
	static IDatebox ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given format.
	 * @param format The datebox format
	 */
	static IDatebox ofFormat(String format) {
		return new IDatebox.Builder().setFormat(format).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static IDatebox ofId(String id) {
		return new IDatebox.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IDateTimeFormatInputElement.super.renderProperties(renderer);
		boolean _btnVisible = isButtonVisible();
		if (!_btnVisible)
			renderer.render("buttonVisible", false);
		boolean _lenient = isLenient();
		if (!_lenient)
			renderer.render("lenient", false);
		boolean _dtzonesReadonly = isTimeZonesReadonly();
		if (_dtzonesReadonly)
			renderer.render("timeZonesReadonly", true);
		List<TimeZone> _dtzones = getDisplayedTimeZones();
		if (_dtzones != null) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < _dtzones.size(); i++) {
				if (i != 0)
					sb.append(',');
				TimeZone tz = _dtzones.get(i);
				sb.append(tz.getID());
			}
			renderer.render("displayedTimeZones", sb.toString());
		}

		render(renderer, "weekOfYear", isWeekOfYear());
		render(renderer, "position", getPosition());

		Locale _locale = getLocale();
		String format = getFormat();
		if (format == null || format.length() == 0)
			format =  DateFormats.getDateFormat(DateFormat.DEFAULT, _locale, "yyyy/MM/dd");; //backward compatible

		int ds = format.indexOf('+');
		if (ds > 0) {
			int ts = toStyle(format.substring(ds + 1));
			if (ts != -111) {
				ds = toStyle(format.substring(0, ds));
				if (ds != -111)
					format =  DateFormats.getDateTimeFormat(ds, ts, _locale,
							"yyyy/MM/dd") + " " + "HH:mm";
			}
		} else {
			ds = toStyle(format);
			if (ds != -111)
				format =  DateFormats.getDateFormat(ds, _locale, "yyyy/MM/dd");
		}
		renderer.render("localizedFormat", new SimpleDateFormat(format, _locale != null ? _locale : Locales.getCurrent())
				.toLocalizedPattern());

		String unformater = getUnformater();
		if (!Strings.isBlank(unformater))
			renderer.render("unformater", unformater);

		if (_locale != null) {
			final String localeName = _locale.toString();
			if (org.zkoss.zk.ui.impl.Utils.markClientInfoPerDesktop(Executions.getCurrent().getDesktop(),
					this.getClass().getName() + localeName)) {
				renderer.render("localizedSymbols", new Object[] { localeName, loadSymbols(_locale) });
			} else renderer.render("localizedSymbols", new Object[] { localeName, null });
		}

		if (isStrictDate())
			render(renderer, "strictDate", isStrictDate());

		render(renderer, "showTodayLink", getShowTodayLink());
		render(renderer, "todayLinkLabel", getTodayLinkLabel());

		LocalDateTime _defaultDateTime =  getDefaultDateTime();
		if (_defaultDateTime != null)
			render(renderer, "defaultDateTime", toDate(_defaultDateTime));
		String _selectLevel = getSelectLevel();
		if (!"day".equals(_selectLevel))
			render(renderer, "selectLevel", _selectLevel);

		boolean _closePopupOnTimezoneChange = getClosePopupOnTimezoneChange();
		if (!_closePopupOnTimezoneChange)
			renderer.render("closePopupOnTimezoneChange", false);
	}

	/**
	 * Builds instances of type {@link IDatebox IDatebox}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableIDatebox.Builder {
	}

	/**
	 * Builds an updater of type {@link IDatebox} for {@link UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see SmartUpdater
	 */
	class Updater extends IDateboxUpdater {}
}