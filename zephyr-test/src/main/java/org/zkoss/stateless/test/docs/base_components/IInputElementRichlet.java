/* IInputElementRichlet.java

	Purpose:

	Description:

	History:
		3:25 PM 2022/2/15, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.base_components;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.zkoss.stateless.sul.IInputElement;
import org.zkoss.xel.fn.CommonFns;
import org.zkoss.stateless.action.data.InputData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.Self;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.util.ActionHandler;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDatebox;
import org.zkoss.stateless.sul.IDecimalbox;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.IGrid;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.IIntbox;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.IRow;
import org.zkoss.stateless.sul.IRows;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.stateless.sul.IWindow;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link IInputElement} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/InputElement">IInputElement</a>,
 * if any.
 * @author jumperchen
 * @see IComponent
 */
@RichletMapping("/base_components/iinputelement")
public class IInputElementRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IGrid.DEFAULT.withRows(
				IRows.of(
						IRow.of(
								ILabel.of("UserName: "),
								ITextbox.of("Jerry").withWidth("150px")
						),
						IRow.of(
								ILabel.of("Password: "),
								ITextbox.of("foo").withType("password").withWidth("150px")
						),
						IRow.of(
								ILabel.of("Phone: "),
								IIntbox.of(12345678).withConstraint("no negative, no zero").withWidth("150px")
						),
						IRow.of(
								ILabel.of("Weight: "),
								IDecimalbox.of("154.32").withFormat("###.##").withWidth("150px")
						),
						IRow.of(
								ILabel.of("Birthday: "),
								IDatebox.ofId("db").withWidth("150px")
						),
						IRow.of(
								ILabel.of("E-mail: "),
								ITextbox.of("zk@zkoss.org")
										.withConstraint("/.+@.+\\.[a-z]+/: Please enter an e-mail address").withWidth("150px")
						)
				)
		);
	}

	@RichletMapping("/builtinConstraints")
	public IComponent builtinConstraints() {
		return IDiv.of(
			ITextbox.ofConstraint("no empty"),
			IIntbox.ofConstraint("no negative,no zero")
		);
	}

	@RichletMapping("/multipleConstraints")
	public IComponent multipleConstraints() {
		return IVlayout.of(
				ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: e-mail address only"),
				IDatebox.ofConstraint("no empty, no future: now or never"),
				ILabel.of("of course, it supports multiple custom messages"),
				IIntbox.ofConstraint("no negative: forbid negative, no positive: forbid positive")
		);
	}

	@RichletMapping("/i18nErrorMessage")
	public IComponent i18nErrorMessage() {
		return ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: " + CommonFns.getLabel("err.email.required"));
	}

	@RichletMapping("/customConstraint")
	public IComponent customConstraint() {
		return ITextbox.DEFAULT.withAction(this::doCustomConstraint);
	}

	@Action(type = Events.ON_CHANGE)
	public void doCustomConstraint(UiAgent uiAgent, Self self, InputData inputData) {
		String value = inputData.getValue();
		if (value != null && new Integer(value).intValue() % 2 == 1) {
			uiAgent.smartUpdate(self, new ITextbox.Updater().errorMessage("Only even numbers are allowed, not " + value));
		}
	}

	@RichletMapping("/displayErrorMessage")
	public IComponent displayErrorMessage() {
		return IWindow.ofTitle("Custom Constraint").withBorder("normal").withChildren(
				IHlayout.of(
						ILabel.of("Enter a number at least 100:"),
						IIntbox.DEFAULT.withAction(this::doDisplayErrorMessage),
						ILabel.ofId("errmsg")
				).withValign("bottom")
		);
	}

	@Action(type = Events.ON_CHANGE)
	public void doDisplayErrorMessage(Self self, InputData inputData) {
		String value = inputData.getValue();
		if (value != null && new Integer(value).intValue() < 100) {
			Clients.wrongValue(self.toComponent(), "At least 100 must be specified");
			UiAgent.getCurrent().smartUpdate(Locator.ofId("errmsg"),
					new ILabel.Updater().value(
							"At least 100 must be specified"));
		}
	}

	@RichletMapping("/value")
	public IComponent value() {
		return IVlayout.of(
				ITextbox.of("test"),
				IDatebox.of(LocalDate.of(2000, 1, 1)).withFormat("yyyy-MM-dd"),
				IIntbox.of(1)
		);
	}

	@RichletMapping("/disabled")
	public IComponent disabled() {
		return IVlayout.of(
				ITextbox.DEFAULT.withDisabled(true),
				IDatebox.DEFAULT.withDisabled(true),
				IIntbox.DEFAULT.withDisabled(true)
		);
	}

	@RichletMapping("/errorbox")
	public IComponent errorbox() {
		return IVlayout.of(
				ITextbox.ofConstraint("no empty").withErrorMessage("error")
						.withErrorboxIconSclass("z-icon-home").withErrorboxSclass("customErrorStyle"),
				IDatebox.ofConstraint("no empty").withErrorMessage("error")
						.withErrorboxIconSclass("z-icon-home").withErrorboxSclass("customErrorStyle"),
				IIntbox.ofConstraint("no empty").withErrorMessage("error")
						.withErrorboxIconSclass("z-icon-home").withErrorboxSclass("customErrorStyle")
		);
	}

	@RichletMapping("/inplace")
	public IComponent inplace() {
		return IVlayout.of(
				ITextbox.DEFAULT.withInplace(true),
				IDatebox.DEFAULT.withInplace(true),
				IIntbox.DEFAULT.withInplace(true)
		);
	}

	@RichletMapping("/attributes")
	public IComponent attributes() {
		Map<String, String> attr = new HashMap<>();
		attr.put("disabled", "disabled");
		return IVlayout.of(
				ITextbox.DEFAULT.withCols(1).withInputAttributes(attr)
						.withMaxlength(3).withName("textbox").withPlaceholder("type some thing here"),
				IDatebox.DEFAULT.withCols(1).withInputAttributes(attr)
						.withMaxlength(3).withName("datebox").withPlaceholder("type some thing here"),
				IIntbox.DEFAULT.withCols(1).withInputAttributes(attr)
						.withMaxlength(3).withName("intbox").withPlaceholder("type some thing here")
		);
	}

	@RichletMapping("/instant")
	public IComponent instant() {
		return IVlayout.of(
				ITextbox.DEFAULT.withInstant(true).withAction(ActionHandler.of(this::doChange)),
				IDatebox.DEFAULT.withInstant(true).withAction(ActionHandler.of(this::doChange)),
				IIntbox.DEFAULT.withInstant(true).withAction(ActionHandler.of(this::doChange)),
				ITextbox.DEFAULT.withInstant(false).withAction(ActionHandler.of(this::doChange)),
				IDatebox.DEFAULT.withInstant(false).withAction(ActionHandler.of(this::doChange)),
				IIntbox.DEFAULT.withInstant(false).withAction(ActionHandler.of(this::doChange))
		);
	}

	@Action(type = Events.ON_CHANGE)
	public void doChange() {
		Clients.log("doChange");
	}
}
