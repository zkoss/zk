/* WidgetListener.java

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 09:59:52     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.ExValue;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Represents a client-side event listener for the peer widget.
 * Notice that, unlike {@link WidgetOverride}, {@link #getScript} has only
 * the function's body (without <code>function (event)</code>).
 *
 * @author tomyeh
 * @since 5.0.0
 * @see EventHandler
 * @see WidgetOverride
 */
public class WidgetListener extends ConditionValue {
	private static final long serialVersionUID = 20081213L;

	protected final String _name;
	protected final ExValue _script;

	/**
	 * @param name the event name, such as onClick
	 * @param script the script snippet. EL is allowed.
	 * @exception IllegalArgumentException if name or script is null
	 */
	public WidgetListener(EvaluatorRef evalr, String name, String script, ConditionImpl cond) {
		super(evalr, cond);

		if (name == null || script == null)
			throw new IllegalArgumentException();
		_name = name;
		_script = script != null ? new ExValue(script, String.class): null;
	}

	/** Returns the event name, such as, onClick.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the script associated with this event handler.
	 * @since 5.0.2
	 */
	public String getScript(Component comp) {
		return _script != null ? (String)_script.getValue(_evalr, comp): null;
	}
	/** Returns the script passed to the constructor.
	 * In other words, it might contains EL. 
	 * @since 5.0.2
	 */
	public String getRawScript() {
		return _script != null ? _script.getRawValue(): null;
	}

	public void assign(Component comp) {
		if (isEffective(comp))
			comp.setWidgetListener(_name, getScript(comp));
	}
}
