/* IDoublespinnerRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:50:32 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IDoublespinner;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IDoublespinner} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/DoubleSpinner">IDoublespinner</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IDoublespinner
 */
@RichletMapping("/input/iDoublespinner")
public class IDoublespinnerRichlet implements StatelessRichlet {

	@RichletMapping("/buttonVisible")
	public List<IComponent> buttonVisible() {
		return Arrays.asList(
				IDoublespinner.ofId("ds").withButtonVisible(false),
				IButton.of("change buttonVisible").withAction(this::changeButtonVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeButtonVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ds"), new IDoublespinner.Updater().buttonVisible(true));
	}

	@RichletMapping("/step")
	public List<IComponent>step() {
		return Arrays.asList(
				IDoublespinner.ofId("ds").withStep(0.2),
				IButton.of("change step").withAction(this::changeStep)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeStep() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ds"), new IDoublespinner.Updater().step(0.1));
	}

	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				IDoublespinner.ofId("ds").withValue(0.1),
				IButton.of("change value").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ds"), new IDoublespinner.Updater().value(1d));
	}
}