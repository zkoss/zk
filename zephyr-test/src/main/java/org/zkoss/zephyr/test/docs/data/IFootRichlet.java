/* IFootRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 08 14:14:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.IFoot;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IFoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/IListbox/Foot">IFoot</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IFoot
 */
@RichletMapping("/data/iGrid/iFoot")
public class IFootRichlet implements StatelessRichlet {

	@RichletMapping("/widthAndHflex")
	public IComponent widthAndHflex() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError),
				IButton.of("check not allow to set hflex").withAction(this::doHflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		IFoot.DEFAULT.withWidth("100px");
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		IFoot.DEFAULT.withHflex("1");
	}
}