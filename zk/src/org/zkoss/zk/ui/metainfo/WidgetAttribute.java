/* WidgetAttribute.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 10 11:36:07 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.ExValue;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.impl.EvaluatorRef;

/**
 * Represents a DOM attribute of the peer widget.
 *
 * @author tomyeh
 * @since 5.0.3
 * @see WidgetListener
 */
public class WidgetAttribute extends EvalRefStub
implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20081213L;

	protected final String _name;
	protected final ExValue _value;
	protected final ConditionImpl _cond;

	/**
	 * @param name the event name, such as onClick
	 * @param evalr the evaluator reference. It is required if cond is not null.
	 * @param value the value. EL is allowed and it will be coerced to String
	 * @exception IllegalArgumentException if value is null
	 * or (cond is not null but evalr is null)
	 */
	public WidgetAttribute(EvaluatorRef evalr, String name, String value, ConditionImpl cond) {
		if (name == null || value == null || (evalr == null && cond != null))
			throw new IllegalArgumentException();
		_evalr = evalr;
		_name = name;
		_value = value != null ? new ExValue(value, String.class): null;
		_cond = cond;
	}

	/** Returns the event name, such as, onClick.
	 */
	public String getName() {
		return _name;
	}
	/** Returns the value associated with this event handler.
	 */
	public String getValue(Component comp) {
		return _value != null ? (String)_value.getValue(_evalr, comp): null;
	}
	/** Returns the value passed to the constructor.
	 * In other words, it might contains EL. 
	 */
	public String getRawValue() {
		return _value != null ? _value.getRawValue(): null;
	}

	public void assign(Component comp) {
		if (isEffective(comp))
			comp.setWidgetAttribute(_name, getValue(comp));
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
