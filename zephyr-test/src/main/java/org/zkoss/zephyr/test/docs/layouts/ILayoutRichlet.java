/* ILayoutRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 30 17:05:55 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IHlayout;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ILayout} Java Docs.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ILayout
 */
@RichletMapping("/layouts/iLayout")
public class ILayoutRichlet implements StatelessRichlet {
	@RichletMapping("/spacing")
	public List<IComponent> spacing() {
		return Arrays.asList(
				IVlayout.ofId("vlayout").withSpacing("5px").withChildren(ILabel.of("A"), ILabel.of("B")),
				IHlayout.ofId("hlayout").withSpacing("5px").withChildren(ILabel.of("A"), ILabel.of("B")),
				IButton.of("change spacing").withAction(this::changeSpacing)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSpacing() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("vlayout"), new IVlayout.Updater().spacing("10px"));
		UiAgent.getCurrent().smartUpdate(Locator.ofId("hlayout"), new IHlayout.Updater().spacing("10px"));
	}
}