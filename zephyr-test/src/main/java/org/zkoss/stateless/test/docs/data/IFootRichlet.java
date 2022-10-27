/* IFootRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 08 14:14:11 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.IFoot;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IFoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/IListbox/Foot">IFoot</a>,
 * if any.
 *
 * @author katherine
 * @see IFoot
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