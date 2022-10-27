/* ITreefootRichlet.java

	Purpose:

	Description:

	History:
		Mon Feb 21 14:14:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ITreefoot;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreefoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treefoot">ITreefoot</a>,
 * if any.
 *
 * @author katherine
 * @see ITreefoot
 */
@RichletMapping("/data/itree/iTreefoot")
public class ITreefootRichlet implements StatelessRichlet {

	@RichletMapping("/widthAndHflex")
	public IComponent widthAndHflex() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError),
				IButton.of("check not allow to set hflex").withAction(this::doHflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		ITreefoot.DEFAULT.withWidth("100px");
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		ITreefoot.DEFAULT.withHflex("1");
	}
}