/* ICombobuttonRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 19 17:52:55 CST 2022, Created by katherine

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
import org.zkoss.stateless.zpr.ICombobutton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IPopup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ICombobutton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Combobutton">ICombobutton</a>,
 * if any.
 * @author katherine
 * @see ICombobutton
 */
@RichletMapping("/essential_components/iCombobutton")
public class ICombobuttonRichlet implements StatelessRichlet {
	@RichletMapping("/autodrop")
	public List<IComponent> autodrop() {
		return Arrays.asList(
				ICombobutton.ofId("cb").withAutodrop(true).withChild(IPopup.of(ILabel.of("popup"))),
				IButton.of("change autodrop").withAction(this::changeaaa)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeaaa() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new ICombobutton.Updater().autodrop(false));
	}
}