/* IHtmlBasedComponentRichlet.java

	Purpose:

	Description:

	History:
		12:00 PM 2021/12/28, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.base_components;

import static org.zkoss.zephyr.action.ActionTarget.SELF;
import static org.zkoss.zephyr.action.ActionType.onSwipe;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.zkoss.zephyr.action.data.AfterSizeData;
import org.zkoss.zephyr.action.data.DropData;
import org.zkoss.zephyr.action.data.KeyData;
import org.zkoss.zephyr.action.data.MouseData;
import org.zkoss.zephyr.action.data.SwipeData;
import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.ActionVariable;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.Self;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.ui.UiAgentCtrl;
import org.zkoss.zephyr.util.ActionHandler;
import org.zkoss.zephyr.zpr.IAnyGroup;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyr.zpr.IDiv;
import org.zkoss.zephyr.zpr.ILabel;
import org.zkoss.zephyr.zpr.ITextbox;
import org.zkoss.zephyr.zpr.IVlayout;
import org.zkoss.zephyr.zpr.IWindow;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;

/**
 * A set of examples for {@link org.zkoss.zephyr.zpr.IHtmlBasedComponent} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Base_Components/HtmlBasedComponent">IHtmlBasedComponent</a>,
 * if any.
 * @author jumperchen
 * @see org.zkoss.zephyr.zpr.IHtmlBasedComponent
 */
@RichletMapping("/base_components/ihtmlbasedcomponent")
public class IHtmlBasedComponentRichlet implements StatelessRichlet {

	@RichletMapping("/actions/click")
	public IComponent clickAction() {
		return IVlayout.of(ILabel.ofId("msg"),
				IButton.of("Click").withAction(this::doClick));
	}

