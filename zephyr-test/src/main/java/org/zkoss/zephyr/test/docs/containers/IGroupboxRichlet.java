/* IGroupboxRichlet.java

	Purpose:

	Description:

	History:
		6:38 PM 2022/2/24, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.ICaption;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IGroupbox;
import org.zkoss.zephyr.zpr.IRadio;
import org.zkoss.zephyr.zpr.IRadiogroup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IGroupbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Groupbox">IGroupbox</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IGroupbox
 */
@RichletMapping("/containers/igroupbox")
public class IGroupboxRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IGroupbox defaultMold() {
		return IGroupbox.of(
				IRadiogroup.of(
						IRadio.of("Apple"),
						IRadio.of("Orange"),
						IRadio.of("Banana")
				)
		).withTitle("Fruits").withWidth("350px");
	}

	@RichletMapping("/mold/3d")
	public IComponent threeDtMold() {
		return IGroupbox.THREE_D.withChildren(
				IRadiogroup.of(
						IRadio.of("Apple"),
						IRadio.of("Orange"),
						IRadio.of("Banana")
				)
		).withTitle("Fruits").withWidth("350px");
	}

	@RichletMapping("/caption")
	public IComponent caption() {
		return defaultMold().withCaption(ICaption.of("caption")).withId("groupbox");
	}

	@RichletMapping("/closable")
	public List<IComponent> closable() {
		return Arrays.asList(
				defaultMold().withClosable(false).withId("groupbox"),
				IButton.of("change closable").withAction(this::changeClosable)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeClosable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("groupbox"), new IGroupbox.Updater().closable(true));
	}

	@RichletMapping("/contentSclass")
	public List<IComponent> contentSclass() {
		return Arrays.asList(
				defaultMold().withContentSclass("z-icon-home").withId("groupbox"),
				IButton.of("change contentSclass").withAction(this::changeContentSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContentSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("groupbox"), new IGroupbox.Updater().contentSclass("z-icon-user"));
	}

	@RichletMapping("/contentStyle")
	public List<IComponent> contentStyle() {
		return Arrays.asList(
				defaultMold().withContentStyle("background-color:blue").withId("groupbox"),
				IButton.of("change contentStyle").withAction(this::changeContentStyle)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContentStyle() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("groupbox"), new IGroupbox.Updater().contentStyle("background-color:red"));
	}

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				defaultMold().withOpen(false).withId("groupbox"),
				IButton.of("change open").withAction(this::changeOpen)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("groupbox"), new IGroupbox.Updater().open(true));
	}

	@RichletMapping("/title")
	public List<IComponent> title() {
		return Arrays.asList(
				defaultMold().withId("groupbox"),
				IButton.of("change title").withAction(this::changeTitle)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTitle() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("groupbox"), new IGroupbox.Updater().title("new title"));
	}
}
