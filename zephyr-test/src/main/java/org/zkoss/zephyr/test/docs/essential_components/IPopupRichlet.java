/* IPopupRichlet.java

	Purpose:

	Description:

	History:
		11:26 AM 2022/3/15, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IPopup;
import org.zkoss.zephyr.zpr.IPopupBase;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IPopup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Popup">IPopup</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IPopup
 */
@RichletMapping("/essential_components/ipopup")
public class IPopupRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IVlayout.of(
				IButton.of("Popup").withPopup("popup_id"),
				IButton.of("Tooltip").withTooltip("popup_id"),
				IButton.of("Context menu").withContext("popup_id"),
				IPopup.ofId("popup_id").withChildren(
						ILabel.of("this is a popup")
				)
		);
	}

	@RichletMapping("/open")
	public IComponent open() {
		return IVlayout.of(
				IButton.ofId("btn").withLabel("Click me to open").withAction(this::doPopupOpen),
				IButton.of("Click me to open at x, y").withAction(this::doPopupOpenXY),
				IButton.of("Click me to close").withAction(this::doPopupClose),
				IPopup.ofId("popup_id").withChildren(
						ILabel.of("this is a popup")
				)
		);
	}

	@Action(type= Events.ON_CLICK)
	public void doPopupOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("popup_id"),
				new IPopup.Updater().open(new IPopupBase.OpenOptionBuilder().position(
						IPopupBase.Position.AFTER_END).reference("btn")));
	}

	@Action(type= Events.ON_CLICK)
	public void doPopupOpenXY() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("popup_id"),
				new IPopup.Updater().open(new IPopupBase.OpenOptionBuilder().xy(500, 500)));
	}

	@Action(type= Events.ON_CLICK)
	public void doPopupClose() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("popup_id"),
				new IPopup.Updater().close(true));
	}
}
