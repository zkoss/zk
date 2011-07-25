/* WidgetAttribute.java

	Purpose:
		
	Description:
		
	History:
		Thu Jun 10 11:36:07 TST 2010, Created by tomyeh

Copyright (C) 2010 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.ui.metainfo;

import org.zkoss.zk.xel.ExValue;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.EvaluatorRef;

/**
 * Represents a DOM attribute of the peer widget.
 *
 * @author tomyeh
 * @since 5.0.3
 * @see WidgetListener
 */
public class WidgetAttribute extends ConditionValue {
	private static final long serialVersionUID = 20081213L;

	protected final String _name;
	protected final ExValue _value;

	/**
	 * @param name the event name, such as onClick
	 * @param value the value. EL is allowed and it will be coerced to String
	 * @exception IllegalArgumentException if value is null
	 */
	public WidgetAttribute(EvaluatorRef evalr, String name, String value, ConditionImpl cond) {
		super(evalr, cond);

		if (name == null || value == null)
			throw new IllegalArgumentException();
		_name = name;
		_value = value != null ? new ExValue(value, String.class): null;
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
}
