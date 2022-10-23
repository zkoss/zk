/* IBandpopupRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 13:06:04 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IBandbox;
import org.zkoss.zephyr.zpr.IBandpopup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IBandpopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Bandpopup">IBandpopup</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IBandpopup
 */
@RichletMapping("/input/iBandpopup")
public class IBandpopupRichlet implements StatelessRichlet {
	@RichletMapping("/default")
	public List<IComponent> bandpopup() {
		return Arrays.asList(
				IBandbox.of("bandbox").withChild(IBandpopup.ofId("bp").withChildren(ITextbox.DEFAULT)),
				IButton.of("replace children").withAction(this::replaceChildren)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void replaceChildren() {
		UiAgent.getCurrent().replaceChildren(Locator.ofId("bp"), ILabel.of("popup"));
	}
}