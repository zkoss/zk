/* INavbarRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:02:40 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyrex.zpr.INav;
import org.zkoss.zephyrex.zpr.INavbar;
import org.zkoss.zephyrex.zpr.INavitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.INavbar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navbar">INavbar</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.INavbar
 */
@RichletMapping("/essential_components/iNavbar")
public class INavbarRichlet implements StatelessRichlet {
	@RichletMapping("/autoclose")
	public List<IComponent> autoclose() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withLabel("nav 1").withChildren(INavitem.of("a"), INavitem.of("b")),
						INavitem.of("nav 2")
				).withAutoclose(true).withId("bar"),
				IButton.of("change autoclose").withAction(this::changeAutoclose)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutoclose() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new INavbar.Updater().autoclose(false));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				INavbar.of(
						INavitem.of("a"),
						INavitem.of("b")
				).withOrient(INavbar.Orient.VERTICAL).withId("bar"),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new INavbar.Updater().orient("horizontal"));
	}

	@RichletMapping("/collapsed")
	public List<IComponent> collapsed() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withLabel("nav")
				).withCollapsed(true).withId("bar"),
				IButton.of("change collapsed").withAction(this::changeCollapsed)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCollapsed() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new INavbar.Updater().collapsed(false));
	}
}