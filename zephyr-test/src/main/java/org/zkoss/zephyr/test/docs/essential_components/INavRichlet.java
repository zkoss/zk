/* INavRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:02:05 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.statelessex.zpr.INav;
import org.zkoss.statelessex.zpr.INavbar;
import org.zkoss.statelessex.zpr.INavitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.INav} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Nav">INav</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.INav
 */
@RichletMapping("/essential_components/iNav")
public class INavRichlet implements StatelessRichlet {
	@RichletMapping("/badgeText")
	public List<IComponent> badgeText() {
		return Arrays.asList(
				INavbar.of(INav.ofId("nav").withBadgeText("1")),
				IButton.of("change badgeText").withAction(this::changeBadgeText)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBadgeText() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("nav"), new INav.Updater().badgeText("2"));
	}

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withOpen(true)
								.withChildren(INavitem.of("a"), INavitem.of("b"))
				).withOrient(INavbar.Orient.VERTICAL),
				IButton.of("change open").withAction(this::changeOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("nav"), new INav.Updater().open(false));
	}
}