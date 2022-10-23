/* IColorboxRichlet.java

	Purpose:

	Description:

	History:
		Tue Mar 01 18:16:03 CST 2022, Created by katherine

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.test.docs.input;

import java.util.Arrays;
import java.util.List;

import org.zkoss.zephyr.annotation.Action;
import org.zkoss.zephyr.annotation.RichletMapping;
import org.zkoss.zephyr.ui.Locator;
import org.zkoss.zephyr.ui.StatelessRichlet;
import org.zkoss.zephyr.ui.UiAgent;
import org.zkoss.zephyr.zpr.IButton;
import org.zkoss.zephyr.zpr.IComponent;
import org.zkoss.zephyrex.zpr.IColorbox;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link org.zkoss.zephyrex.zpr.IColorbox} Java Docs.
 * And also refers to something else on <a href="https://www.zkoss.org/wiki/ZK_Component_Reference/Input/Colorbox">IColorbox</a>,
 * if any.
 *
 * @author katherine
 * @see org.zkoss.zephyrex.zpr.IColorbox
 */
@RichletMapping("/input/iColorbox")
public class IColorboxRichlet implements StatelessRichlet {

	@RichletMapping("/color")
	public List<IComponent> color() {
		return Arrays.asList(
				IButton.of("change color").withAction(this::changeColor),
				IColorbox.of("#FFFFFF").withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeColor() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IColorbox.Updater().color("#AAAADC"));
	}

	@RichletMapping("/disabled")
	public List<IComponent> disabled() {
		return Arrays.asList(
				IButton.of("change color").withAction(this::changeDisabled),
				IColorbox.DEFAULT.withDisabled(true).withId("box")
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeDisabled() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("box"), new IColorbox.Updater().disabled(false));
	}

	@RichletMapping("/value")
	public IComponent value() {
		return IColorbox.DEFAULT.withValue("#FFFF00");
	}
}