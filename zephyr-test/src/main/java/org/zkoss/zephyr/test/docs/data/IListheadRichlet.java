/* IListheadRichlet.java

	Purpose:

	Description:

	History:
		Fri Apr 08 18:01:09 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IListbox;
import org.zkoss.zephyr.zpr.IListcell;
import org.zkoss.zephyr.zpr.IListhead;
import org.zkoss.zephyr.zpr.IListheader;
import org.zkoss.zephyr.zpr.IListitem;
import org.zkoss.zephyrex.zpr.IListgroup;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IListhead} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listhead">IListhead</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyr.zpr.IListhead
 */
@RichletMapping("/data/iListhead")
public class IListheadRichlet implements StatelessRichlet {
	@RichletMapping("/columnsgroup")
	public List<IComponent> columnsgroup() {
		return Arrays.asList(
				IListbox.ofListhead(IListhead.of(
								IListheader.of("header 1"), IListheader.of("header 2")).withId("listhead")
								.withColumnsgroup(false).withMenupopup("auto")
						)
						.withChildren(
								IListgroup.of("group1"),
								IListitem.of(IListcell.of("item1"), IListcell.of("item1-1")),
								IListitem.of(IListcell.of("item2"), IListcell.of("item2-1"))
						),
				IButton.of("change columnsgroup").withAction(this::changeColumnsgroup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColumnsgroup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listhead"), new IListhead.Updater().columnsgroup(true));
	}

	@RichletMapping("/columnshide")
	public List<IComponent> columnshide() {
		return Arrays.asList(
				IListbox.ofListhead(IListhead.of(
								IListheader.of("header 1"), IListheader.of("header 2")).withId("listhead")
								.withColumnshide(false).withMenupopup("auto")
						)
						.withChildren(
								IListitem.of(IListcell.of("item1"), IListcell.of("item1-1")),
								IListitem.of(IListcell.of("item2"), IListcell.of("item2-1"))
						),
				IButton.of("change columnshide").withAction(this::changeColumnshide)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColumnshide() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listhead"), new IListhead.Updater().columnshide(true));
	}

	@RichletMapping("/menupopup")
	public List<IComponent> menupopup() {
		return Arrays.asList(
				IListbox.ofListhead(IListhead.of(
								IListheader.of("header 1"), IListheader.of("header 2")).withId("listhead").withMenupopup("auto")
						)
						.withChildren(
								IListitem.of(IListcell.of("item1"), IListcell.of("item1-1")),
								IListitem.of(IListcell.of("item2"), IListcell.of("item2-1"))
						),
				IButton.of("change menupopup").withAction(this::changeMenupopup)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMenupopup() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listhead"), new IListhead.Updater().menupopup("none"));
	}
}