/* WidgetListener.java

	Purpose:
		
	Description:
		
	History:
		Sat Dec 13 09:59:52     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.impl.EvaluatorRef;

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
public class WidgetListener extends EvalRefStub
implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20081213L;

	protected final String _name;
	protected final String _script;
	protected final ConditionImpl _cond;

	/**
	 * @param name the event name, such as onClick
	 * @param evalr the evaluator reference. It is required if cond is not null.
	 * @exception IllegalArgumentException if script is null
	 * or (cond is not null but evalr is null)
	 */
	public WidgetListener(EvaluatorRef evalr, String name, String script, ConditionImpl cond) {
		if (name == null || script == null || (evalr == null && cond != null))
			throw new IllegalArgumentException();
		_evalr = evalr;
		_name = name;
		_script = script;
		_cond = cond;
	}

	/** Returns the event name, such as, onClick.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the script associated with this event handler.
	 */
	public String getScript() {
		return _script;
	}

	public void assign(Component comp) {
		if (isEffective(comp))
			comp.setWidgetListener(_name, _script);
	}

	/** Returns the evaluator reference, or null if not available.
	 */
	/*package*/ EvaluatorRef getEvaluatorRef() {
		return _evalr;
	}
	/** Returns the condition, or null if not available (i.e., always
	 * effective).
	 */
	/*package*/ ConditionImpl getCondition() {
		return _cond;
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(getEvaluatorRef(), comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(getEvaluatorRef(), page);
	}
}
