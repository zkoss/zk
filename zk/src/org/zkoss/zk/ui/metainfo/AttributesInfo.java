/* AttributesInfo.java

	Purpose:
		
	Description:
		
	History:
		Thu Apr 13 09:06:16     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;

import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.ConditionImpl;
import org.zkoss.zk.xel.Evaluator;
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
public class AttributesInfo extends ConditionLeafInfo {
	/** Map(String name, ExValue/ExValue[]/Map value). */
	private final Map<String, Object> _attrs;
	private final int _composite;
	private final int _scope;

	/** Constructor.
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 * @param scope specifies the scope.
	 * @param composite indicates the composite type.
	 * It can be one of "none", "list" or "map".
	 * If null or empty, "none" is assumed.
	 * @exception IllegalArgumentException if the composite type is illegal.
	 * @since 3.0.6
	 */
	public AttributesInfo(NodeInfo parent,
	Map<String, String> attrs, String scope, String composite, ConditionImpl cond) {
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

		
		if (attrs != null && !attrs.isEmpty()) {
			_attrs = new LinkedHashMap<String, Object>();
			for (Map.Entry<String, String> me: attrs.entrySet()) {
				_attrs.put(me.getKey(),
					Utils.parseComposite(me.getValue(), Object.class, _composite));
			}
		} else {
			_attrs = null;
		}

		_scope = scope == null ? -1: Components.getScope(scope);
	}
	/** The same as AttributesInfo(parent, attrs, scope, "none", cond).
	 *
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 * @param scope specifies the scope.
	 */
	public AttributesInfo(NodeInfo parent,
	Map<String, String> attrs, String scope, ConditionImpl cond) {
		this(parent, attrs, scope, null, cond);
	}

	/** Returns the scope, or null if it is not assoicated with a scope.
	 * <p>Notice that, prior to 5.0.8, "component" is returned if
	 * it is not associated with a scope (which is not correct since
	 * this info might be associated with a page).
	 * @since 3.0.6
	 */
	public String getScope() {
		return _scope != -1 ? Components.scopeToString(_scope): null;
	}
	/** Returns the composite type: "none", "list" or "map".
	 * @since 3.0.6
	 */
	public String getComposite() {
		return _composite == Utils.LIST ? "list":
			_composite == Utils.MAP ? "map": "none";
	}

	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Component comp) {
		if (_attrs != null && isEffective(comp)) {
			final Evaluator eval = getEvaluator();
			for (Map.Entry<String, Object> me: _attrs.entrySet()) {
				final String name = me.getKey();
				final Object value = me.getValue();
				comp.setAttribute(
					name, Utils.evaluateComposite(eval, comp, value),
					_scope != -1 ? _scope: Component.COMPONENT_SCOPE);
			}
		}
	}
	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Page page) {
		if (_attrs != null && isEffective(page)) {
			final Evaluator eval = getEvaluator();
			for (Map.Entry<String, Object> me: _attrs.entrySet()) {
				final String name = me.getKey();
				final Object value = me.getValue();
				page.setAttribute(name,
					Utils.evaluateComposite(eval, page, value),
					_scope != -1 ? _scope: Component.PAGE_SCOPE);
			}
		}
	}

	//Object//
	public String toString() {
		final StringBuffer sb = new StringBuffer(40).append("[custom-attributes:");
		if (_attrs != null)
			for (String name: _attrs.keySet())
				sb.append(' ').append(name);
		return sb.append(']').toString();
	}
}
