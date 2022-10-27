/* INorthRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 15:56:48 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IBorderlayout;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.ICaption;
import org.zkoss.stateless.sul.ICenter;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.INorth;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link INorth} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Borderlayout/North">INorth</a>,
 * if any.
 *
 * @author katherine
 * @see INorth
 */
@RichletMapping("/data/iNorth")
public class INorthRichlet implements StatelessRichlet {
	@RichletMapping("/cmargin")
	public List<IComponent> cmargin() {
		return Arrays.asList(
				IBorderlayout.of(
						INorth.ofId("north").withCmargins("10,0,0,0").withChild(ICaption.of("North"))
								.withCollapsible(true).withSplittable(true),
						ICenter.of(ILabel.of("Center"))
				),
				IButton.of("change cmargins").withAction(this::changeCmargins)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCmargins() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("north"), new INorth.Updater().cmargins("20,0,0,0"));
	}

	@RichletMapping("/size")
	public List<IComponent> size() {
		return Arrays.asList(
				IBorderlayout.of(INorth.ofId("north").withSize("100px"), ICenter.of(ILabel.of("Center"))),
				IButton.of("change size").withAction(this::changeSize)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSize() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("north"), new INorth.Updater().height("200px"));
	}
}