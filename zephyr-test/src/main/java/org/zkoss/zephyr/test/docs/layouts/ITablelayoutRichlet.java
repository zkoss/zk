/* ITablelayoutRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 14:26:24 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyrex.zpr.ITablechildren;
import org.zkoss.zephyrex.zpr.ITablelayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.ITablelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Tablelayout">ITablelayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ITablelayout
 */
@RichletMapping("/layouts/iTablelayout")
public class ITablelayoutRichlet implements StatelessRichlet {
	@RichletMapping("/columns")
	public List<IComponent> columns() {
		return Arrays.asList(
				ITablelayout.ofId("layout").withColumns(2).withChildren(
								ITablechildren.of(ILabel.of("table 1")),
								ITablechildren.of(ILabel.of("table 2")),
								ITablechildren.of(ILabel.of("table 3"))
						),
				IButton.of("change columns").withAction(this::changeaaa)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeaaa() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("layout"), new ITablelayout.Updater().columns(1));
	}
}