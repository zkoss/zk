/* ILongboxRichlet.java

	Purpose:

	Description:

	History:
		Mon Mar 07 15:37:57 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILongbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ILongbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Longbox">ILongbox</a>,
 * if any.
 *
 * @author katherine
 * @see ILongbox
 */
@RichletMapping("/input/iLongbox")
public class ILongboxRichlet implements StatelessRichlet {
	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				ILongbox.ofId("box").withValue(10L),
				IButton.of("change value").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ILongbox.Updater().value(20L));
	}
}