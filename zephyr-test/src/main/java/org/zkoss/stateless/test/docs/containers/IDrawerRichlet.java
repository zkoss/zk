/* IDrawerRichlet.java

	Purpose:

	Description:

	History:
		3:07 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.containers;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IRow;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.statelessex.sul.IDrawer;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.IDrawer} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Drawer">IDrawer</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.statelessex.sul.IDrawer
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