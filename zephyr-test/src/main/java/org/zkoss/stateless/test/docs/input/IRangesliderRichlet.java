/* IRangesliderRichlet.java

	Purpose:

	Description:

	History:
		Tue Mar 08 09:54:56 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.input;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.statelessex.sul.IRangeslider;
import org.zkoss.statelessex.sul.IRangesliderBase;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.statelessex.sul.IRangeslider} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Rangeslider">IRangeslider</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.statelessex.sul.IRangeslider
 */
@RichletMapping("/input/iRangeslider")
public class IRangesliderRichlet implements StatelessRichlet {
	@RichletMapping("/marks")
	public IComponent marks() {
		Map<Integer, String> map = new HashMap();
		map.put(0, "a");
		return IRangeslider.ofId("rs").withMarks(map);
	}

	@RichletMapping("/markScale")
	public List<IComponent> markScale() {
		return Arrays.asList(
				IRangeslider.ofId("rs").withMarkScale(50),
				IButton.of("change markScale").withAction(this::changeMarkScale)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMarkScale() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rs"), new IRangeslider.Updater().markScale(10));
	}

	@RichletMapping("/minMax")
	public List<IComponent> minMax() {
		return Arrays.asList(
				IRangeslider.ofId("rs").withMin(10).withMax(90),
				IButton.of("change min and max").withAction(this::changeMinMax)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeMinMax() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rs"), new IRangeslider.Updater().min(0).max(100));
	}

	@RichletMapping("/orient")
	public List<IComponent> orient() {
		return Arrays.asList(
				IRangeslider.ofId("rs").withOrient(IRangesliderBase.Orient.VERTICAL),
				IButton.of("change orient").withAction(this::changeOrient)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeOrient() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rs"), new IRangeslider.Updater().orient("horizontal"));
	}

	@RichletMapping("/step")
	public List<IComponent> step() {
		return Arrays.asList(
				IRangeslider.ofId("rs").withStep(50),
				IButton.of("change step").withAction(this::changeStep)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeStep() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rs"), new IRangeslider.Updater().step(10));
	}

	@RichletMapping("/tooltipVisible")
	public List<IComponent> tooltipVisible() {
		return Arrays.asList(
				IRangeslider.ofId("rs").withTooltipVisible(true),
				IButton.of("change tooltipVisible").withAction(this::changeTooltipVisible)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTooltipVisible() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("rs"), new IRangeslider.Updater().tooltipVisible(false));
	}

	@RichletMapping("/startEndValue")
	public IComponent startEndValue() {
		return IRangeslider.of(10, 20).withId("rs");
	}
}