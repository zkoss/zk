/* ILineitemRichlet.java

	Purpose:

	Description:

	History:
		Thu Apr 07 12:29:28 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.zkoss.web.fn.ServletFns;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.zpr.IButton;
import org.zkoss.stateless.zpr.IComponent;
import org.zkoss.stateless.zpr.ILabel;
import org.zkoss.statelessex.zpr.ILineitem;
import org.zkoss.statelessex.zpr.ILinelayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.ILineitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Lineitem">ILineitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.ILineitem
 */
@RichletMapping("/layouts/iLineitem")
public class ILineitemRichlet implements StatelessRichlet {
	@RichletMapping("/backSpace")
	public List<IComponent> backSpace() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withBackSpace("10px")),
				IButton.of("change backSpace").withAction(this::changeBackSpace)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBackSpace() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().backSpace("20px"));
	}

	@RichletMapping("/frontSpace")
	public List<IComponent> frontSpace() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withFrontSpace("10px")),
				IButton.of("change frontSpace").withAction(this::changeFrontSpace)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeFrontSpace() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().frontSpace("20px"));
	}

	@RichletMapping("/opposite")
	public List<IComponent> opposite() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withChildren(ILabel.of("hello!"), IButton.of("ZK!"))),
				IButton.of("change opposite").withAction(this::changeOpposite)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOpposite() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().opposite(false));
	}

	@RichletMapping("/pointIconSclass")
	public List<IComponent> pointIconSclass() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withPointIconSclass("z-icon-home")),
				IButton.of("change pointIconSclass").withAction(this::changePointIconSclass)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePointIconSclass() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().pointIconSclass("z-icon-user"));
	}

	@RichletMapping("/pointImageContent")
	public List<IComponent> pointImageContent() throws IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream(ServletFns.getCurrentServletContext().getRealPath("/zephyr") + "/ZK-Logo.gif");
		org.zkoss.image.AImage img = new org.zkoss.image.AImage("ZK-Logo-dynamic", fis);
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withPointImageContent(img)),
				IButton.of("change pointImageContent").withAction(this::changePointImageContent)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePointImageContent() throws IOException {
		java.io.FileInputStream fis = new java.io.FileInputStream(ServletFns.getCurrentServletContext().getRealPath("/zephyr") + "/ZK-Logo.gif");
		org.zkoss.image.AImage img = new org.zkoss.image.AImage("ZK-Logo-dynamic-new", fis);
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().pointImageContent(img));
	}

	@RichletMapping("/pointImageSrc")
	public List<IComponent> pointImageSrc() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withPointImageSrc("/zephyr/ZK-Logo.gif")),
				IButton.of("change pointImageSrc").withAction(this::changePointImageSrc)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePointImageSrc() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().pointImageSrc("/zephyr-test/zephyr/ZK-Logo-old.gif"));
	}

	@RichletMapping("/pointStyle")
	public List<IComponent> pointStyle() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withPointStyle("background: red;")),
				IButton.of("change pointStyle").withAction(this::changePointStyle)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePointStyle() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().pointStyle("background: blue;"));
	}

	@RichletMapping("/pointVisible")
	public List<IComponent> pointVisible() {
		return Arrays.asList(
				ILinelayout.of(ILineitem.ofId("item").withPointVisible(false)),
				IButton.of("change pointVisible").withAction(this::changePointVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePointVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new ILineitem.Updater().pointVisible(true));
	}
}