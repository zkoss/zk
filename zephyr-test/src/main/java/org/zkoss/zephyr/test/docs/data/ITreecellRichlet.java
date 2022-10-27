/* ITreecell.java

	Purpose:

	Description:

	History:
		Fri Feb 18 17:34:23 CST 2022, Created by katherine

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
import org.zkoss.stateless.zpr.IDiv;
import org.zkoss.stateless.zpr.ITree;
import org.zkoss.stateless.zpr.ITreecell;
import org.zkoss.stateless.zpr.ITreechildren;
import org.zkoss.stateless.zpr.ITreecol;
import org.zkoss.stateless.zpr.ITreecols;
import org.zkoss.stateless.zpr.ITreeitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreecell} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treecell">ITreecell</a>,
 * if any.
 *
 * @author katherine
 * @see ITreecell
 */
@RichletMapping("/data/itree/iTreecell")
public class ITreecellRichlet implements StatelessRichlet {

	@RichletMapping("/span")
	public List<IComponent> span() {
		return Arrays.asList(
			IButton.of("change image").withAction(this::changeSpan),
			ITree.DEFAULT.withTreecols(ITreecols.of(
						ITreecol.of("Name"),
						ITreecol.of(""),
						ITreecol.of("Description")))
				.withTreechildren(ITreechildren.of(
						ITreeitem.ofTreecells(
								ITreecell.of("Item 1").withSpan(2).withId("cell"),
								ITreecell.of("Item 1 description"))))
			);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSpan() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cell"), new ITreecell.Updater().span(1));
	}

	@RichletMapping("/image")
	public List<IComponent> image() {
		return Arrays.asList(
			IButton.of("change image").withAction(this::changeImage),
			ITree.DEFAULT.withTreechildren(ITreechildren.of(
				ITreeitem.ofTreecells(
					ITreecell.ofImage("/zephyr/ZK-Logo.gif").withId("cell"),
					ITreecell.of("Item 1 description"))))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cell"),
				new ITreecell.Updater().image("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

	@RichletMapping("/widthAndHflex")
	public IComponent widthAndHflex() {
		return IDiv.of(IButton.of("check not allow to set width").withAction(this::doWidthError),
				IButton.of("check not allow to set hflex").withAction(this::doHflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthError() {
		ITreecell.of("Item 1").withWidth("100px");
	}

	@Action(type = Events.ON_CLICK)
	public void doHflexError() {
		ITreecell.of("Item 1").withHflex("1");
	}
}