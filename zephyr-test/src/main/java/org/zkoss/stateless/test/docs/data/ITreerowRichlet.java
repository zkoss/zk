/* ITreerowRichlet.java

	Purpose:

	Description:

	History:
		Fri Feb 18 18:34:43 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ITree;
import org.zkoss.stateless.sul.ITreechildren;
import org.zkoss.stateless.sul.ITreeitem;
import org.zkoss.stateless.sul.ITreerow;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreerow} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treerow">ITreerow</a>,
 * if any.
 *
 * @author katherine
 * @see ITreerow
 */
@RichletMapping("/data/itree/iTreerow")
public class ITreerowRichlet implements StatelessRichlet {

	@RichletMapping("/image")
	public IComponent image() {
		return ITree.DEFAULT.withTreechildren(ITreechildren.of(
				ITreeitem.ofTreerow(ITreerow.ofImage("/stateless/ZK-Logo.gif"))));
	}

	@RichletMapping("/label")
	public IComponent label() {
		return ITree.DEFAULT.withTreechildren(ITreechildren.of(
				ITreeitem.ofTreerow(ITreerow.of("item 1"))));
	}

	@RichletMapping("/widthAndHflex")
	public IComponent widthAndHflex() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError),
				IButton.of("check not allow to set hflex").withAction(this::doHflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		ITreerow.of("Item 1").withWidth("100px");
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		ITreerow.of("Item 1").withHflex("1");
	}
}