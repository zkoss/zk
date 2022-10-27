/* IAreaRichlet.java

		Purpose:
		
		Description:
		
		History:
				Mon Feb 07 15:57:38 CST 2022, Created by leon

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.essential_components;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.action.data.MouseData;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IArea;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IImagemap;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of example for {@link IArea} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Imagemap/Area">IArea</a>,
 * if any.
 * @author leon
 * @see IArea
 */
@RichletMapping("/essential_components/iarea")
public class IAreaRichlet implements StatelessRichlet {

	@RichletMapping("/coords")
	public List<IComponent> coords() {
		return Arrays.asList(
				IButton.of("change coords").withAction(this::changeCoords),
				IImagemap.of("/stateless/ZK-Logo.gif").withAction(this::doClick).withChildren(
						IArea.ofId("left").withCoords("0, 0, 45, 80"))
				);
	}

	@Action(type = Events.ON_CLICK)
	public void changeCoords() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("left"), new IArea.Updater().coords("0, 0, 60, 80"));
	}

	@RichletMapping("/overlap")
	public IComponent coordsOverlap() {
		return IImagemap.of("/stateless/ZK-Logo.gif").withAction(this::doClick).withChildren(
				IArea.ofId("left").withCoords("0, 0, 45, 80"),
				IArea.ofId("all").withCoords("0, 0, 90, 80")
		);
	}

	@RichletMapping("/shape")
	public List<IComponent> shape() {
		return Arrays.asList(
				IButton.of("change shape").withAction(this::changeShape),
				IImagemap.of("/stateless/ZK-Logo.gif").withAction(this::doClick).withChildren(
						IArea.ofId("circle1").withShape("circ").withCoords("45, 40, 10"),
						IArea.ofId("circle2").withShape("circle").withCoords("45, 40, 20"),
						IArea.ofId("circle3").withShape(IArea.Shape.CIRCLE).withCoords("45, 40, 30")
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeShape() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("circle3"), new IArea.Updater().shape("rectangle"));
	}

	@Action(type = Events.ON_CLICK)
	public void doClick(MouseData mouseData) {
		Clients.log(mouseData.getArea());
	}

	@RichletMapping("/tabindex")
	public List<IComponent> tabindex() {
		return Arrays.asList(
				IButton.of("change tabindex").withAction(this::changeTabindex),
				IImagemap.of("/stateless/ZK-Logo.gif").withChildren(
						IArea.of("0, 0, 45, 80").withTabindex(1).withId("area1"),
						IArea.of("46, 0, 90, 80").withTabindex(0).withId("area2")
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTabindex() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("area1"), new IArea.Updater().tabindex(0))
				.smartUpdate(Locator.ofId("area2"), new IArea.Updater().tabindex(1));
	}

	@RichletMapping("/tooltiptext")
	public List<IComponent> tooltiptext() {
		return Arrays.asList(
				IButton.of("change tooltiptext").withAction(this::changeTooltiptext),
				IImagemap.of("/stateless/ZK-Logo.gif").withChildren(
						IArea.of("0, 0, 45, 80").withTooltiptext("tooltip").withId("area")
				)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeTooltiptext() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("area"), new IArea.Updater().tooltiptext("new tooltip"));
	}
}
