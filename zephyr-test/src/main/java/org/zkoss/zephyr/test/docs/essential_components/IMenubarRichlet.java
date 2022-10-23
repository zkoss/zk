/* IMenubarRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:42:11 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IMenu;
import org.zkoss.zephyr.zpr.IMenubar;
import org.zkoss.zephyr.zpr.IMenuitem;
import org.zkoss.zephyr.zpr.IMenupopup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IMenubar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menubar">IMenubar</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IMenubar
 */
@RichletMapping("/essential_components/iMenubar")
public class IMenubarRichlet implements StatelessRichlet {
	@RichletMapping("/autodrop")
	public List<IComponent> autodrop() {
		return Arrays.asList(
				IButton.of("change autodrop").withAction(this::changeAutodrop),
				IMenubar.ofId("bar").withAutodrop(true).withChildren(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodrop() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new IMenubar.Updater().autodrop(false));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				IButton.of("change orient").withAction(this::changeOrient),
				IMenubar.ofId("bar").withOrient(IMenubar.Orient.HORIZONTAL).withChildren(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1"), IMenuitem.of("item 2"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new IMenubar.Updater().orient("vertical"));
	}

	@RichletMapping("/scrollable")
	public List<IComponent> scrollable() {
		return Arrays.asList(
				IButton.of("change scrollable").withAction(this::changeScrollable),
				IMenubar.ofId("bar").withScrollable(false).withWidth("200px").withChildren(
						IMenu.DEFAULT.withLabel("Menu 1").withChild(
								IMenupopup.of(IMenuitem.of("item 1"))
						),
						IMenu.DEFAULT.withLabel("Menu 2").withChild(
								IMenupopup.of(IMenuitem.of("item 1"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeScrollable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("bar"), new IMenubar.Updater().scrollable(true));
	}
}