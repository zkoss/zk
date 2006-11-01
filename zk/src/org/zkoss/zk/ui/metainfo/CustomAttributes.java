/* CustomAttributes.java

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
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.util.Condition;

/**
 * Represents a map of custom attributes of an instance definition
 * ({@link InstanceDefinition}).
 * It is equivalent to the custom-attributes element.
 *
 * @author tomyeh
 */
public class CustomAttributes implements Condition, java.io.Serializable {
    private static final long serialVersionUID = 20060622L;

	private final Map _attrs;
	private final Condition _cond;
	private final int _scope;

	/**
	 * @param attrs the custom attributes (String name, String value).
	 * Once called, the caller shall not access attrs again -- it belongs
	 * to this object.
	 */
	public CustomAttributes(Map attrs, String scope, Condition cond) {
		if (attrs == null)
			throw new IllegalArgumentException("null");
		_scope = scope == null ?
			Component.COMPONENT_SCOPE: Components.getScope(scope);
		_attrs = attrs;
		_cond = cond;
	}

	/** Applies the custom attributes.
	 * <p>Note: this method does nothing if {@link #isEffective} returns false.
	 */
	public void apply(Component comp) {
		if (isEffective(comp))
			for (Iterator it = _attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				comp.setAttribute((String)me.getKey(),
					Executions.evaluate(comp, (String)me.getValue(), Object.class),
					_scope);
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
		final StringBuffer sb = new StringBuffer(40).append("[Custom Attrs:");
		for (Iterator it = _attrs.keySet().iterator(); it.hasNext();)
			sb.append(' ').append(it.next());
		return sb.append(']').toString();
	}
}
