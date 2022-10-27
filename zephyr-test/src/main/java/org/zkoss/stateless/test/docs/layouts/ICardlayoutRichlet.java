/* ICardlayoutRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 18 12:06:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.statelessex.sul.ICardlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.ICardlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Cardlayout">ICardlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.ICardlayout
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