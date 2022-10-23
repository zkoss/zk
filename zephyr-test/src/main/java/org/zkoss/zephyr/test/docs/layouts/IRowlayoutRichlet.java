/* IRowlayoutRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 18 14:28:18 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IWindow;
import org.zkoss.zephyrex.zpr.IRowchildren;
import org.zkoss.zephyrex.zpr.IRowlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.IRowlayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Rowlayout">IRowlayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IRowlayout
 */
@RichletMapping("/layouts/iRowlayout")
public class IRowlayoutRichlet implements StatelessRichlet {
	@RichletMapping("/hflex")
	public IButton hflex() {
		return IButton.of("check not allow to set hflex").withAction(this::doHflexError);
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		IRowlayout.DEFAULT.withHflex("1");
	}

	@RichletMapping("/ncols")
	public List<IComponent> ncols() {
		return Arrays.asList(
				IDiv.of(IRowlayout.ofId("layout").withNcols(8)
						.withChildren(IRowchildren.of(IWindow.ofTitle("ncols")))).withWidth("200px"),
				IButton.of("change ncols").withAction(this::changeNcols)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeNcols() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("layout"), new IRowlayout.Updater().ncols(4));
	}

	@RichletMapping("/spacing")
	public List<IComponent> spacing() {
		return Arrays.asList(
				IRowlayout.ofId("layout").withSpacing("1/4")
						.withChildren(IRowchildren.of(IWindow.ofTitle("spacing")), IRowchildren.of(IWindow.ofTitle("spacing"))),
				IButton.of("change spacing").withAction(this::changeSpacing)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSpacing() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("layout"), new IRowlayout.Updater().spacing("1/3"));
	}

	@RichletMapping("/width")
	public IButton width() {
		return IButton.of("check not allow to set width").withAction(this::doWidthError);
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		IRowlayout.DEFAULT.withWidth("100px");
	}
}