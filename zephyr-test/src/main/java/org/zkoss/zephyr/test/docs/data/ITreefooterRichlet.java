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

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.stateless.zpr.ITreefoot;
import org.zkoss.stateless.zpr.ITreefooter;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreefooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treefooter">ITreefooter</a>,
 * if any.
 *
 * @author katherine
 * @see ITreefooter
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