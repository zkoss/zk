/* WidgetMethod.java

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 09:59:52     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a method of the peer widget.
 * Unlike {@link WidgetListener}, {@link #getScript} must be a complete
 * function declaration:
 * <code>function (arg..) {...}</code>
 *
 * @author tomyeh
 * @since 5.0.0
 * @see WidgetListener
 */
public class WidgetMethod extends WidgetListener {
	/**
	 * @param name the the method name, such as setValue.
	 * @param evalr the evaluator reference. It is required if cond is not null.
	 * @exception IllegalArgumentException if script is null
	 * or (cond is not null but evalr is null)
	 */
	public WidgetMethod(EvaluatorRef evalr, String name, String script, ConditionImpl cond) {
		super(evalr, name, script, cond);
	}

	public void assign(Component comp) {
		if (isEffective(comp))
			comp.setWidgetMethod(_name, _script);
	}
}