	@Action(type = Events.ON_CLICK)
	public void doClick(UiAgent uiAgent, MouseData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/doubleClick")
	public IComponent doubleClickAction() {
		return IVlayout.of(ILabel.ofId("msg"),
				IButton.of("Double Click").withAction(this::doDblClick));
	}

	@Action(type = Events.ON_DOUBLE_CLICK)
	public void doDblClick(UiAgent uiAgent, MouseData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/rightClick")
	public IComponent righteClickAction() {
		return IVlayout.of(ILabel.ofId("msg"),
				IButton.of("Right Click").withAction(this::doRightClick));
	}

	@Action(type = Events.ON_RIGHT_CLICK)
	public void doRightClick(UiAgent uiAgent, MouseData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/mouseOver")
	public IComponent mouseOverAction() {
		return IVlayout.of(ILabel.ofId("msg"),
				IButton.of("Mouse Over").withAction(this::doMouseOver));
	}

	@Action(type = Events.ON_MOUSE_OVER)
	public void doMouseOver(UiAgent uiAgent, MouseData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/mouseOut")
	public IComponent mouseOutAction() {
		return IVlayout.of(ILabel.ofId("msg"),
				IButton.of("Mouse Out").withAction(this::doMouseOut));
	}

	@Action(type = Events.ON_MOUSE_OUT)
	public void doMouseOut(UiAgent uiAgent, MouseData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/key")
	public IComponent keyAction() {
		return IVlayout.of(ILabel.ofId("msg"), ITextbox.ofId("inp")
				.withActions(ActionHandler.of(this::doOK),
						ActionHandler.of(this::doCancel)));
	}

	@Action(type = Events.ON_OK)
	public void doOK(UiAgent uiAgent, KeyData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@Action(type = Events.ON_CANCEL)
	public void doCancel(UiAgent uiAgent, KeyData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/afterSize")
	public IComponent afterSizeAction() {
		return IVlayout.of(ILabel.ofId("msg"), IWindow.ofSize("300px", "300px")
				.withAction(this::doAfterSize));
	}

	@Action(type = Events.ON_AFTER_SIZE)
	public void doAfterSize(UiAgent uiAgent, AfterSizeData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	@RichletMapping("/actions/swipe")
	public IComponent swipeAction() {
		return IVlayout.of(ILabel.ofId("msg"), IWindow.ofSize("300px", "300px")
				.withAction(onSwipe(this::doSwipe)));
	}

	public void doSwipe(UiAgent uiAgent, SwipeData data) {
		uiAgent.replaceWith(Locator.ofId("msg"), ILabel.ofId("msg").withValue(data.toString()));
	}

	private String[][] positions = {{"0px", "0px"}, {"90px", "0px"}, {"0px", "90px"}, {"90px", "90px"}};

	@RichletMapping("/actions/drop")
	public IComponent dropAction() {
		final IDiv<IAnyGroup> template = IDiv.of().withStyle("position:absolute;")
				.withDraggable("true").withDroppable("true")
				.withAction(this::doExchange);

		List<IDiv> objects = IntStream.range(0, 4)
				.mapToObj(i -> template.withLeft(positions[i][0])
								.withTop(positions[i][1])
								.withChildren(ILabel.of("div " + i)))
				.collect(Collectors.toList());
		return IDiv.ofId("container").withChildren(objects);
	}

	@Action(type = Events.ON_DROP)
	public void doExchange(Self dropped, DropData data) {
		int droppedIndex = data.getDroppedIndex();
		int draggedIndex = data.getDraggedIndex();

		// update the position of the dropped component
		UiAgentCtrl.smartUpdate(dropped, "left", positions[draggedIndex][0]);
		UiAgentCtrl.smartUpdate(dropped, "top",  positions[draggedIndex][1]);

		// update the position of the dragged component
		UiAgentCtrl.smartUpdate(data.getDragged(), "left", positions[droppedIndex][0]);
		UiAgentCtrl.smartUpdate(data.getDragged(), "top", positions[droppedIndex][1]);

		// swap the position data
		String[] position = positions[draggedIndex];
		positions[draggedIndex] = positions[droppedIndex];
		positions[droppedIndex] = position;
	}

	@RichletMapping("/sclass")
	public IComponent sclass() {
		return IDiv.of().withSclass("sclass1 sclass2");
	}

	@RichletMapping("/zclass")
	public IComponent zclass() {
		return IWindow.of().withZclass("mywin");
	}

	@RichletMapping("/flex")
	public IComponent flex() {
		return IDiv.ofSize("300px", "300px").withChildren(IDiv.ofId("main").withVflex("1").withHflex("1"));
	}

	@RichletMapping("/flexMin")
	public IComponent flexMin() {
		return IWindow.ofSize("300px", "300px")
				.withChildren(IDiv.ofId("main").withVflex("min").withHflex("min").withChildren(ILabel.of("test")));
	}

	@RichletMapping("/flexAndSize")
	public IComponent flexAndSize() {
		return IDiv.of(IButton.of("Click me and show error1")
				.withAction(this::doWidthAndHflexError),
				IButton.of("Click me and show error2")
				.withAction(this::doHeightAndVflexError));
	}

	@Action(type = Events.ON_CLICK)
	public void doWidthAndHflexError() {
		IWindow.ofSize("300px", "300px").withHflex("1");
	}

	@Action(type = Events.ON_CLICK)
	public void doHeightAndVflexError() {
		IWindow.ofSize("300px", "300px").withVflex("1");
	}

	@RichletMapping("/clientAction")
	public IComponent clientAction() {
		return IDiv.of(ILabel.ofId("msg")
						.withClientAction("show: slideDown({duration:3000})")
						.withValue("Show me").withVisible(false),
				IButton.of("show msg").withAction(this::doClientAction));
	}

	@Action(type = Events.ON_CLICK)
	public void doClientAction() {
		UiAgentCtrl.smartUpdate(Locator.ofId("msg"), "visible", true);
	}

	@RichletMapping("/tabindex")
	public IComponent tabindex() {
		return IDiv.of(
				ITextbox.of("A1"),
				ITextbox.of("B1").withTabindex(3),
				ITextbox.of("C1").withTabindex(0)
		);
	}

	@RichletMapping("/renderdefer")
	public IComponent renderdefer() {
		return ILabel.of("Test render defer").withRenderdefer(2000);
	}

	@RichletMapping("/tooltiptext")
	public IComponent tooltiptext() {
		return ILabel.of("Test tooltip").withTooltiptext("tooltip");
	}

	@RichletMapping("/zIndex")
	public IComponent zIndex() {
		return IDiv.of(
				IDiv.of().withZIndex(0).withStyle("position: absolute; width: 300px; height: 300px;border: 1px solid red;").withAction(this::doZIndex),
				IDiv.of().withZIndex(550).withStyle("position: absolute; width: 300px; height: 300px;border: 1px solid blue;").withAction(this::doZIndex)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void doZIndex(@ActionVariable(targetId = SELF, field = "style") String style) {
		Clients.log(style);
	}
}
