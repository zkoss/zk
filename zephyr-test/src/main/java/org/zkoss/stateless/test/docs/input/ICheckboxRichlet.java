/* ICheckboxRichlet.java

	Purpose:

	Description:

	History:
		2:50 PM 2022/2/22, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.docs.input;

import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.ui.Locator;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.stateless.ui.UiAgent;
import org.zkoss.stateless.sul.IButton;
import org.zkoss.stateless.sul.ICheckbox;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.IHlayout;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ICheckbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Essential_Components/Input/Checkbox">ICheckbox</a>,
 * if any.
 * @author jumperchen
 * @see ICheckbox
 */
@RichletMapping("/input/icheckbox")
public class ICheckboxRichlet implements StatelessRichlet {
	@RichletMapping("/mold/default")
	public IComponent defaultMold() {
		return IHlayout.of(
				ICheckbox.DEFAULT,
				ICheckbox.DEFAULT.withChecked(true)
		);
	}

	@RichletMapping("/mold/switch")
	public IComponent switchMold() {
		return IHlayout.of(
				ICheckbox.SWITCH,
				ICheckbox.SWITCH.withChecked(true)
		);
	}

	@RichletMapping("/mold/toggle")
	public IComponent toggleMold() {
		return IHlayout.of(
				ICheckbox.TOGGLE,
				ICheckbox.TOGGLE.withChecked(true)
		);
	}

	@RichletMapping("/mold/tristate")
	public IComponent tristateMold() {
		return IHlayout.of(
				ICheckbox.TRISTATE,
				ICheckbox.TRISTATE.withIndeterminate(true),
				ICheckbox.TRISTATE.withChecked(true)
		);
	}

	@RichletMapping("/image")
	public IComponent image() {
		return IHlayout.of(
				IButton.of("change image").withAction(this::changeImage),
				ICheckbox.ofImage("/stateless/ZK-Logo.gif").withId("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeImage() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"),
				new ICheckbox.Updater().image("/zephyr-test/stateless/ZK-Logo-old.gif"));
	}

	@RichletMapping("/disabled")
	public IComponent disabled() {
		return IHlayout.of(
				IButton.of("change disabled").withAction(this::changeDisabled),
				ICheckbox.DEFAULT.withDisabled(true).withId("cb")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("cb"), new ICheckbox.Updater().disabled(false));
	}
}
