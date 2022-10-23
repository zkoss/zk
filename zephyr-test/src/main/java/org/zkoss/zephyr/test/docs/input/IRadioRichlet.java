/* IRadioRichlet.java

	Purpose:

	Description:

	History:
		Mon Mar 07 16:11:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IRadio;
import org.zkoss.zephyr.zpr.IRadiogroup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IRadio} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Radio">IRadio</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IRadio
 */
@RichletMapping("/input/iRadio")
public class IRadioRichlet implements StatelessRichlet {
	@RichletMapping("/radiogroup")
	public List<IComponent> radiogroup() {
		return Arrays.asList(
				IRadiogroup.of(IRadio.of("a").withId("r"), IRadio.of("b").withRadiogroup("rg2")).withId("rg1"),
				IRadiogroup.ofId("rg2"),
				IButton.of("change radio group").withAction(this::changeRadiogroup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRadiogroup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("r"), new IRadio.Updater().radiogroup("rg2"));
	}

	@RichletMapping("/selected")
	public List<IComponent> selected() {
		return Arrays.asList(
				IRadiogroup.of(IRadio.ofId("r").withSelected(true)),
				IButton.of("change selected").withAction(this::changeSelection)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSelection() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("r"), new IRadio.Updater().checked(false));
	}
}