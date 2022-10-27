/* IComboboxRichlet.java

	Purpose:

	Description:

	History:
		Tue Mar 01 18:14:14 CST 2022, Created by katherine

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
import org.zkoss.stateless.zpr.ICombobox;
import org.zkoss.stateless.zpr.IComboitem;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.statelessex.state.IComboboxController;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.SimpleListModel;

/**
 * A set of example for {@link ICombobox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Combobox">ICombobox</a>,
 * if any.
 *
 * @author katherine
 * @see ICombobox
 */
@RichletMapping("/input/iCombobox")
public class IComboboxRichlet implements StatelessRichlet {

	@RichletMapping("/autocomplete")
	public List<IComponent> autocomplete() {
		return Arrays.asList(
				initCombobox().withAutocomplete(false),
				IButton.of("set autocomplete false").withAction(this::doAutocomplete)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doAutocomplete() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().autocomplete(true));
	}

	@RichletMapping("/autodrop")
	public List<IComponent> autodrop() {
		return Arrays.asList(
				initCombobox().withAutodrop(true),
				IButton.of("set autodrop false").withAction(this::doAutodrop)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doAutodrop() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().autodrop(false));
	}

	@RichletMapping("/buttonVisible")
	public List<IComponent> buttonVisible() {
		return Arrays.asList(
				initCombobox().withButtonVisible(false),
				IButton.of("set buttonVisible true").withAction(this::doButtonVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doButtonVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().buttonVisible(true));
	}

	@RichletMapping("/emptySearchMessage")
	public List<IComponent> emptySearchMessage() {
		return Arrays.asList(
				initComboboxWithModel().withEmptySearchMessage("no result").withAutodrop(true),
				IButton.of("change empty search message").withAction(this::doEmptySearchMessage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doEmptySearchMessage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().emptySearchMessage("no result2").autodrop(true));
	}

	@RichletMapping("/iconSclass")
	public List<IComponent> iconSclass() {
		return Arrays.asList(
				initCombobox().withIconSclass("z-icon-user"),
				IButton.of("change iconSclass").withAction(this::doIconSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doIconSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().iconSclass("z-icon-home"));
	}

	@RichletMapping("/instantSelect")
	public List<IComponent> instantSelect() {
		return Arrays.asList(
				initCombobox().withInstantSelect(false).withAction(this::doSelect),
				IButton.of("set instantSelect true").withAction(this::doInstantSelect)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doInstantSelect() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().instantSelect(true));
	}

	@Action(type = Events.ON_SELECT)
	public void doSelect() {
		Clients.log("select");
	}

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				initCombobox().withOpen(false),
				IButton.of("set open true").withAction(this::doOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().open(true));
	}

	@RichletMapping("/popupWidth")
	public List<IComponent> popupWidth() {
		return Arrays.asList(
				initCombobox().withOpen(true).withPopupWidth("100px"),
				IButton.of("change popupWidth").withAction(this::doPopupWidth)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doPopupWidth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("combobox"), new ICombobox.Updater().popupWidth("200px").open(true));
	}

	private ICombobox initCombobox() {
		return ICombobox.of(IComboitem.of("item 1"), IComboitem.of("item 2")).withId("combobox");
	}

	private ICombobox initComboboxWithModel() {
		String[] data = new String[3];
		for(int j=0; j < data.length; ++j) {
			data[j] = "item " + j;
		}

		SimpleListModel strset = new SimpleListModel(data);
		return IComboboxController.of(ICombobox.ofId("combobox"), strset).build();
	}
}