/* IDrawerRichlet.java

	Purpose:

	Description:

	History:
		3:07 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IGrid;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IRow;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zephyrex.zpr.IDrawer;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.IDrawer} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Drawer">IDrawer</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.zephyrex.zpr.IDrawer
 */
@RichletMapping("/containers/idrawer")
public class IDrawerRichlet implements StatelessRichlet {
	@RichletMapping("/example")
	public IComponent example() {
		return IVlayout.of(
				IButton.of("Show File Info").withAction(this::doOpenDrawer),
				IDrawer.ofId("fi").withTitle("File information").withChildren(
						IGrid.of(
								IRow.of(ILabel.of("Name"), ILabel.of("1.jpg")),
								IRow.of(ILabel.of("Size"), ILabel.of("1.8 Megabytes")),
								IRow.of(ILabel.of("Dimensions"), ILabel.of("1920x1080"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doOpenDrawer(UiAgent uiAgent) {
		uiAgent.smartUpdate(Locator.ofId("fi"), new IDrawer.Updater().visible(true));
	}
}