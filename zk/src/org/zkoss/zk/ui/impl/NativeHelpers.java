/* NativeHelpers.java

	Purpose:
		
	Description:
		
	History:
		Thu Aug 23 17:33:59     2007, Created by tomyeh

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.zkoss.lang.Objects;
import org.zkoss.xml.HTMLs;
import org.zkoss.idom.Namespace;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;

/**
 * Utilities for implementing {@link org.zkoss.zk.ui.ext.Native.Helper}.
 *
 * @author tomyeh
 * @since 3.0.0
 */
public class NativeHelpers {
	/** Generates the attributes for the specified properties
	 * and namespaces.
	 *
	 * @param props a map of name and value pairs or null
	 * if no properties at all.
	 * Note: the value doesn't contain any EL expression.
	 * @param namespaces a list of {@link Namespace}
	 * to be generated, or null if not.
	 * Note: EL expressions is not allowed
	 */
	public static final
	void getAttributes(StringBuffer sb, Map props, Collection namespaces) {
		if (namespaces != null && !namespaces.isEmpty()) {
			for (Iterator it = namespaces.iterator(); it.hasNext();) {
				final Namespace ns = (Namespace)it.next();
				sb.append(" xmlns");
				if (ns.getPrefix().length() > 0)
					sb.append(':').append(ns.getPrefix());
				sb.append("=\"").append(ns.getURI()).append('"');
			}
		}

		if (props != null && !props.isEmpty()) {
			for (Iterator it = props.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				HTMLs.appendAttribute(sb,
					Objects.toString(me.getKey()),
					Objects.toString(me.getValue()));
			}
		}
	}
}
