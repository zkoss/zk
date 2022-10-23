/* ActionRichlet.java

	Purpose:
		
	Description:
		
	History:
		11:55 AM 2022/1/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.auxiliary;

import static org.zkoss.zephyr.action.ActionTarget.SELF;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link Action}
 * @author jumperchen
 * @see Action
 */
@RichletMapping("/auxiliary/action")
public class ActionRichlet implements StatelessRichlet {
	@RichletMapping("/selector/id")
	public IComponent idSelector() {
		return IDiv.of(ILabel.ofId("msg"),
				IButton.ofId("btn"));
	}
	@Action(from = "#btn", type = Events.ON_CLICK)
	public void doButtonClick() {
		UiAgent.getCurrent().replaceWith(Locator.ofId("msg"),
				ILabel.ofId("msg").withValue("Clicked"));
	}

	@RichletMapping("/selector/type")
	public IComponent typeSelector() {
		return IDiv.of(ILabel.ofId("msg2"),
				IButton.of("button 1"), IButton.of("button 2"), IButton.of("button 3"));
	}
	@Action(from = "button", type = {Events.ON_CLICK, Events.ON_RIGHT_CLICK})
	public void doAllButtonsClick(@ActionVariable(targetId = SELF, field = "label") String buttonLabel) {
		UiAgent.getCurrent().replaceWith(Locator.ofId("msg2"),
				ILabel.ofId("msg2").withValue(buttonLabel));
	}
}
