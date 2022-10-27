/* ILinelayoutRichlet.java

	Purpose:

	Description:

	History:
		Thu Apr 07 12:28:35 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.layouts;

import java.util.Arrays;
import java.util.List;

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
 * A set of example for {@link org.zkoss.statelessex.zpr.ILinelayout} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Layouts/Linelayout">ILinelayout</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.ILinelayout
 */
@RichletMapping("/layouts/iLinelayout")
public class ILinelayoutRichlet implements StatelessRichlet {
	@RichletMapping("/firstScale")
	public List<IComponent> firstScale() {
		return Arrays.asList(
				ILinelayout.ofId("linelayout").withFirstScale(2)
						.withChildren(ILineitem.of(ILabel.of("1"), ILabel.of("2"))),
				IButton.of("change firstScale").withAction(this::changeFirstScale)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeFirstScale() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("linelayout"), new ILinelayout.Updater().firstScale(3));
	}

	@RichletMapping("/lastScale")
	public List<IComponent> lastScale() {
		return Arrays.asList(
				ILinelayout.ofId("linelayout").withLastScale(2)
						.withChildren(ILineitem.of(ILabel.of("1"), ILabel.of("2"))),
				IButton.of("change lastScale").withAction(this::changeLastScale)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLastScale() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("linelayout"), new ILinelayout.Updater().lastScale(3));
	}

	@RichletMapping("/lineStyle")
	public List<IComponent> lineStyle() {
		return Arrays.asList(
				ILinelayout.ofId("linelayout").withChildren(ILineitem.of(ILabel.of("1"), ILabel.of("2")))
						.withLineStyle("background:red"),
				IButton.of("change lineStyle").withAction(this::changeLineStyle)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeLineStyle() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("linelayout"),
				new ILinelayout.Updater().lineStyle("background:blue"));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				ILinelayout.ofId("linelayout").withOrient(ILinelayout.Orient.HORIZONTAL)
						.withChildren(ILineitem.of(ILabel.of("1"), ILabel.of("2"))),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("linelayout"), new ILinelayout.Updater().orient("vertical"));
	}
}