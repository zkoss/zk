/* IDecimalboxRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:42:09 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDecimalbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IDecimalbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Decimalbox">IDecimalbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IDecimalbox
 */
@RichletMapping("/input/iDecimalbox")
public class IDecimalboxRichlet implements StatelessRichlet {

	@RichletMapping("/scale")
	public List<IComponent> scale() {
		return Arrays.asList(
				IDecimalbox.ofId("box").withScale(2),
				IButton.of("change scale").withAction(this::changeScale)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeScale() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IDecimalbox.Updater().scale(1));
	}

	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				IDecimalbox.of("0.12").withId("box"),
				IButton.of("change value").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IDecimalbox.Updater().value(new BigDecimal("0.22")));
	}
}