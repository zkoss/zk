/* ITablechildrenRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 14:27:20 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyrex.zpr.ITablechildren;
import org.zkoss.zephyrex.zpr.ITablelayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.ITablechildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Tablelayout/Tablechildren">ITablechildren</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.ITablechildren
 */
@RichletMapping("/layouts/iTablechildren")
public class ITablechildrenRichlet implements StatelessRichlet {
	@RichletMapping("/colspan")
	public List<IComponent> colspan() {
		IDiv<IAnyGroup> div = IDiv.ofSize("100px", "100px").withStyle("border: 1px solid black");
		return Arrays.asList(
				ITablelayout.of(
						ITablechildren.ofId("tc").withColspan(3).withChildren(div.withChildren(ILabel.of("table 1"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 2"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 3"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 4")))
				).withColumns(3),
				IButton.of("change colspan").withAction(this::changeColspan)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColspan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tc"), new ITablechildren.Updater().colspan(2));
	}

	@RichletMapping("/rowspan")
	public List<IComponent> rowspan() {
		IDiv<IAnyGroup> div = IDiv.ofSize("100px", "100px").withStyle("border: 1px solid black");
		return Arrays.asList(
				ITablelayout.of(
						ITablechildren.ofId("tc").withRowspan(2).withChildren(div.withChildren(ILabel.of("table 1"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 2"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 3"))),
						ITablechildren.of(div.withChildren(ILabel.of("table 4")))
				).withColumns(2),
				IButton.of("change rowspan").withAction(this::changeRowspan)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRowspan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tc"), new ITablechildren.Updater().rowspan(1));
	}
}