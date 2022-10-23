/* ICardlayoutRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 18 12:06:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyrex.zpr.ICardlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.ICardlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Cardlayout">ICardlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ICardlayout
 */
@RichletMapping("/Layouts/iCardlayout")
public class ICardlayoutRichlet implements StatelessRichlet {
	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				ICardlayout.ofId("cardlayout").withOrient(ICardlayout.Orient.VERTICAL).withChildren(ILabel.of("card1")),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cardlayout"), new ICardlayout.Updater().orient("horizontal"));
	}
}