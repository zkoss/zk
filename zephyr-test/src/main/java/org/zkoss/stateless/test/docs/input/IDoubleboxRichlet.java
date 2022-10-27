/* IDoubleboxRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 02 15:49:11 CST 2022, Created by katherine

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
import org.zkoss.stateless.sul.IDoublebox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IDoublebox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Doublebox">IDoublebox</a>,
 * if any.
 *
 * @author katherine
 * @see IDoublebox
 */
@RichletMapping("/input/iDoublebox")
public class IDoubleboxRichlet implements StatelessRichlet {

	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				IDoublebox.of(1.2).withId("box"),
				IButton.of("change value").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IDoublebox.Updater().value(1d));
	}
}