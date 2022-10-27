/* EastRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:58:36 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IBorderlayout;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.ICaption;
import org.zkoss.stateless.zpr.ICenter;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.stateless.zpr.IEast;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IEast} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/East">East</a>,
 * if any.
 *
 * @author katherine
 * @see IEast
 */
@RichletMapping("/data/iEast")
public class IEastRichlet implements StatelessRichlet {
	@RichletMapping("/cmargin")
	public List<IComponent> cmargin() {
		return Arrays.asList(
				IBorderlayout.of(
						IEast.ofId("east").withCmargins("10,0,0,0").withChild(ICaption.of("East"))
								.withCollapsible(true).withSplittable(true),
						ICenter.of(ILabel.of("Center"))
				),
				IButton.of("change cmargins").withAction(this::changeCmargins)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCmargins() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("east"), new IEast.Updater().cmargins("20,0,0,0"));
	}

	@RichletMapping("/size")
	public List<IComponent> size() {
		return Arrays.asList(
				IBorderlayout.of(IEast.ofId("east").withSize("100px"), ICenter.of(ILabel.of("Center"))),
				IButton.of("change size").withAction(this::changeSize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("east"), new IEast.Updater().width("200px"));
	}
}