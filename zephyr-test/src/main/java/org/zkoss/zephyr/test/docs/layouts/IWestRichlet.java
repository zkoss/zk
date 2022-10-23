/* IWestRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:59:00 CST 2022, Created by katherine

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
import org.zkoss.zephyr.zpr.IBorderlayout;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.ICaption;
import org.zkoss.zephyr.zpr.ICenter;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.IWest;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IWest} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/West">IWest</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IWest
 */
@RichletMapping("/data/iWest")
public class IWestRichlet implements StatelessRichlet {
	@RichletMapping("/cmargin")
	public List<IComponent> cmargin() {
		return Arrays.asList(
				IBorderlayout.of(
						IWest.ofId("west").withCmargins("10,0,0,0").withChild(ICaption.of("West"))
								.withCollapsible(true).withSplittable(true),
						ICenter.of(ILabel.of("Center"))
				),
				IButton.of("change cmargins").withAction(this::changeCmargins)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCmargins() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("west"), new IWest.Updater().cmargins("20,0,0,0"));
	}

	@RichletMapping("/size")
	public List<IComponent> size() {
		return Arrays.asList(
				IBorderlayout.of(IWest.ofId("west").withSize("100px"), ICenter.of(ILabel.of("Center"))),
				IButton.of("change size").withAction(this::changeSize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("west"), new IWest.Updater().width("200px"));
	}
}