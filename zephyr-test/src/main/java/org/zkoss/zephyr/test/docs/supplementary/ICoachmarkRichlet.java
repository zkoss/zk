/* ICoachmarkRichlet.java

	Purpose:

	Description:

	History:
		Wed Apr 20 16:23:58 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.supplementary;

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
import org.zkoss.statelessex.zpr.ICoachmark;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.zpr.ICoachmark} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Supplementary/Coachmark">ICoachmark</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.zpr.ICoachmark
 */
@RichletMapping("/supplementary/iCoachmark")
public class ICoachmarkRichlet implements StatelessRichlet {
	@RichletMapping("/position")
	public List<IComponent> position() {
		return Arrays.asList(
				IButton.of("change position").withAction(this::changePosition).withId("target"),
				ICoachmark.of("target", ILabel.of("this is coachmark1"))
						.withPosition(ICoachmark.Position.AFTER_START).withId("cm")

		);
	}

	@Action(type = Events.ON_CLICK)
	public void changePosition() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cm"), new ICoachmark.Updater().position("end_after"));
	}

	@RichletMapping("/target")
	public List<IComponent> target() {
		return Arrays.asList(
				IButton.of("change target").withAction(this::changeTarget).withId("change"),
				IButton.of("coachmark target").withId("target"),
				ICoachmark.of("target", ILabel.of("this is coachmark")).withId("cm")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTarget() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cm"), new ICoachmark.Updater().target("change").visible(true));
	}
}