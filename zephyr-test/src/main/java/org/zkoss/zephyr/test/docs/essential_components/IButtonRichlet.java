/* IButtonRichlet.java

	Purpose:

	Description:

	History:
		Wed Mar 30 15:39:00 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IButtonBase;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IButton} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Button">IButton</a>,
 * if any.
 *
 * @author katherine
 * @see IButton
 */
@RichletMapping("/essential_components/iButton")
public class IButtonRichlet implements StatelessRichlet {
	@RichletMapping("/autodisable")
	public List<IComponent> autodisable() {
		return Arrays.asList(
				IButton.ofId("btn").withAutodisable("self"),
				IButton.of("change Autodisable").withAction(this::changeAutodisable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodisable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().autodisable(null));
	}

	@RichletMapping("/dir")
	public List<IComponent> dir() {
		return Arrays.asList(
				IButton.ofId("btn").withDir(IButtonBase.Direction.REVERSE).withImage("/zephyr/ZK-Logo.gif"),
				IButton.of("change Dir").withAction(this::changeDir)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDir() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().dir("normal"));
	}

	@RichletMapping("/disabled")
	public List<IComponent> disabled() {
		return Arrays.asList(
				IButton.ofId("btn").withDisabled(true),
				IButton.of("change Disabled").withAction(this::changeDisabled)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().disabled(false));
	}

	@RichletMapping("/href")
	public List<IComponent> href() {
		return Arrays.asList(
				IButton.ofId("btn").withHref("www.google.com"),
				IButton.of("change Href").withAction(this::changeHref)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHref() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().href("www.yahoo.com"));
	}

	@RichletMapping("/target")
	public List<IComponent> target() {
		return Arrays.asList(
				IButton.ofId("btn").withTarget("go").withHref("www.google.com"),
				IButton.of("change Target").withAction(this::changeTarget)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTarget() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().target("go2"));
	}

	@RichletMapping("/type")
	public List<IComponent> type() {
		return Arrays.asList(
				IButton.ofId("btn").withType(IButtonBase.Type.SUBMIT),
				IButton.of("change Type").withAction(this::changeType)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeType() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().type("button"));
	}

	@RichletMapping("/upload")
	public List<IComponent> upload() {
		return Arrays.asList(
				IButton.ofId("btn").withUpload("multiple=true"),
				IButton.of("change Upload").withAction(this::changeUpload)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeUpload() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().upload("multiple=false"));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				IButton.of("btn", "/zephyr/ZK-Logo.gif").withId("btn").withOrient(IButtonBase.Orient.VERTICAL),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("btn"), new IButton.Updater().orient("horizontal"));
	}
}