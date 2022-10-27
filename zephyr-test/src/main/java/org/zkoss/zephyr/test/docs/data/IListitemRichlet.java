/* IListitemRichlet.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:00:19 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IListbox;
import org.zkoss.stateless.zpr.IListitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IListitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listitem">IListitem</a>,
 * if any.
 *
 * @author katherine
 * @see IListitem
 */
@RichletMapping("/data/iListitem")
public class IListitemRichlet implements StatelessRichlet {
	@RichletMapping("/label")
	public List<IComponent> label() {
		return Arrays.asList(
				IListbox.of(IListitem.ofId("item").withLabel("item")),
				IButton.of("change label").withAction(this::changeLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IListitem.Updater().label("new item"));
	}
}