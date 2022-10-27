/* IChosenboxRichlet.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:15:09 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.input;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.statelessex.state.IChosenboxController;
import org.zkoss.statelessex.sul.IChosenbox;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.IChosenbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Chosenbox">IChosenbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IChosenbox
 */
@RichletMapping("/input/iChosenbox")
public class IChosenboxRichlet implements StatelessRichlet {

	@RichletMapping("/creatable")
	public IComponent creatable() {
		return IHlayout.of(
				IButton.of("change creatable").withAction(this::changeCreatable),
				initChosenbox().withCreatable(true),
				initChosenbox("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCreatable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().creatable(true));
	}

	@RichletMapping("/createMessage")
	public IComponent createMessage() {
		return IHlayout.of(
				IButton.of("change createMessage").withAction(this::changeCreateMessage),
				initChosenbox("cb").withCreatable(true).withCreateMessage("Create new item")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCreateMessage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().createMessage("create"));
	}

	@RichletMapping("/disabled")
	public IComponent disabled() {
		return IHlayout.of(
				IButton.of("change disabled").withAction(this::changeDisabled),
				IChosenbox.DEFAULT.withId("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().disabled(true));
	}

	@RichletMapping("/emptyMessage")
	public IComponent emptyMessage() {
		return IHlayout.of(
				IButton.of("change emptyMessage").withAction(this::changeEmptyMessage),
				initChosenbox("cb").withEmptyMessage("empty")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeEmptyMessage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().emptyMessage("empty2"));
	}

	@RichletMapping("/inplace")
	public IComponent inplace() {
		return IHlayout.of(
				IButton.of("change inplace").withAction(this::changeInplace),
				initChosenbox().withInplace(true),
				initChosenbox("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeInplace() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().inplace(true));
	}

	@RichletMapping("/name")
	public IComponent name() {
		return IHlayout.of(
				IButton.of("change name").withAction(this::changeName),
				IChosenbox.DEFAULT.withName("this is chosenbox").withId("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeName() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().name("chosenbox"));
	}

	@RichletMapping("/noResultsText")
	public IComponent noResultsText() {
		return IHlayout.of(
				IButton.of("change noResultsText").withAction(this::changeNoResultsText),
				initChosenbox("cb").withNoResultsText("No such item")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeNoResultsText() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().noResultsText("no result"));
	}

	@RichletMapping("/open")
	public IComponent open() {
		return IHlayout.of(
				IButton.of("change open").withAction(this::changeOpen),
				IChosenbox.DEFAULT.withOpen(true),
				IChosenbox.DEFAULT.withId("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().open(true));
	}

	@RichletMapping("/separator")
	public IComponent separator() {
		return IHlayout.of(
				IButton.of("change separator").withAction(this::changeSeparator),
				initChosenbox("cb").withSeparator("1"),
				initChosenbox()
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSeparator() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new IChosenbox.Updater().separator("2"));
	}

	private IChosenbox initChosenbox() {
		return initChosenbox(null);
	}
	private IChosenbox initChosenbox(String id) {
		String[] data = new String[10];
		for(int j = 0; j < data.length; ++j) {
			data[j] = "item " + j;
		}
		ListModel model = new SimpleListModel(data);
		return IChosenboxController.of((id != null ?
				IChosenbox.ofId(id) :
				IChosenbox.DEFAULT).withWidth("100px"), model).build();
	}
}