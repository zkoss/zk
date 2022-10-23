/* IComponentRichlet.java

	Purpose:

	Description:

	History:
		4:41 PM 2021/12/27, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import org.zkoss.zephyr.action.ActionType;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.ICheckbox;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IComponent} Java Docs.
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
