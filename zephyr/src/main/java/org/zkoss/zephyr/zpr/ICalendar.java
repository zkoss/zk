/* ICalendar.java

	Purpose:

	Description:

	History:
		Thu Nov 18 12:56:01 CST 2021, Created by katherine

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.zpr;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nullable;

import org.immutables.value.Value;

import org.zkoss.json.JavaScriptValue;
import org.zkoss.mesg.Messages;
import org.zkoss.util.TimeZones;
import org.zkoss.zephyr.immutable.ZephyrStyle;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.SmartUpdater;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.sys.ContentRenderer;
import org.zkoss.zul.Calendar;
import org.zkoss.zul.SimpleConstraint;
import org.zkoss.zul.mesg.MZul;

/**
 * Immutable {@link Calendar} component
 *
 * <p>A calendar.</p>
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
 *          <td>onWeekClick</td>
 *          <td>Denotes a user clicks upon a label of week of year. [ZK EE].</td>
 *       </tr>
 *    </tbody>
 * </table>
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
 *
 * <h2>Constraint</h2>
 * <p>You could specify what value to accept for input controls by use of the
 * {@link #withConstraint(String) constraint} attribute. It could be a combination
 * of {@code no future}, {@code no past}, and {@code no today}.</p>
 * <p>It also supports an interval of dates. For example,</p>
 * <pre>
 * <code>
 * IDatebox.ofConstraint("between 20071225 and 20071203");
 * IDatebox.ofConstraint("before 20071225");
 * IDatebox.ofConstraint("after 20071225");
 * </code>
 * </pre>
 *
 * @author katherine
 * @see Calendar
 */
@ZephyrStyle
public interface ICalendar extends IXulElement<ICalendar>, IAnyGroup<ICalendar> {

	/**
	 * Constant for default attributes of this immutable component.
	 */
	ICalendar DEFAULT = new ICalendar.Builder().build();

	/**
	 * Internal use
	 * @hidden for Javadoc
	 */
	@Value.Lazy
	default Class<Calendar> getZKType() {
		return Calendar.class;
	}

	/**
	 * Returns the client widget class.
	 * <p>Default: {@code "zul.db.Calendar"}</p>
	 */
	default String getWidgetClass() {
		return "zul.db.Calendar";
	}

	/**
	 * Returns the client constraint to validate the value entered by a user. if any.
	 * <p>Default: {@code null}</p>
	 */
	@Nullable
	String getConstraint();

