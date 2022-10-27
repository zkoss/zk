/* ISpanRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 20 16:23:39 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.containers;

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
import org.zkoss.stateless.sul.ISeparator;
import org.zkoss.stateless.sul.ISpan;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ISpan} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Span">ISpan</a>,
 * if any.
 *
 * @author katherine
 * @see ISpan
 */
@RichletMapping("/containers/iSpan")
public class ISpanRichlet implements StatelessRichlet {
	@RichletMapping("/span")
	public List<IComponent> span() {
		return Arrays.asList(
				ISpan.of(ISpan.ofId("span")).withStyle("border: 1px solid black;"),
				ISeparator.DEFAULT,
				IButton.of("change children").withAction(this::changeChildren)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeChildren() {
		UiAgent.getCurrent().replaceChildren(Locator.ofId("span"), ILabel.of("children"));
	}
}