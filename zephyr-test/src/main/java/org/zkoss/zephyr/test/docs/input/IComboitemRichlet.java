/* IComboitemRichlet.java

	Purpose:

	Description:

	History:
		Tue Mar 01 18:14:48 CST 2022, Created by katherine

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
import org.zkoss.stateless.zpr.ICombobox;
import org.zkoss.stateless.zpr.IComboitem;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IComboitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Comboitem">IComboitem</a>,
 * if any.
 *
 * @author katherine
 * @see IComboitem
 */
@RichletMapping("/input/iComboitem")
public class IComboitemRichlet implements StatelessRichlet {

	@RichletMapping("/content")
	public List<IComponent> content() {
		return Arrays.asList(
				ICombobox.of(IComboitem.of("item 1").withContent("content 1").withId("item")).withOpen(true),
				IButton.of("change content").withAction(this::changeContent)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContent() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IComboitem.Updater().content("content 2"));
	}

	@RichletMapping("/description")
	public List<IComponent> description() {
		return Arrays.asList(
				ICombobox.of(IComboitem.of("item 1").withDescription("description 1").withId("item")).withOpen(true),
				IButton.of("change description").withAction(this::changeDescription)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDescription() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IComboitem.Updater().description("description 2"));
	}
}