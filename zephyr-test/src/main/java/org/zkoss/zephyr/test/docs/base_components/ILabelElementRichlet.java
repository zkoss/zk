/* ILabelElementRichlet.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:54:08 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.zpr.ILabelElement;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.ICaption;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ILabelElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/LabelElement">ILabelElement</a>,
 * if any.
 *
 * @author katherine
 * @see ILabelElement
 */
@RichletMapping("/base_components/iLabelElement")
public class ILabelElementRichlet implements StatelessRichlet {
	@RichletMapping("/label")
	public List<IComponent> label() {
		return Arrays.asList(
				IButton.of("label").withId("button"),
				ICaption.of("label").withId("caption"),
				IButton.of("change label").withAction(this::changeLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("button"), new IButton.Updater().label("new label"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("caption"), new ICaption.Updater().label("new label"));
	}
}