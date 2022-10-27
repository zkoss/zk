/* IInputgroupRichlet.java

	Purpose:

	Description:

	History:
		12:31 PM 2022/3/1, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.containers;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IInputgroup;
import org.zkoss.stateless.sul.ILabel;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.stateless.sul.IVlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link IInputgroup} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Containers/Inputgroup">IInputgroup</a>,
 * if any.
 *
 * @author jumperchen
 * @see IInputgroup
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
