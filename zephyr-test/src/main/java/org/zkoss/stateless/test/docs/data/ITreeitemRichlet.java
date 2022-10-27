/* ITreeitemRichlet.java

	Purpose:

	Description:

	History:
		Mon Feb 21 15:30:26 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IDiv;
import org.zkoss.stateless.sul.ITree;
import org.zkoss.stateless.sul.ITreechildren;
import org.zkoss.stateless.sul.ITreefoot;
import org.zkoss.stateless.sul.ITreeitem;
import org.zkoss.stateless.sul.ITreerow;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITreeitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Tree/Treeitem">ITreeitem</a>,
 * if any.
 *
 * @author katherine
 * @see ITreeitem
 */
@RichletMapping("/data/itree/iTreeitem")
public class ITreeitemRichlet implements StatelessRichlet {

	@RichletMapping("/image")
	public IComponent image() {
		return ITree.of(ITreeitem.DEFAULT.withImage("/stateless/ZK-Logo.gif").withId("item"));
	}

	@RichletMapping("/label")
	public IComponent label() {
		return ITree.of(ITreeitem.of("item 1").withId("item"));
	}

	@RichletMapping("/open")
	public List<IComponent> open() {
		return Arrays.asList(
				IButton.of("change open").withAction(this::changeOpen),
				ITree.of(
						ITreeitem.of("item 1").withTreechildren(ITreechildren.of(ITreeitem.of("item 1-1"))),
						ITreeitem.of("item 2").withTreechildren(ITreechildren.of(ITreeitem.of("item 2-1")))
								.withOpen(false).withId("item"))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpen() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ITreeitem.Updater().open(true));
	}

	@RichletMapping("/selectable")
	public List<IComponent> selectable() {
		return Arrays.asList(
				IButton.of("change selectable").withAction(this::changeSelectable),
				ITree.of(
						ITreeitem.of("item 1"),
						ITreeitem.of("item 2").withSelectable(false).withId("item"))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSelectable() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ITreeitem.Updater().selectable(true));
	}

	@RichletMapping("/selected")
	public List<IComponent> selected() {
		return Arrays.asList(
				IButton.of("change selected").withAction(this::changeSelected),
				ITree.of(
						ITreeitem.of("item 1"),
						ITreeitem.of("item 2").withSelected(true).withId("item"))
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeSelected() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ITreeitem.Updater().selected(false));
	}

	@RichletMapping("/children")
	public IComponent children() {
		return ITree.of(
				ITreeitem.of("item 1").withTreechildren(ITreechildren.of(ITreeitem.of("item 1-1"))),
				ITreeitem.DEFAULT.withTreerow(ITreerow.of("item 2")));
	}

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