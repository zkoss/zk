/* VariablesInfo.java

	Purpose:
		
	Description:
		
	History:
		Wed Feb 28 19:19:49     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.Utils;

/**
 * The information about the variables element in the ZUML page.
 * 
 * @author tomyeh
 */
public class VariablesInfo extends ConditionLeafInfo {
	/** Map(String name, ExValue/ExValue[]/Map value). */
	private final Map<String, Object> _vars;
	private final int _composite;
	private final boolean _local;

	/** Constructor.
	 * @param vars a map of (String name, String value).
	 * Note: once called, the caller cannot access it any more.
	 * In other words, it becomes part of this object.
	 * @param local whether they are local variables.
	 * @param composite indicates the composite type.
	 * It can be one of "none", "list" or "map".
	 * If null or empty, "none" is assumed.
	 * @exception IllegalArgumentException if the composite type is illegal.
	 * @since 3.0.6
	 */
	public VariablesInfo(NodeInfo parent, Map<String, String> vars, boolean local,
	String composite, ConditionImpl cond) {
		super(parent, cond);

		if (composite == null || composite.length() == 0
		|| composite.equals("none"))
			_composite = Utils.SCALAR;
		else if (composite.equals("list"))
			_composite = Utils.LIST;
		else if (composite.equals("map"))
			_composite = Utils.MAP;
		else
			throw new IllegalArgumentException("Unkonwn composite: "+composite);

		if (vars != null) {
			_vars = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, String> me: vars.entrySet()) {
				_vars.put(me.getKey(),
					Utils.parseComposite(me.getValue(), Object.class, _composite));
			}
		} else {
			_vars = null;
		}

		_local = local;
	}
	/** The same as VariablesInfo(parent, vars, locale, "none", cond).
	 * @param vars a map of (String name, String value).
	 * Note: once called, the caller cannot access it any more.
	 * In other words, it becomes part of this object.
	 * @param local whether they are local variables.
	 */
	public VariablesInfo(NodeInfo parent, Map<String, String> vars, boolean local,
	ConditionImpl cond) {
		this(parent, vars, local, null, cond);
	}

	/** Returns if it is for local variable.
	 * @since 3.0.6
	 */
	public boolean isLocal() {
		return _local;
	}
	/** Returns the composite type: "none", "list" or "map".
	 * @since 3.0.6
	 */
	public String getComposite() {
		return _composite == Utils.LIST ? "list":
			_composite == Utils.MAP ? "map": "none";
	}

	/** Applies the variable element against the parent component.
	 *
	 * @param comp the parent component (it cannot be null)
	 */
	public void apply(Component comp) {
		if (_vars != null && isEffective(comp)) {
			final Evaluator eval = getEvaluator();
			for (Map.Entry<String, Object> me: _vars.entrySet()) {
				final String name = me.getKey();
				final Object value = me.getValue();
				comp.getSpaceOwner().setAttribute(
					name, Utils.evaluateComposite(eval, comp, value), !_local);
			}
		}
	}
	/** Applies the variable element against the page.
	 * It is called if the element doesn't belong to any component.
	 */
	public void apply(Page page) {
		if (_vars != null && isEffective(page)) {
			final Evaluator eval = getEvaluator();
			for (Map.Entry<String, Object> me: _vars.entrySet()) {
				final String name = me.getKey();
				final Object value = me.getValue();
				page.setAttribute(
					name, Utils.evaluateComposite(eval, page, value), !_local);
			}
		}
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[variables:");
		if (_vars != null)
			for (String name: _vars.keySet())
				sb.append(' ').append(name);
		return sb.append(']').toString();
	}
}
