/* IDivRichlet.java

	Purpose:

	Description:

	History:
		Thu Mar 10 16:01:23 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IDiv} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Div">IDiv</a>,
 * if any.
 *
 * @author katherine
 * @see IDiv
 */
@RichletMapping("/containers/iDiv")
public class IDivRichlet implements StatelessRichlet {
	@RichletMapping("/size")
	public List<IComponent> size() {
		return Arrays.asList(
				IDiv.ofSize("10px", "10px").withId("div").withStyle("border: 1px solid black;"),
				IButton.of("change size").withAction(this::changeSize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("div"), new IDiv.Updater().width("100px").height("100px"));
	}

	@RichletMapping("/children")
	public List<IComponent> children() {
		return Arrays.asList(
				IDiv.ofId("div").withChildren(
						IDiv.ofSize("20px", "20px").withStyle("border: 1px solid black;").withChildren(
								IDiv.ofSize("10px", "10px").withStyle("border: 1px solid black;")
						)
				),
				IButton.of("change children").withAction(this::changeChildren)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeChildren() {
		UiAgent.getCurrent().replaceChildren(Locator.ofId("div"), ILabel.of("change children"));
	}
}