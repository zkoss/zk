/* AttributesInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 13 09:06:16     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.impl.EvaluatorRef;
import org.zkoss.zk.xel.impl.Utils;

/**
 * Represents a map of custom attributes of a component definition
 * ({@link ComponentDefinition}).
 * It is equivalent to the custom-attributes element.
 *
 * <p>Note: it is serializable.
 *
 * @author tomyeh
 */
public class AttributesInfo extends EvalRefStub
implements Condition, java.io.Serializable {
	/** Map(String name, ExValue[] value). */
	private final Map _attrs;
	private final ConditionImpl _cond;
	private final int _scope;

	/**
	 * @param evalr the evaluator reference. It cannot be null.
	 * Retrieve it from {@link LanguageDefinition#getEvaluatorRef}
	 * or {@link PageDefinition#getEvaluatorRef}, depending which it
	 * belongs.
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 */
	public AttributesInfo(EvaluatorRef evalr,
	Map attrs, String scope, ConditionImpl cond) {
		if (evalr == null) throw new IllegalArgumentException();
		_evalr = evalr;
		_attrs = attrs;
		if (_attrs != null) {
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				me.setValue(
					Utils.parseBracketList((String)me.getValue(), Object.class));
			}
		}

		_scope = scope == null ?
			Component.COMPONENT_SCOPE: Components.getScope(scope);
		_cond = cond;
	}

	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Component comp) {
		if (_attrs != null && isEffective(comp)) {
			final Evaluator eval = _evalr.getEvaluator();
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final ExValue[] value = (ExValue[])me.getValue();
				comp.setAttribute(
					name, Utils.evaluate(eval, comp, value), _scope);
			}
		}
	}
	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Page page) {
		if (_attrs != null && isEffective(page)) {
			final Evaluator eval = _evalr.getEvaluator();
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final String name = (String)me.getKey();
				final ExValue[] value = (ExValue[])me.getValue();
				page.setAttribute(name,
					Utils.evaluate(eval, page, value), _scope);
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
		final StringBuffer sb = new StringBuffer(40).append("[custom-attributes:");
		if (_attrs != null)
			for (Iterator it = _attrs.keySet().iterator(); it.hasNext();)
				sb.append(' ').append(it.next());
		return sb.append(']').toString();
	}
}
