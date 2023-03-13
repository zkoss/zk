/* Issue5411Richlet.java

	Purpose:
		
	Description:
		
	History:
		3:08 PM 2023/3/13, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.stateless.test.issues;

import java.util.Arrays;
import java.util.List;

import org.zkoss.stateless.action.ActionTarget;
import org.zkoss.stateless.annotation.Action;
import org.zkoss.stateless.annotation.ActionVariable;
import org.zkoss.stateless.annotation.RichletMapping;
import org.zkoss.stateless.sul.IComponent;
import org.zkoss.stateless.sul.ITextbox;
import org.zkoss.stateless.ui.StatelessRichlet;
import org.zkoss.zk.ui.event.Events;

/**
 * @author jumperchen
 */
@RichletMapping("/stateless/issue5411")
public class Issue5411Richlet implements StatelessRichlet {

	@RichletMapping("")
	public List<IComponent> index() {
		//Creating IComponents using the IComponent.of pattern
		return Arrays.asList(
				ITextbox.ofId("tbUserId").withAction(this::inputUpdate)
		);
	}

	@Action(type = Events.ON_CHANGE)
	public void inputUpdate(@ActionVariable(targetId = ActionTarget.SELF, field = "value") String newValue, @ActionVariable(targetId = ActionTarget.SELF, field = "id") String id) {
	}
}
