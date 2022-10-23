/* IBandboxRichlet.java

	Purpose:

	Description:

	History:
		Thu Feb 24 16:14:02 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IBandbox;
import org.zkoss.zephyr.zpr.IBandpopup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IBandbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Bandbox">IBandbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IBandbox
 */
@RichletMapping("/input/iBandbox")
public class IBandboxRichlet implements StatelessRichlet {
	
	private static final IBandpopup bandpopup = IBandpopup.of(ILabel.of("bandpopup"));

	@RichletMapping("/autodrop")
	public IComponent autodrop() {
		return IVlayout.of(
				IButton.of("change autodrop").withAction(this::changeAutodrop),
				IBandbox.of(bandpopup),
				IBandbox.of(bandpopup).withAutodrop(true).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeAutodrop() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IBandbox.Updater().autodrop(false));
	}

	@RichletMapping("/buttonVisible")
	public IComponent buttonVisible() {
		return IVlayout.of(
				IButton.of("change autodrop").withAction(this::changeButtonVisible),
				IBandbox.DEFAULT,
				IBandbox.DEFAULT.withButtonVisible(false).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeButtonVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IBandbox.Updater().buttonVisible(true));
	}

	@RichletMapping("/iconSclass")
	public IComponent iconSclass() {
		return IVlayout.of(
				IButton.of("change iconSclass").withAction(this::changeIconSclass),
				IBandbox.DEFAULT.withIconSclass("z-icon-home").withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeIconSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IBandbox.Updater().iconSclass("z-icon-user"));
	}

	@RichletMapping("/open")
	public IComponent open() {
		return IVlayout.of(
				IButton.of("change open").withAction(this::changeOpen),
				IBandbox.of(bandpopup),
				IBandbox.of(IBandpopup.of(ILabel.of("bandpopup2"))).withOpen(true).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IBandbox.Updater().open(false));
	}

	@RichletMapping("/popupWidth")
	public IComponent popupWidth() {
		return IVlayout.of(
				IButton.of("change popupWidth").withAction(this::changePopupWidth),
				IBandbox.of(bandpopup).withPopupWidth("100px").withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePopupWidth() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IBandbox.Updater().popupWidth("200px"));
	}
}