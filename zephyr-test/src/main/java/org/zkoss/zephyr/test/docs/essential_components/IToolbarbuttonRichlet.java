/* IToolbarbuttonRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 17:16:47 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IToolbar;
import org.zkoss.stateless.zpr.IToolbarbutton;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IToolbarbutton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Toolbarbutton">IToolbarbutton</a>,
 * if any.
 * @author katherine
 * @see IToolbarbutton
 */
@RichletMapping("/essential_components/iToolbarbutton")
public class IToolbarbuttonRichlet implements StatelessRichlet {
	@RichletMapping("/mode")
	public List<IComponent> mode() {
		return Arrays.asList(
				IToolbar.of(IToolbarbutton.of("button").withMode(IToolbarbutton.Mode.TOGGLE).withId("btn"),
						IToolbarbutton.of("button").withMode("default").withId("btn2")),
				IButton.of("change mode").withAction(this::changeMode)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMode() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IToolbarbutton.Updater().mode("default"));
	}

	@RichletMapping("/checked")
	public List<IComponent> checked() {
		return Arrays.asList(
				IToolbar.of(IToolbarbutton.of("button").withChecked(true)
						.withMode(IToolbarbutton.Mode.TOGGLE).withId("btn")),
				IButton.of("change checked").withAction(this::changeChecked)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeChecked() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IToolbarbutton.Updater().checked(false));
	}
}