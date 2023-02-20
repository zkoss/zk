/* IListgroupfootRichlet.java

	Purpose:

	Description:

	History:
		Mon Apr 11 14:07:01 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.data;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IListbox;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.statelessex.sul.IListgroup;
import org.zkoss.statelessex.sul.IListgroupfoot;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.IListgroupfoot} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Data/Listbox/Listgroupfoot">IListgroupfoot</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IListgroupfoot
 */
@RichletMapping("/data/iListgroupfoot")
public class IListgroupfootRichlet implements StatelessRichlet {
	@RichletMapping("/label")
	public List<IComponent> label() {
		return Arrays.asList(
				IListbox.of(
						IListgroup.ofId("listgroup").withLabel("head"),
						IListgroupfoot.ofId("listgroupfoot").withLabel("foot")),
				IButton.of("change label").withAction(this::changeLabel)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLabel() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("listgroupfoot"), new IListgroupfoot.Updater().label("new foot"));
	}
}