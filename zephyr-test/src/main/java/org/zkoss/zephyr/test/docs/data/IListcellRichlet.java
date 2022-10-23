/* IListcellRichlet.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:01:03 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IListbox;
import org.zkoss.zephyr.zpr.IListcell;
import org.zkoss.zephyr.zpr.IListhead;
import org.zkoss.zephyr.zpr.IListheader;
import org.zkoss.zephyr.zpr.IListitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IListcell} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listcell">IListcell</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IListcell
 */
@RichletMapping("/data/iListcell")
public class IListcellRichlet implements StatelessRichlet {
	@RichletMapping("/span")
	public List<IComponent> span() {
		return Arrays.asList(
				IListbox.of(IListitem.of(IListcell.ofId("span").withSpan(2).withLabel("item"))
				).withListhead(IListhead.of(IListheader.of("header 1"), IListheader.of("header 2"))),
				IButton.of("change span").withAction(this::changeSpan)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSpan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("span"), new IListcell.Updater().span(1));
	}
}