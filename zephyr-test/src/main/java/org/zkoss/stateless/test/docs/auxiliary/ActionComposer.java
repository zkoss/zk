/* ActionComposer.java

	Purpose:
		
	Description:
		
	History:
		9:47 AM 2022/1/6, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.auxiliary;

import static org.zkoss.stateless.action.ActionTarget.SELF;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link Action}
 * @author jumperchen
 * @see Action
 */
public class ActionComposer implements StatelessComposer {
	public IComponent build(BuildContext ctx) {
		return IDiv.of(ILabel.ofId("msg"),
				ILabel.ofId("msg2"),
				IButton.of("button#btn").withId("btn"),
				IButton.of("button 1"), IButton.of("button 2"), IButton.of("button 3"));
	}
	@Action(from = "#btn", type = Events.ON_CLICK)
	public void doButtonClick() {
		UiAgent.getCurrent().replaceWith(Locator.ofId("msg"),
				ILabel.ofId("msg").withValue("Clicked"));
	}
	@Action(from = "button", type = {Events.ON_CLICK, Events.ON_RIGHT_CLICK})
	public void doAllButtonsClick(@ActionVariable(targetId = SELF, field = "label") String buttonLabel) {
		UiAgent.getCurrent().replaceWith(Locator.ofId("msg2"),
				ILabel.ofId("msg2").withValue(buttonLabel));
	}
}
