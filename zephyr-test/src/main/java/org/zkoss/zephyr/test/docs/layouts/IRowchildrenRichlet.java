/* IRowchildrenRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 18 14:27:16 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IWindow;
import org.zkoss.statelessex.zpr.IRowchildren;
import org.zkoss.statelessex.zpr.IRowlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.IRowchildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Rowlayout/Rowchildren">IRowchildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.IRowchildren
 */
@RichletMapping("/layouts/iRowchildren")
public class IRowchildrenRichlet implements StatelessRichlet {
	@RichletMapping("/colspan")
	public List<IComponent> colspan() {
		return Arrays.asList(
				IRowlayout.of(IRowchildren.ofId("rowchildren").withChild(IWindow.ofTitle("colspan")).withColspan(3)),
				IButton.of("change colspan").withAction(this::changeColspan)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColspan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rowchildren"), new IRowchildren.Updater().colspan(2));
	}

	@RichletMapping("/offset")
	public List<IComponent> offset() {
		return Arrays.asList(
				IRowlayout.of(IRowchildren.ofId("rowchildren").withChild(IWindow.ofTitle("offset")).withOffset(3)),
				IButton.of("change offset").withAction(this::changeOffset)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOffset() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rowchildren"), new IRowchildren.Updater().offset(2));
	}
}