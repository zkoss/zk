/* IFooterRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 08 18:04:58 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IFoot;
import org.zkoss.stateless.zpr.IFooter;
import org.zkoss.stateless.zpr.IGrid;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IFooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/IListbox/Footer">IFooter</a>,
 * if any.
 *
 * @author katherine
 * @see IFooter
 */
@RichletMapping("/data/iGrid/iFooter")
public class IFooterRichlet implements StatelessRichlet {

	@RichletMapping("/image")
	public List<IComponent> image() {
		return Arrays.asList(
				IGrid.DEFAULT.withFoot(IFoot.of(
						IFooter.ofImage("/zephyr/ZK-Logo.gif").withId("footer"))),
				IButton.of("change image").withAction(this::changeImage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("footer"), new IFooter.Updater().image("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

}