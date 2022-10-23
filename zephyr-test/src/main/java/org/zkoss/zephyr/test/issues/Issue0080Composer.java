/* Issue0080Composer.java

	Purpose:
		
	Description:
		
	History:
		4:44 PM 2021/11/3, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.issues;

import static org.zkoss.zephyr.action.ActionType.onClick;

import org.zkoss.zephyr.ui.BuildContext;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessComposer;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ITextbox;

/**
 * @author jumperchen
 */
public class Issue0080Composer implements StatelessComposer<IDiv<IAnyGroup>> {
	public IDiv build(BuildContext<IDiv<IAnyGroup>> ctx) {
		ITextbox text = ITextbox.ofId("text");
		return ctx.getOwner().withChildren(IButton.of("Click me").withId("btn").withAction(
				onClick((UiAgent uiAgent) -> uiAgent.replaceWith(
						Locator.of(text), text.withValue("OK")))), text);
	}
}