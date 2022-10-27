/* IIntbox.java

	Purpose:

	Description:

	History:
		Mon Mar 07 14:37:05 CST 2022, Created by katherine

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
import org.zkoss.stateless.zpr.IIntbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IIntbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/IIntbox">IIIntbox</a>,
 * if any.
 *
 * @author katherine
 * @see IIntbox
 */
@RichletMapping("/input/iIntbox")
public class IIntboxRichlet implements StatelessRichlet {
	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				IIntbox.ofId("box").withValue(1),
				IButton.of("change value").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IIntbox.Updater().value(2));
	}
}