/* IRadiogroupRichlet.java

	Purpose:

	Description:

	History:
		6:31 PM 2022/2/22, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.IGrid;
import org.zkoss.stateless.zpr.IHlayout;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IRadio;
import org.zkoss.stateless.zpr.IRadiogroup;
import org.zkoss.stateless.zpr.IRow;
import org.zkoss.stateless.zpr.ITextbox;
import org.zkoss.stateless.zpr.IVlayout;
import org.zkoss.stateless.zpr.IWindow;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IRadiogroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Radiogroup">IRadiogroup</a>,
 * if any.
 * @author jumperchen
 * @see IRadiogroup
 */
@RichletMapping("/input/iradiogroup")
public class IRadiogroupRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IWindow.ofTitle("Radiobox & Radio Demo").withWidth("400px")
				.withBorder("normal").withChildren(
				IVlayout.of(
						IRadiogroup.of(
								IRadio.of("Apple"),
								IRadio.of("Orange"),
								IRadio.of("Banana")
						).withAction(this::doRadioCheck), ILabel.of(" You have selected :"),
						ILabel.ofId("fruit").withStyle("color:red")
				)
		);
	}
	@Action(type = Events.ON_CHECK)
	public void doRadioCheck(UiAgent uiAgent, @ActionVariable(targetId = ActionTarget.SELF, field = "selectedItem.label") String label) {
		uiAgent.smartUpdate(Locator.ofId("fruit"), new ILabel.Updater().value(label));
	}

	@RichletMapping("/example/ancestor")
	public IComponent ancestorExample() {
		return IRadiogroup.of(
				IVlayout.of(IHlayout.of(
								IRadio.of("radio 1"),
								IRadio.of("radio 2"),
								IRadio.of("radio 3")
						),IHlayout.of(
								IRadio.of("radio 4"),
								IRadio.of("radio 5"),
								IRadio.of("radio 6")
						)
				)
		);
	}

	@RichletMapping("/input/iradiogroup/example/grid")
	public IComponent gridExample() {
		return IDiv.of(
				IRadiogroup.ofId("popular"),
				IRadiogroup.ofId("fun"),
				IGrid.of(IRow.of(
								ILabel.of("Most popular"),
								IRadio.of("Java").withRadiogroup("popular"),
								IRadio.of("Groovy").withRadiogroup("popular"),
								IRadio.of("C#").withRadiogroup("popular"),
								ITextbox.DEFAULT
						),IRow.of(
								ILabel.of("Most fun"),
								IRadio.of("Open Source").withRadiogroup("fun"),
								IRadio.of("Social Networking").withRadiogroup("fun"),
								IRadio.of("Searching").withRadiogroup("fun"),
								ITextbox.DEFAULT
						)
				)
		);
	}

	@RichletMapping("/name")
	public List<IComponent> name() {
		return Arrays.asList(
				IRadiogroup.of(
						IRadio.of("a"),
						IRadio.of("b")
				).withName("rg1").withId("rg"),
				IButton.of("change name").withAction(ActionHandler.of(this::changeName))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeName() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rg"), new IRadiogroup.Updater().name("rg2"));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				IRadiogroup.of(
						IRadio.of("a"),
						IRadio.of("b")
				).withOrient(IRadiogroup.Orient.VERTICAL).withId("rg"),
				IButton.of("change name").withAction(ActionHandler.of(this::changeOrient))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rg"), new IRadiogroup.Updater().orient("horizontal"));
	}
}
