/* IListfooterRichlet.java

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
import org.zkoss.stateless.zpr.IListbox;
import org.zkoss.stateless.zpr.IListfoot;
import org.zkoss.stateless.zpr.IListfooter;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IListfooter} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/IListbox/Listfooter">IListfooter</a>,
 * if any.
 *
 * @author katherine
 * @see IListfooter
 */
@RichletMapping("/data/iList/iListfooter")
public class IListfooterRichlet implements StatelessRichlet {

	@RichletMapping("/image")
	public List<IComponent> image() {
		return Arrays.asList(
				IListbox.DEFAULT.withListfoot(IListfoot.of(
						IListfooter.ofImage("/zephyr/ZK-Logo.gif").withId("footer"))),
				IButton.of("change image").withAction(this::changeImage)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("footer"), new IListfooter.Updater().image("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

}