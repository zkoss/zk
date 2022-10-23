/* ActionVariableRichlet.java

	Purpose:
		
	Description:
		
	History:
		3:12 PM 2022/1/3, Created by jumperchen

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
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link org.zkoss.zephyr.annotation.ActionVariable}
 * @author jumperchen
 * @see org.zkoss.zephyr.annotation.ActionVariable
 */
@RichletMapping("/auxiliary/actionVariable")
public class ActionVariableRichlet implements StatelessRichlet {
	@RichletMapping("")
	public IComponent index() {
		return IDiv.of(ITextbox.ofId("email").withValue("foo@foo.com"),
				ILabel.ofId("msg"),
				IButton.of("Submit").withAction(this::doSubmit));
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(UiAgent uiAgent, String email,
			// dom element's defaultValue
			@ActionVariable(targetId = "email", field = "defaultValue") String innerText,
			MyEmail myEmail, @ActionVariable(targetId ="email") MyEmail2 myEmail2) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg")
				.withValue(email + " " + innerText + " " + myEmail.email + " " + myEmail2.email));
	}

	public static class MyEmail {
		@ActionVariable
		private String email;

		public String getEmail() {
			return email;
		}
	}

	public static class MyEmail2 {
		@ActionVariable(field = "value")
		private String email;

		public String getEmail() {
			return email;
		}
	}

	@RichletMapping("/combination")
	public IComponent combination() {
		return IDiv.of(ITextbox.ofId("email").withValue("foo@foo.com"),
				ILabel.ofId("msg"),
				IButton.of("Submit").withAction(this::doSubmit2));
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit2(UiAgent uiAgent, @ActionVariable(targetId = SELF, field = "previousSibling.previousSibling.defaultValue") String email,
			@ActionVariable(targetId = "email", field = "a|value") String elseValue,
			@ActionVariable(targetId = SELF, field = "previousSibling.previousSibling.(foo|value)") String complexElseValue) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg")
				.withValue(email + " " + elseValue + " " + complexElseValue));
	}

}
