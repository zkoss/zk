/* VariablesInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Feb 28 19:19:49     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.impl.EvaluatorRef;
import org.zkoss.zk.xel.impl.Utils;

/**
 * The information about the variables element in the ZUML page.
 * 
 * @author tomyeh
 */
public class VariablesInfo extends EvalRefStub
implements Condition, java.io.Serializable {
	/** Map(String name, ExValue/ExValue[]/Map value). */
	private final Map _vars;
	private final ConditionImpl _cond;
	private final int _composite;
	private final boolean _local;

	/** Constructor.
	 * @param evalr the evaluator reference. It cannot be null.
	 * Retrieve it from {@link LanguageDefinition#getEvaluatorRef}
	 * or {@link PageDefinition#getEvaluatorRef}, depending which it
	 * belongs.
	 * @param vars a map of (String name, String value).
	 * Note: once called, the caller cannot access it any more.
	 * In other words, it becomes part of this object.
	 * @param local whether they are local variables.
	 * @param composite indicates the composite type.
	 * It can be one of "none", "list" or "map".
	 * If null or empty, "none" is assumed.
	 * @exception IllegalArgumentException if evalr is null,
	 * or the composite type is illegal.
	 * @since 3.0.6
	 */
	public VariablesInfo(EvaluatorRef evalr, Map vars, boolean local,
	String composite, ConditionImpl cond) {
		if (evalr == null) throw new IllegalArgumentException();
		if (composite == null || composite.length() == 0
		|| composite.equals("none"))
			_composite = Utils.SCALAR;
		else if (composite.equals("list"))
			_composite = Utils.LIST;
		else if (composite.equals("map"))
			_composite = Utils.MAP;
		else
			throw new IllegalArgumentException("Unkonwn composite: "+composite);

		_evalr = evalr;
		_vars = vars;
		if (_vars != null) {
			for (Iterator it = _vars.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(
					Utils.parseComposite(
						(String)me.getValue(), Object.class, _composite));
			}
		}

		_local = local;
		_cond = cond;
	}
	/** The same as VariablesInfo(evalr, vars, locale, "none", cond).
	 * @param evalr the evaluator reference. It cannot be null.
	 * Retrieve it from {@link LanguageDefinition#getEvaluatorRef}
	 * or {@link PageDefinition#getEvaluatorRef}, depending which it
	 * belongs.
	 * @param vars a map of (String name, String value).
	 * Note: once called, the caller cannot access it any more.
	 * In other words, it becomes part of this object.
	 * @param local whether they are local variables.
	 * @exception IllegalArgumentException if evalr is null
	 */
	public VariablesInfo(EvaluatorRef evalr, Map vars, boolean local,
	ConditionImpl cond) {
		this(evalr, vars, local, null, cond);
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
			final Evaluator eval = _evalr.getEvaluator();
			for (Iterator it = _vars.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object value = me.getValue();
				comp.setVariable(
					name, Utils.evaluateComposite(eval, comp, value), _local);
			}
		}
	}
	/** Applies the variable element against the page.
	 * It is called if the element doesn't belong to any component.
	 */
	public void apply(Page page) {
		if (_vars != null && isEffective(page)) {
			final Evaluator eval = _evalr.getEvaluator();
			for (Iterator it = _vars.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final Object value = me.getValue();
				page.setVariable(
					name, Utils.evaluateComposite(eval, page, value));
			}
		}
	}

	//Condition//
	public boolean isEffective(Component comp) {
		return _cond == null || _cond.isEffective(_evalr, comp);
	}
	public boolean isEffective(Page page) {
		return _cond == null || _cond.isEffective(_evalr, page);
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[variables:");
		if (_vars != null)
			for (Iterator it = _vars.keySet().iterator(); it.hasNext();)
				sb.append(' ').append(it.next());
		return sb.append(']').toString();
	}
}
