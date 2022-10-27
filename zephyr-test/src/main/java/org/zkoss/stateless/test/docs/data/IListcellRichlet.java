/* IListcellRichlet.java

	Purpose:

	Description:

	History:
		Thu Apr 07 17:01:03 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IListbox;
import org.zkoss.stateless.sul.IListcell;
import org.zkoss.stateless.sul.IListhead;
import org.zkoss.stateless.sul.IListheader;
import org.zkoss.stateless.sul.IListitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IListcell} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listcell">IListcell</a>,
 * if any.
 *
 * @author katherine
 * @see IListcell
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