/* EventHandler.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Mon Jun 19 15:17:41     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * An event handler of a component definition ({@link ComponentDefinition}).
 *
 * @author tomyeh
 */
public class EventHandler extends EvalRefStub
implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

	private final ZScript _zscript;
	private final ConditionImpl _cond;

	public EventHandler(ZScript zscript, ConditionImpl cond) {
		if (zscript == null)
			throw new IllegalArgumentException();
		if (cond != null && zscript.getEvaluatorRef() == null)
			throw new IllegalArgumentException("evalr is required");

		_evalr = null; //to save the size of serialized, _zscript.getEvaluatorRef is retrieved on demand
		_zscript = zscript;
		_cond = cond;
	}
	/**
	 * @param evalr the evaluator reference. It is required if cond is not null.
	 * @since 3.0.0
	 */
	public EventHandler(EvaluatorRef evalr, ZScript zscript, ConditionImpl cond) {
		_evalr = evalr;
		_zscript = zscript;
		_cond = cond;
	}

	/** Returns the zscript associated with this event handler.
	 */
	public ZScript getZScript() {
		return _zscript;
	}

	/** Returns the evaluator reference, or null if not available.
	 */
	/*package*/ EvaluatorRef getEvaluatorRef() {
		return _evalr != null ? _evalr: _zscript.getEvaluatorRef();
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
