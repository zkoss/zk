/* CustomAttributes.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Apr 13 09:06:16     2006, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.potix.zk.ui.Page;
import com.potix.zk.ui.Component;
import com.potix.zk.ui.Components;
import com.potix.zk.ui.Executions;
import com.potix.zk.ui.UiException;
import com.potix.zk.ui.util.Condition;

/**
 * Represents a map of custom attributes of an instance definition
 * ({@link InstanceDefinition}).
 * It is equivalent to the custom-attributes element.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class CustomAttributes implements Condition, java.io.Serializable {
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
