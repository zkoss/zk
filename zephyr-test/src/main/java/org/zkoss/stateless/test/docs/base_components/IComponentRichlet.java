/* IComponentRichlet.java

	Purpose:

	Description:

	History:
		4:41 PM 2021/12/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.base_components;

import org.zkoss.stateless.action.ActionType;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.ICheckbox;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link IComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/AbstractComponent">IComponent</a>,
 * if any.
 * @author jumperchen
 * @see IComponent
 */
@RichletMapping("/base_components/icomponent")
public class IComponentRichlet implements StatelessRichlet {

	@RichletMapping("")
	public IComponent index() {
		return IVlayout.of(ITextbox.ofId("msg"),
				IButton.of("Submit").withAction(this::doSubmit));
	}

	@Action(type = Events.ON_CLICK)
	public void doSubmit(@ActionVariable(targetId = "msg", field="value") String msg) {
		Clients.alert("Hello " + msg);
	}

	@RichletMapping("/withActions")
	public IComponent withActions() {
		return ICheckbox.ofId("myCheckbox").withActions(ActionType.onCheck(this::doMyCheck),
				ActionType.onRightClick(this::doMyRightClick));
	}

	public void doMyCheck() {
		Clients.alert("do check");
	}

	public void doMyRightClick() {
		Clients.alert("do right click");
	}

	@RichletMapping("/withVisible")
	public IComponent withVisible() {
		return ILabel.of("ILabel").withVisible(false);
	}
}
