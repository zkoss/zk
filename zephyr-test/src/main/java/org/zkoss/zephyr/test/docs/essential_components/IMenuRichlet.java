/* IMenuRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:40:23 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IMenupopup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IMenu} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu">IMenu</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IMenu
 */
@RichletMapping("/essential_components/iMenu")
public class IMenuRichlet implements StatelessRichlet {
	@RichletMapping("/content")
	public List<IComponent> content() {
		return Arrays.asList(
				IButton.of("change content").withAction(this::changeContent),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenu.of("menu", "/zephyr/ZK-Logo.gif")
										.withContent("#color=#184dc6").withId("menu"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContent() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("menu"), new IMenu.Updater().content("#color=#FFFFFF"));
	}
}