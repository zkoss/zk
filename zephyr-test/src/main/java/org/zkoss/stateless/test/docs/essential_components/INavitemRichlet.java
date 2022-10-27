/* INavitemRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 06 16:03:07 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.statelessex.sul.INav;
import org.zkoss.statelessex.sul.INavbar;
import org.zkoss.statelessex.sul.INavitem;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.INavitem} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Navitem">INavitem</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.INavitem
 */
@RichletMapping("/essential_components/iNavitem")
public class INavitemRichlet implements StatelessRichlet {
	@RichletMapping("/badgeText")
	public List<IComponent> badgeText() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withOpen(true)
								.withChildren(INavitem.of("a"), INavitem.of("b").withId("item").withBadgeText("1"))
				).withOrient(INavbar.Orient.VERTICAL),
				IButton.of("change badgeText").withAction(this::changeBadgeText)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeBadgeText() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new INavitem.Updater().badgeText("2"));
	}

	@RichletMapping("/content")
	public List<IComponent> content() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withOpen(true)
								.withChildren(INavitem.of("a"), INavitem.ofId("item").withContent("test"))
				).withOrient(INavbar.Orient.VERTICAL),
				IButton.of("change content").withAction(this::changeContent)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeContent() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new INavitem.Updater().content("test2"));
	}

	@RichletMapping("/href")
	public List<IComponent> href() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withOpen(true)
								.withChildren(
										INavitem.of("a"),
										INavitem.of("b").withId("item").withHref("www.google.com")
								)
				).withOrient(INavbar.Orient.VERTICAL),
				IButton.of("change href").withAction(this::changeHref)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeHref() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new INavitem.Updater().href("www.yahoo.com"));
	}

	@RichletMapping("/target")
	public List<IComponent> target() {
		return Arrays.asList(
				INavbar.of(
						INav.ofId("nav").withOpen(true)
								.withChildren(
										INavitem.of("a"),
										INavitem.of("b").withId("item").withHref("www.google.com").withTarget("target1")
								)
				).withOrient(INavbar.Orient.VERTICAL),
				IButton.of("change target").withAction(this::changeTarget)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTarget() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("item"), new INavitem.Updater().target("target2"));
	}

}