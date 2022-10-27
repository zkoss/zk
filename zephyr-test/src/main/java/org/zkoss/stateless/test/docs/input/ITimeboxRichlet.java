/* ITimeboxRichlet.java

	Purpose:

	Description:

	History:
		Fri Mar 04 09:32:45 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ITimebox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITimebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Timebox">ITimebox</a>,
 * if any.
 *
 * @author katherine
 * @see ITimebox
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