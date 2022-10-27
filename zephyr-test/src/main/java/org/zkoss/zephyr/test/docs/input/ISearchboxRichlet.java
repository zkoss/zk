/* ISearchboxRichlet.java

	Purpose:

	Description:

	History:
		Tue Mar 08 14:41:17 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.statelessex.state.ISearchboxController;
import org.zkoss.statelessex.zpr.ISearchbox;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListModelArray;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.ISearchbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Searchbox">ISearchbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.ISearchbox
 */
@RichletMapping("/input/iSearchbox")
public class ISearchboxRichlet implements StatelessRichlet {
	@RichletMapping("/autoclose")
	public List<IComponent> autoclose() {
		return Arrays.asList(
				initSearchbox().withAutoclose(true),
				IButton.of("changeAutoclose").withAction(this::changeAutoclose)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutoclose() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISearchbox.Updater().autoclose(false));
	}

	@RichletMapping("/multiple")
	public List<IComponent> multiple() {
		return Arrays.asList(
				initSearchbox().withMultiple(true),
				IButton.of("changeMultiple").withAction(this::changeMultiple)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMultiple() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISearchbox.Updater().multiple(false));
	}

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				initSearchbox().withOpen(true),
				IButton.of("changeOpen").withAction(this::changeOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISearchbox.Updater().open(false));
	}

	@RichletMapping("/placeholder")
	public List<IComponent> placeholder() {
		return Arrays.asList(
				initSearchbox().withPlaceholder("search.."),
				IButton.of("changeSearchMessage").withAction(this::changePlaceholder)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePlaceholder() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISearchbox.Updater().placeholder("search"));
	}

	@RichletMapping("/searchMessage")
	public List<IComponent> searchMessage() {
		return Arrays.asList(
				initSearchbox().withSearchMessage("search"),
				IButton.of("changeSearchMessage").withAction(this::changeSearchMessage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSearchMessage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("sb"), new ISearchbox.Updater().searchMessage("doSearch"));
	}

	private ISearchbox initSearchbox() {
		ISearchbox box = ISearchbox.ofId("sb");
		ListModel model = new ListModelArray(new String[] {
				"item 1", "item 2", "item 3"
		});
		return ISearchboxController.of(box, model).build();
	}
}