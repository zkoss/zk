/* ITbeditorRichlet.java

	Purpose:

	Description:

	History:
		11:41 AM 2022/2/24, Created by jumperchen

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
import org.zkoss.statelessex.sul.ITbeditor;
import org.zkoss.zk.ui.event.Events;

/**
 * A set of example for {@link ITbeditor} Java Docs.
 * And also refers to something else on <a href="www.zkoss.org/wiki/ZK_Component_Reference/Input/Tbeditor">ITbeditor</a>,
 * if any.
 * @author jumperchen
 * @see ITbeditor
 */
@RichletMapping("/input/itbeditor")
public class ITbeditorRichlet implements StatelessRichlet {

	@RichletMapping("/example")
	public IComponent example() {
		return ITbeditor.of("this is a demo for <b>trumbowy</b> editor!!");
	}
	@RichletMapping("/customizedProperties")
	public IComponent customizedProperties() {
		Map config = new HashMap();
		config.put("btns", new String[] {"bold", "italic", "link"});
		return ITbeditor.of("this is a demo for <b>trumbowy</b> editor!!").withConfig(config);
	}

	@RichletMapping("/value")
	public List<IComponent> value() {
		return Arrays.asList(
				ITbeditor.ofId("ds").withValue("testing"),
				IButton.of("change config").withAction(this::changeValue)
		);
	}

	@Action(type = Events.ON_CLICK)
	public void changeValue() {
		UiAgent.getCurrent().smartUpdate(Locator.ofId("ds"), new ITbeditor.Updater().value("test"));
	}
}
