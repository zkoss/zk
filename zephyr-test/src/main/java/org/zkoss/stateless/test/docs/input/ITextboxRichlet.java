/* ITextboxRichlet.java

	Purpose:

	Description:

	History:
		5:20 PM 2022/2/16, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.stateless.sul.ITextboxBase;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITextbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Textbox">ITextbox</a>,
 * if any.
 * @author jumperchen
 * @see ITextbox
 */
@RichletMapping("/input/itextbox")
public class ITextboxRichlet implements StatelessRichlet {
	@RichletMapping("/example")
	public IComponent example() {
		return IHlayout.of(
				ITextbox.of("text..."),
				ITextbox.of("secret").withType(ITextboxBase.Type.PASSWORD),
				ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: Please enter an e-mail address"),
				ITextbox.of("text line1...\ntext line2...").withRows(5).withCols(40)
		);
	}

	@RichletMapping("/rows")
	public List<IComponent> rows() {
		return Arrays.asList(
				IButton.of("change rows").withAction(this::changeRows),
				ITextbox.DEFAULT.withRows(2).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeRows() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ITextbox.Updater().rows(3));
	}

	@RichletMapping("/submitByEnter")
	public IComponent submitByEnter() {
		return IHlayout.of(
				IButton.of("change submitByEnter").withAction(this::changeSubmitByEnter),
				ITextbox.DEFAULT.withRows(5),
				ITextbox.DEFAULT.withRows(5).withSubmitByEnter(true).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSubmitByEnter() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ITextbox.Updater().submitByEnter(false));
	}

	@RichletMapping("/type")
	public IComponent type() {
		return IHlayout.of(
				IButton.of("change type").withAction(this::changeType),
				ITextbox.DEFAULT.withType(ITextboxBase.Type.TEXT).withId("box"),
				ITextbox.DEFAULT.withType(ITextboxBase.Type.PASSWORD),
				ITextbox.DEFAULT.withType(ITextboxBase.Type.EMAIL),
				ITextbox.DEFAULT.withType(ITextboxBase.Type.TELEPHONE),
				ITextbox.DEFAULT.withType(ITextboxBase.Type.URL)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeType() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ITextbox.Updater().type("password"));
	}

	@RichletMapping("/tabbable")
	public IComponent tabbable() {
		return IHlayout.of(
				IButton.of("change tabbable").withAction(this::changeTabbable),
				ITextbox.DEFAULT,
				ITextbox.DEFAULT.withTabbable(true).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTabbable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ITextbox.Updater().tabbable(false));
	}

	@RichletMapping("/constraint")
	public List<IComponent> constraint() {
		return Arrays.asList(
				IButton.of("change constraint").withAction(this::changeConstraint),
				ITextbox.ofConstraint("/.+@.+\\.[a-z]+/: Please enter an e-mail address").withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeConstraint() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new ITextbox.Updater().constraint("no empty"));
	}
}
