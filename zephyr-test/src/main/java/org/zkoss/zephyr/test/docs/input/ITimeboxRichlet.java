/* ITimeboxRichlet.java

	Purpose:

	Description:

	History:
		Fri Mar 04 09:32:45 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.ITimebox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ITimebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Timebox">ITimebox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ITimebox
 */
@RichletMapping("/input/iTimebox")
public class ITimeboxRichlet implements StatelessRichlet {
	@RichletMapping("/buttonVisible")
	public List<IComponent> buttonVisible() {
		return Arrays.asList(
				ITimebox.ofId("tb").withButtonVisible(false),
				IButton.of("change buttonVisible").withAction(this::changeButtonVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeButtonVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tb"), new ITimebox.Updater().buttonVisible(true));
	}
}