	/**
	 * Returns a copy of {@code this} immutable component with the specified
	 * {@code constraint}.
	 *
	 * <p> Sets the client constraint to validate the value entered by a user; you
	 * can specify the following values or Regular Expression.
	 <table border="1" cellspacing="0">
	 * <tr>
	 * <th>Condition</th>
	 * <th>Description
	 * </th>
	 * </tr>
	 * </tbody>
	 * <tbody>
	 * <tr>
	 *     <td>no future</td>
	 *     <td>Date in the future is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no past</td>
	 *     <td>Date in the past is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>no today</td>
	 *     <td>Today is not allowed.</td>
	 * </tr>
	 * <tr>
	 *     <td>between <i>yyyyMMdd</i> and <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed between the specified range.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * ICalendar.ofConstraint("between 20211225 and 20211203");
	 *     </code></pre>
	 * </td>
	 * </tr>
	 * <tr>
	 *     <td>after <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed after (and including) the specified date.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * ICalendar.ofConstraint("after 20211225");
	 *     </code></pre></td>
	 * </tr>
	 * <tr>
	 *     <td>before <i>yyyyMMdd</i></td>
	 *     <td>Date only allowed before (and including) the specified date.
	 *     The format must be {@code yyyyMMdd}, such as
	 *     <pre><code>
	 * ICalendar.ofConstraint("before 20211225");
	 *     </code></pre></td>
	 * </tr></tbody></table>
	 *
	 * @param constraint The client constraint of the component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICalendar withConstraint(@Nullable String constraint);

	/** Returns the name of this component.
	 * <p>Default: {@code null}.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 */
	@Nullable
	String getName();

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code name}.
	 *
	 * <p> Sets the name of this component.
	 * <p>The name is used only to work with "legacy" Web application that
	 * handles user's request by servlets.
	 * It works only with HTTP/HTML-based browsers. It doesn't work
	 * with other kind of clients.
	 * <p>Don't use this method if your application is purely based
	 * on ZK's event-driven model.
	 * @param name The name of this component.
	 * <p>Default: {@code null}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICalendar withName(@Nullable String name);

	/** Returns the value that is assigned to this component, never null.
	 * <p>Default: {@link ZonedDateTime#now()}</p>
	 */
	default Date getValue() {
		return Date.from(ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS).toInstant());
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code value}.
	 *
	 * <p> Assigns a value to this component.
	 *
	 * @param value The value of the calendar.
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICalendar withValue(Date value);

	/**
	 * Returns the value (in ZonedDateTime) that is assigned to this component, never null.
	 *
	 * <p>Default: {@link ZonedDateTime#now()}</p>
	 */
	@Value.Lazy
	default ZonedDateTime getValueInZonedDateTime() {
		return ZonedDateTime.ofInstant(getValue().toInstant(), ZoneId.systemDefault());
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code value}.
	 *
	 * <p> Assigns a value to this component.
	 *
	 * @param value The value of the calendar.
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ICalendar withValueInZonedDateTime(ZonedDateTime value) {
		return withValue(Date.from(value.toInstant()));
	};

	/**
	 * Returns the value (in LocalDateTime) that is assigned to this component, never null.
	 *
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 */
	@Value.Lazy
	default LocalDateTime getValueInLocalDateTime() {
		return getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code value}.
	 *
	 * <p> Assigns a value to this component.
	 *
	 * @param value The value of the calendar.
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ICalendar withValueInLocalDateTime(LocalDateTime value) {
		return withValue(Date.from(value.atZone(ZoneId.systemDefault()).toInstant()));
	};

	/**
	 * Returns the value (in LocalDate) that is assigned to this component, never null.
	 *
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 */
	@Value.Lazy
	default LocalDate getValueInLocalDate() {
		return getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code value}.
	 *
	 * <p> Assigns a value to this component.
	 *
	 * @param value The value of the calendar.
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	default ICalendar withValueInLocalDate(LocalDate value) {
		return withValue(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant()));
	};

	/**
	 * Returns whether enable to show the week number within the current year or not.
	 * <p>Default: {@code false}
	 */
	default boolean isWeekOfYear() {
		return false;
	}

	/**
	 * Returns a copy of {@code this} immutable component with the specified {@code weekOfYear}.
	 *
	 * <p>Sets whether enable to show the week number within the current year or
	 * not. [ZK EE]
	 *
	 * @param weekOfYear Whether enable to show the week number within the current year or
	 * not.
	 * <p>Default: {@link ZonedDateTime#now()}.</p>
	 * @return A modified copy of the {@code this} object
	 */
	ICalendar withWeekOfYear(boolean weekOfYear);

	/**
	 * Returns whether enable to show the link that jump to today in day view
	 * <p>Default: {@code false}
	 */
	default boolean getShowTodayLink() {
		return false;
	}

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
	ICalendar withShowTodayLink(boolean showTodayLink);

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
	ICalendar withTodayLinkLabel(String todayLinkLabel);

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the time for the calendar.
	 */
	static ICalendar of(Date date) {
		return new ICalendar.Builder().setValue(date).build();
	}

	/**
	 * Return the instance of the given date.
	 * @param date The date to indicate the time for the calendar.
	 */
	static ICalendar of(Temporal date) {
		if (date instanceof ZonedDateTime) return ICalendar.DEFAULT.withValueInZonedDateTime((ZonedDateTime) date);
		else if (date instanceof LocalDateTime) return ICalendar.DEFAULT.withValueInLocalDateTime((LocalDateTime) date);
		else if (date instanceof LocalDate) return ICalendar.DEFAULT.withValueInLocalDate((LocalDate) date);
		else throw new WrongValueException("unsupported date: " + date);
	}

	/**
	 * Returns the instance with the given constraint.
	 * @param constraint The calendar constraint
	 */
	static ICalendar ofConstraint(String constraint) {
		Objects.requireNonNull(constraint, "Cannot allow null");
		return new ICalendar.Builder().setConstraint(constraint).build();
	}

	/**
	 * Returns the instance with the given id.
	 * @param id The id to identify this component
	 */
	static ICalendar ofId(String id) {
		return new ICalendar.Builder().setId(id).build();
	}

	/**
	 * Internal use
	 * @param renderer
	 * @throws IOException
	 * @hidden for Javadoc
	 */
	default void renderProperties(ContentRenderer renderer) throws IOException {
		IXulElement.super.renderProperties(renderer);
		String _name = getName();
		if (_name != null)
			render(renderer, "name", _name);
		render(renderer, "defaultTzone", TimeZones.getCurrent().getID());
		render(renderer, "weekOfYear", isWeekOfYear());
		render(renderer, "value", getValue());
		render(renderer, "showTodayLink", getShowTodayLink());
		render(renderer, "todayLinkLabel", getTodayLinkLabel());

		final String constraint = getConstraint();
		if (constraint != null) {
			render(renderer, "constraint", new JavaScriptValue(SimpleConstraint.getInstance(constraint).getClientConstraint()));
		}
	}

	/**
	 * Builds instances of type {@link ICalendar ICalendar}.
	 * Initialize attributes and then invoke the {@link #build()} method to create an
	 * immutable instance.
	 * <p><em>{@code Builder} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 */
	class Builder extends ImmutableICalendar.Builder {}

	/**
	 * Builds an updater of type {@link ICalendar} for {@link org.zkoss.zephyr.ui.UiAgent#smartUpdate(Locator, SmartUpdater)}.
	 * <p><em>{@code Updater} is not thread-safe and generally should not be stored in a field or collection,
	 * but instead used immediately to create instances.</em>
	 * @see org.zkoss.zephyr.ui.SmartUpdater
	 */
	class Updater extends ICalendarUpdater {}
}