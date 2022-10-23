/* IToolbarRichlet.java

	Purpose:

	Description:

	History:
		11:42 AM 2022/3/21, Created by jumperchen

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
import org.zkoss.zephyr.zpr.IToolbar;
import org.zkoss.zephyr.zpr.IToolbarbutton;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IToolbar} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Toolbar">IToolbar</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IToolbar
 */
@RichletMapping("/essential_components/itoolbar")
public class IToolbarRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IToolbar<IToolbarbutton> defaultMold() {
		return IToolbar.of(IToolbarbutton.of("Left"), IToolbarbutton.of("Right"));
	}
	@RichletMapping("/mold/panel")
	public IComponent panelMold() {
		return IToolbar.of(IToolbarbutton.of("Left"), IToolbarbutton.of("Right")).withMold("panel");
	}

	@RichletMapping("/align")
	public List<IComponent> align() {
		return Arrays.asList(
				defaultMold().withId("tb").withAlign("center"),
				IButton.of("change align").withAction(this::changeAlign)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAlign() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tb"), new IToolbar.Updater().align("start"));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				defaultMold().withId("tb").withOrient(IToolbar.Orient.VERTICAL),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tb"), new IToolbar.Updater().orient("horizontal"));
	}

	@RichletMapping("/overflowPopup")
	public List<IComponent> overflowPopup() {
		return Arrays.asList(
				defaultMold().withId("tb").withWidth("50px").withHeight("50px")
						.withOverflowPopup(true).withOverflowPopupIconSclass("z-icon-home"),
				IButton.of("change overflowPopup").withAction(this::changeOverflowPopup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOverflowPopup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tb"), new IToolbar.Updater().overflowPopup(false));
	}

	@RichletMapping("/overflowPopupIconSclass")
	public List<IComponent> overflowPopupIconSclass() {
		return Arrays.asList(
				defaultMold().withId("tb").withWidth("50px").withHeight("50px")
						.withOverflowPopup(true).withOverflowPopupIconSclass("z-icon-home"),
				IButton.of("change overflowPopupIconSclass").withAction(this::changeOverflowPopupIconSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOverflowPopupIconSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("tb"), new IToolbar.Updater().overflowPopupIconSclass("z-icon-user"));
	}
}
