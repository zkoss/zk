/* IMultisliderRichlet.java

	Purpose:

	Description:

	History:
		Mon Mar 07 15:48:48 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.statelessex.zpr.IMultislider;
import org.zkoss.statelessex.zpr.IRangesliderBase;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.IMultislider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Multislider">IMultislider</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.IMultislider
 */
@RichletMapping("/input/iMultislider")
public class IMultisliderRichlet implements StatelessRichlet {
	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				IMultislider.ofId("multislider").withOrient(IRangesliderBase.Orient.VERTICAL),
				IButton.of("change multislider").withAction(this::changeMultislider)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMultislider() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("multislider"), new IMultislider.Updater().orient("horizontal"));
	}

	@RichletMapping("/minMax")
	public List<IComponent> minMax() {
		return Arrays.asList(
				IMultislider.ofId("multislider").withMin(10).withMax(90),
				IButton.of("change min and max").withAction(this::changeMinMax)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinMax() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("multislider"), new IMultislider.Updater().min(20).max(80));
	}
}