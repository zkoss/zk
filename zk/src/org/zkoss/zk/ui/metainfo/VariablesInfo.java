/* VariablesInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb 28 19:19:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;

/**
 * The information about the variables element in the ZUML page.
 * 
 * @author tomyeh
 */
public class VariablesInfo implements Condition, java.io.Serializable {
	private final Map _vars;
	private final Condition _cond;
	private final boolean _local;

	public VariablesInfo(Map vars, boolean local, Condition cond) {
		if (vars == null)
			throw new IllegalArgumentException("null vars");
		_vars = vars;
		_local = local;
		_cond = cond;
	}

	/** Applies the variable element against the parent component.
	 *
	 * @param comp the parent component (it cannot be null)
	 */
	public void apply(Component comp) {
		if (isEffective(comp))
			for (Iterator it = _vars.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey(),
					value = (String)me.getValue();
				comp.setVariable(name,
					Executions.evaluate(comp, value, Object.class),
					_local);
			}
	}
	/** Applies the variable element against the page.
	 * It is called if the element doesn't belong to any component.
	 */
	public void apply(Page page) {
		if (isEffective(page))
			for (Iterator it = _vars.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey(),
					value = (String)me.getValue();
				page.setVariable(name,
					Executions.evaluate(page, value, Object.class));
			}
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(page);
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[variables:");
		for (Iterator it = _vars.keySet().iterator(); it.hasNext();)
			sb.append(' ').append(it.next());
		return sb.append(']').toString();
	}
}
