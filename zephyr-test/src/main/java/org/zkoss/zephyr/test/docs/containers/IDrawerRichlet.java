/* IDrawerRichlet.java

	Purpose:

	Description:

	History:
		3:07 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IGrid;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IRow;
import org.zkoss.stateless.zpr.IVlayout;
import org.zkoss.statelessex.zpr.IDrawer;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.IDrawer} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Drawer">IDrawer</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.statelessex.zpr.IDrawer
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