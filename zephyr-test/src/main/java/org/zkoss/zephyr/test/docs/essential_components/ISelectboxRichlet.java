/* ISelectboxRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 20 14:30:32 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ISelectbox;
import org.zkoss.zephyrex.state.ISelectboxController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ISelectbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Selectbox">ISelectbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ISelectbox
 */
@RichletMapping("/essential_components/iSelectbox")
public class ISelectboxRichlet implements StatelessRichlet {
	@RichletMapping("/children")
	public List<IComponent> children() {
		return Arrays.asList(
				ISelectboxController.of(ISelectbox.of("a", "b", "c").withMultiple(true)).build(),
				initSelectbox()
		);
	}

	@RichletMapping("/maxlength")
	public List<IComponent> maxlength() {
		return Arrays.asList(
				initSelectbox().withMaxlength(4).withId("sb"),
				IButton.of("change maxlength").withAction(this::changeMaxlength)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMaxlength() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISelectbox.Updater().maxlength(6));
	}

	@RichletMapping("/multiple")
	public List<IComponent> multiple() {
		return Arrays.asList(
				initSelectbox().withMultiple(true).withId("sb"),
				IButton.of("change multiple").withAction(this::changeMultiple)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMultiple() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISelectbox.Updater().multiple(false));
	}

	@RichletMapping("/name")
	public List<IComponent> name() {
		return Arrays.asList(
				initSelectbox().withName("selectbox").withId("sb"),
				IButton.of("change name").withAction(this::changeName)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeName() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISelectbox.Updater().name("new selectbox"));
	}

	private ISelectbox initSelectbox() {
		ListModelList model = new ListModelList();
		model.add("item a");
		model.add("item b");
		model.add("item c");
		ISelectbox selectbox = ISelectbox.DEFAULT;
		return ISelectboxController.of(selectbox, model).build();
	}
}