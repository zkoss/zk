/* Issue0079Composer.java

	Purpose:

	Description:

	History:
		4:44 PM 2021/11/3, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import static org.zkoss.stateless.action.ActionType.onClick;

import java.util.Arrays;

import org.zkoss.util.Maps;
import org.zkoss.stateless.action.data.ActionData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.ui.BuildContext;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessComposer;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.sul.IAnyGroup;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
public class Issue0079Composer implements StatelessComposer<IDiv<IAnyGroup>> {
	ITextbox text = ITextbox.ofId("text");
	public IDiv<IAnyGroup> build(BuildContext<IDiv<IAnyGroup>> ctx) {
		return ctx.getOwner().withChildren(IButton.of("Click me").withId("btn").withAction(
				onClick(() -> Events.postEvent("onForward", Locator.of(text).toComponent(),
						Maps.of(ActionData.ARGUMENTS, Arrays.asList("OK"))))), text.withAction(
				ActionHandler.of(this::doForward)));
	}

	@Action(type="onForward")
	public void doForward(String msg) {
		UiAgent.getCurrent().replaceWith(Locator.ofId("text"), text.withValue(msg));
	}
}