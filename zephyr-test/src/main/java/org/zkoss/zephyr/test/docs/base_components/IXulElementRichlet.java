/* IXulElement.java

	Purpose:

	Description:

	History:
		3:10 PM 2021/12/29, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import static org.zkoss.zephyr.action.ActionType.onOpen;

import org.zkoss.zephyr.action.data.KeyData;
import org.zkoss.zephyr.action.data.OpenData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDatebox;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IPopup;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IXulElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/XulElement">IXulElement</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IXulElement
 */
@RichletMapping("/base_components/ixulelement")
public class IXulElementRichlet implements StatelessRichlet {

	@RichletMapping("/keystrokeHandling")
	public IComponent keystrokeHandling() {
		return IVlayout.of(ITextbox.DEFAULT, IDatebox.DEFAULT)
				.withCtrlKeys("@c^a#f10^#f3").withAction(this::doKeystrokeHandling);
	}

	@Action(type = Events.ON_CTRL_KEY)
	public void doKeystrokeHandling(KeyData data) {
		Clients.log(data.getKeyCode());
	}

	@RichletMapping("/context")
	public IComponent context() {
		return IDiv.of(ILabel.of("right-click on me").withContext("context"),
				IPopup.ofId("context").withChildren(ILabel.of("Popup context"))
						.withAction(onOpen(this::doOpen)));
	}

	@RichletMapping("/popup")
	public IComponent popup() {
		return IDiv.of(ILabel.of("click on me").withPopup("popup"),
				IPopup.ofId("popup").withChildren(ILabel.of("Popup"))
						.withAction(onOpen(this::doOpen)));
	}

	@RichletMapping("/tooltip")
	public IComponent tooltip() {
		return IDiv.of(ILabel.of("mouse hover on me").withTooltip("tooltip"),
				IPopup.ofId("tooltip").withChildren(ILabel.of("Popup tooltip"))
						.withAction(onOpen(this::doOpen)));
	}

	public void doOpen(OpenData data) {
		Clients.log(data);
	}
}
