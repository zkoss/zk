/* ICalendarRichlet.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:15:56 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.ICalendar;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ICalendar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Calendar">ICalendar</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ICalendar
 */
@RichletMapping("/input/iCalendar")
public class ICalendarRichlet implements StatelessRichlet {

	@RichletMapping("/constraint")
	public IComponent constraint() {
		return IVlayout.of(
				IButton.of("change constraint").withAction(this::changeAutodrop),
				ICalendar.DEFAULT,
				ICalendar.ofConstraint("no past").withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodrop() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"), new ICalendar.Updater().constraint(null));
	}

	@RichletMapping("/name")
	public IComponent name() {
		return IVlayout.of(
				IButton.of("change name").withAction(this::changeName),
				ICalendar.DEFAULT.withName("calendar").withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeName() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"), new ICalendar.Updater().name("calendar2"));
	}

	@RichletMapping("/showTodayLink")
	public IComponent showTodayLink() {
		return IVlayout.of(
				IButton.of("change showTodayLink").withAction(this::changeShowTodayLink),
				ICalendar.DEFAULT,
				ICalendar.DEFAULT.withShowTodayLink(true).withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeShowTodayLink() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"), new ICalendar.Updater().showTodayLink(false));
	}

	@RichletMapping("/todayLinkLabel")
	public IComponent todayLinkLabel() {
		return IVlayout.of(
				IButton.of("change todayLinkLabel").withAction(this::changeTodayLinkLabel),
				ICalendar.DEFAULT.withShowTodayLink(true).withTodayLinkLabel("jump to today").withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTodayLinkLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"), new ICalendar.Updater().todayLinkLabel("today"));
	}

	@RichletMapping("/value")
	public IComponent value() {
		ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime
				.of(1, 1, 1, 1, 1), ZoneId.of("Asia/Taipei"));
		return IVlayout.of(
				IButton.of("change value").withAction(this::changeValue),
				ICalendar.of(Date.from(zonedDateTime.toInstant())).withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		LocalDate value = LocalDate.of(2000, 1, 1);
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"),
				new ICalendar.Updater().value(Date.from(value.atStartOfDay(ZoneId.systemDefault()).toInstant())));
	}

	@RichletMapping("/valueInLocalDate")
	public IComponent valueInLocalDate() {
		LocalDate date = LocalDate.of(2000, 1, 1);
		return ICalendar.of(date);
	}

	@RichletMapping("/valueInLocalDateTime")
	public IComponent valueInLocalDateTime() {
		LocalDateTime localDateTime = LocalDateTime.of(1, 1, 1, 1, 1, 1);
		return ICalendar.of(localDateTime);
	}

	@RichletMapping("/valueInZonedDateTime")
	public IComponent valueInZonedDateTime() {
		ZonedDateTime zonedDateTime = ZonedDateTime.of(LocalDateTime
				.of(1, 1, 1, 1, 1), ZoneId.of("Asia/Taipei"));
		return ICalendar.of(zonedDateTime);
	}

	@RichletMapping("/weekOfYear")
	public IComponent weekOfYear() {
		return IVlayout.of(
				IButton.of("change weekOfYear").withAction(this::changeWeekOfYear),
				ICalendar.DEFAULT,
				ICalendar.DEFAULT.withWeekOfYear(true).withId("calendar")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeWeekOfYear() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("calendar"), new ICalendar.Updater().weekOfYear(false));
	}
}