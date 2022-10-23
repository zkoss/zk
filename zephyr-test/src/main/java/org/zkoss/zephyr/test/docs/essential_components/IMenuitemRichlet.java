/* IMenuitemRichlet.java

	Purpose:

	Description:

	History:
		Tue Apr 12 11:40:56 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IMenu;
import org.zkoss.zephyr.zpr.IMenubar;
import org.zkoss.zephyr.zpr.IMenuitem;
import org.zkoss.zephyr.zpr.IMenupopup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IMenuitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Menu/Menuitem">IMenuitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IMenuitem
 */
@RichletMapping("/essential_components/iMenuitem")
public class IMenuitemRichlet implements StatelessRichlet {
	@RichletMapping("/autocheck")
	public List<IComponent> autocheck() {
		return Arrays.asList(
				IButton.of("change autocheck").withAction(this::changeAutocheck),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withAutocheck(true).withCheckmark(true))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutocheck() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().autocheck(false));
	}

	@RichletMapping("/autodisable")
	public List<IComponent> autodisable() {
		return Arrays.asList(
				IButton.of("change autodisable").withId("btn").withAction(this::changeAutodisable),
				IButton.of("button 2").withId("btn2"),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withAutodisable("btn2"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodisable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().autodisable("btn"));
	}

	@RichletMapping("/checked")
	public List<IComponent> checked() {
		return Arrays.asList(
				IButton.of("change checked").withAction(this::changeChecked),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withChecked(true).withCheckmark(true))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeChecked() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().checked(false));
	}

	@RichletMapping("/checkmark")
	public List<IComponent> checkmark() {
		return Arrays.asList(
				IButton.of("change checkmark").withAction(this::changeCheckmark),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withCheckmark(true))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCheckmark() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().checkmark(false));
	}

	@RichletMapping("/href")
	public List<IComponent> href() {
		return Arrays.asList(
				IButton.of("change href").withAction(this::changeHref),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withHref("www.google.com"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHref() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().href("www.yahoo.com"));
	}

	@RichletMapping("/target")
	public List<IComponent> target() {
		return Arrays.asList(
				IButton.of("change target").withAction(this::changeTarget),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withTarget("target1"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTarget() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().target("target2"));
	}

	@RichletMapping("/upload")
	public List<IComponent> upload() {
		return Arrays.asList(
				IButton.of("change upload").withAction(this::changeUpload),
				IMenubar.of(
						IMenu.DEFAULT.withChild(
								IMenupopup.of(IMenuitem.of("item 1").withId("item").withUpload("multiple=true"))
						)
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeUpload() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new IMenuitem.Updater().upload("multiple=false"));
	}
}