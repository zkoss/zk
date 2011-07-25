/* ConditionValue.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  7 11:40:48 TST 2011, Created by tomyeh

Copyright (C) 2011 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Represents a value that supports {@link Condition}.
 * It is usually used as an attribute of a {@link NodeInfo}.
 * @author tomyeh
 * @since 5.1.0
 */
/*package*/ abstract class ConditionValue implements Condition, java.io.Serializable {
	/*pacakge*/ EvaluatorRef _evalr;
	/*package*/ ConditionImpl _cond;

	/**
	 * @throws IllegalArgumentException if evalr is null but cond is not.
	 */
	/*package*/ ConditionValue(EvaluatorRef evalr, ConditionImpl cond) {
		if (evalr == null && cond != null)
			throw new IllegalArgumentException();
		_evalr = evalr;
		_cond = cond;
	}

	/** Returns the evaluator reference.
	 */
	public EvaluatorRef getEvaluatorRef() {
		return _evalr;
	}
	/** Returns the effectiveness condition.
	 */
	public ConditionImpl getCondition() {
		return _cond;
	}
	/** Sets the effectiveness condition.
	 */
	public void setCondition(ConditionImpl cond) {
		_cond = cond;
	}
	/** Tests if the condition is set
	 */
	public boolean withCondition() {
		return _cond != null;
	}

	//Condition//
	//@Override
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	//@Override
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}
}
