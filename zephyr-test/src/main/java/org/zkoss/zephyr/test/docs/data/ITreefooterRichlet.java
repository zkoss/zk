/* ITreefooterRichlet.java

	Purpose:

	Description:

	History:
		Fri Feb 18 18:04:58 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.ITree;
import org.zkoss.zephyr.zpr.ITreefoot;
import org.zkoss.zephyr.zpr.ITreefooter;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.ITreefooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treefooter">ITreefooter</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.ITreefooter
 */
@RichletMapping("/data/itree/iTreefooter")
public class ITreefooterRichlet implements StatelessRichlet {

	@RichletMapping("/image")
	public List<IComponent> image() {
		return Arrays.asList(
				IButton.of("change image").withAction(this::changeImage),
				ITree.DEFAULT.withTreefoot(ITreefoot.of(ITreefooter.ofImage("/zephyr/ZK-Logo.gif").withId("footer")))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("footer"), new ITreefooter.Updater().image("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}
}