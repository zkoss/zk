/* IBandpopupRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 13:06:04 CST 2022, Created by katherine

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
import org.zkoss.stateless.sul.IBandbox;
import org.zkoss.stateless.sul.IBandpopup;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IBandpopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Bandpopup">IBandpopup</a>,
 * if any.
 *
 * @author katherine
 * @see IBandpopup
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