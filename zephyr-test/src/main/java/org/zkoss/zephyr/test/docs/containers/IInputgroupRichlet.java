/* IInputgroupRichlet.java

	Purpose:

	Description:

	History:
		12:31 PM 2022/3/1, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.containers;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IInputgroup;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyr.zpr.IInputgroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Inputgroup">IInputgroup</a>,
 * if any.
 *
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IInputgroup
 */
@RichletMapping("/containers/iinputgroup")
public class IInputgroupRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return IVlayout.of(
				IInputgroup.of(ILabel.of("@"), ITextbox.DEFAULT),
				IInputgroup.of(ITextbox.DEFAULT.withPlaceholder("Recipient's username"), ILabel.of("@example.com")),
				IInputgroup.of(ILabel.of("$"), ITextbox.DEFAULT, ILabel.of(".00")),
				IInputgroup.of(ILabel.of("With textarea"), ITextbox.ofCols(30).withRows(5).withMultiline(true))
		);
	}

	@RichletMapping("/orient")
	public IComponent orient() {
		return IVlayout.of(
				IInputgroup.ofId("ig").withOrient(IInputgroup.Orient.VERTICAL)
						.withChildren(ILabel.of("@"), ITextbox.DEFAULT),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ig"), new IInputgroup.Updater().orient("horizontal"));
	}
}
