/* IDateboxRichlet.java

	Purpose:

	Description:

	History:
		3:32 PM 2022/2/21, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDatebox;
import org.zkoss.zephyr.zpr.IHlayout;
import org.zkoss.zephyr.zpr.IPopup;
import org.zkoss.zephyr.zpr.ISeparator;
import org.zkoss.zk.ui.event.Events;

/**
 * A change of example for {@link org.zkoss.zephyr.zpr.IDatebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Datebox">IDatebox</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IDatebox
 */
@RichletMapping("/input/iDatebox")
public class IDateboxRichlet implements StatelessRichlet {
	@RichletMapping("/displayedTimeZones")
	public IComponent displayedTimeZones() {
		return IDatebox.ofFormat("M/d/yy KK:mm:ss a").withWidth("150px")
				.withDisplayedTimeZones("GMT+12", "GMT+8").withTimeZone("GMT+8")
				.withTimeZonesReadonly(false);
	}

	@RichletMapping("/multipleEras")
	public IComponent multipleEras() {
		return IHlayout.of(
				IDatebox.ofLocale("zh-TW-u-ca-roc"),
				IDatebox.ofLocale("ja-JP-u-ca-japanese"),
				IDatebox.ofLocale("th-TH-u-ca-buddhist")
		);
	}

	@RichletMapping("/buttonVisible")
	public List<IComponent> buttonVisible() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withButtonVisible(false),
				IButton.of("change buttonVisible true").withAction(this::changeButtonVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeButtonVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().buttonVisible(true));
	}

	@RichletMapping("/closePopupOnTimezoneChange")
	public List<IComponent> closePopupOnTimezoneChange() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withClosePopupOnTimezoneChange(false).withDisplayedTimeZones("GMT+12", "GMT+8"),
				IButton.of("change closePopupOnTimezoneChange").withAction(this::changeClosePopupOnTimezoneChange)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeClosePopupOnTimezoneChange() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().closePopupOnTimezoneChange(true));
	}

	@RichletMapping("/defaultDateTime")
	public IComponent defaultDateTime() {
		return IDatebox.ofId("datebox")
				.withDefaultDateTime(LocalDateTime.of(1, Month.APRIL, 1, 1, 1));
	}

	@RichletMapping("/format")
	public List<IComponent> format() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withFormat("dd***MMM yyyy"),
			IButton.of("change format").withAction(this::changeFormat)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeFormat() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().format("dd---MMM,yyyy"));
	}

	@RichletMapping("/lenient")
	public List<IComponent> lenient() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withFormat("mm,yyyy"),
				IButton.of("change changeLenient false").withAction(this::changeLenient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLenient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().lenient(false));
	}

	@RichletMapping("/position")
	public List<IComponent> position() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withPosition(IPopup.Position.END_AFTER),
				ISeparator.DEFAULT.withHeight("10px"),
				IButton.of("change position").withAction(this::changePosition)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePosition() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().position("after_start"));
	}

	@RichletMapping("/selectLevel")
	public List<IComponent> selectLevel() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withSelectLevel("year"),
				IButton.of("change selectLevel").withAction(this::changeSelectLevel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSelectLevel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().selectLevel("day"));
	}

	@RichletMapping("/showTodayLink")
	public List<IComponent> showTodayLink() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withShowTodayLink(true),
				IButton.of("change todaylink").withAction(this::changeShowTodayLink)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeShowTodayLink() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().showTodayLink(false));
	}

	@RichletMapping("/strictDate")
	public List<IComponent> strictDate() {
		return Arrays.asList(
				IDatebox.ofId("datebox"),
				IButton.of("change strictDate").withAction(this::changeStrictDate)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeStrictDate() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().strictDate(true));
	}

	@RichletMapping("/timeZonesReadonly")
	public List<IComponent> timeZonesReadonly() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withTimeZonesReadonly(true).withDisplayedTimeZones("GMT+12", "GMT+8"),
				IButton.of("change timeZonesReadonly").withAction(this::changeTimeZonesReadonly)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTimeZonesReadonly() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().timeZonesReadonly(false));
	}

	@RichletMapping("/todayLinkLabel")
	public List<IComponent> todayLinkLabel() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withTodayLinkLabel("today").withShowTodayLink(true),
				IButton.of("change content").withAction(this::changeTodayLinkLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTodayLinkLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().todayLinkLabel("go to today"));
	}

	@RichletMapping("/weekOfYear")
	public List<IComponent> weekOfYear() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withWeekOfYear(true),
				IButton.of("change weekOfYear").withAction(this::changeWeekOfYear)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeWeekOfYear() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().weekOfYear(false));
	}

	@RichletMapping("/constraint")
	public List<IComponent> constraint() {
		return Arrays.asList(
				IDatebox.ofId("datebox").withConstraint("no past,no future"),
				IButton.of("change constraint").withAction(this::changeConstraint)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeConstraint() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("datebox"), new IDatebox.Updater().constraint("no past"));
	}
}
