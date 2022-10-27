/* ITreechildrenRichlet.java

	Purpose:

	Description:

	History:
		Mon Feb 21 14:13:51 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.ITreechildren;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreechildren} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treechildren">ITreechildren</a>,
 * if any.
 *
 * @author katherine
 * @see ITreechildren
 */
@RichletMapping("/data/itree/iTreechildren")
public class ITreechildrenRichlet implements StatelessRichlet {

	@RichletMapping("/widthAndHflex")
	public IComponent widthAndHflex() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError),
				IButton.of("check not allow to set hflex").withAction(this::doHflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		ITreechildren.DEFAULT.withWidth("100px");
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		ITreechildren.DEFAULT.withHflex("1");
	}
}