/* ISelectboxRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 20 14:30:32 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ISelectbox;
import org.zkoss.statelessex.state.ISelectboxController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModelList;

/**
 * A set of example for {@link ISelectbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Selectbox">ISelectbox</a>,
 * if any.
 *
 * @author katherine
 * @see ISelectbox
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