/* Meta.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 14 21:49:18     2006, Created by tomyeh@potix.com
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
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import com.potix.xml.HTMLs;
import com.potix.zk.ui.Page;
import com.potix.zk.ui.Executions;

/**
 * Represents the &lt;?link?&gt; directive.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 */
public class Meta implements Header {
	private final List _attrs;

	/** Constructor.
	 * @param attrs a map of (String, String) attributes.
	 */
	public Meta(Map attrs) {
		if (attrs == null || attrs.isEmpty()) {
			_attrs = Collections.EMPTY_LIST;
		} else {
			_attrs = new LinkedList();
			for (Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry)it.next();
				final Object nm = me.getKey(), val = me.getValue();
				if (!(nm instanceof String))
					throw new IllegalArgumentException("String is expected, not "+nm);
				if (!(val instanceof String))
					throw new IllegalArgumentException("String is expected, not "+val);

				String sval = (String)val;
				if (sval.length() == 0) sval = null;
				_attrs.add(new String[] {(String)nm, sval});
			}
		}
	}

	public String toHTML(Page page) {
		final StringBuffer sb = new StringBuffer(128).append("<meta");

		for (Iterator it = _attrs.iterator(); it.hasNext();) {
			final String[] p = (String[])it.next();
			String nm = p[0], val = p[1];
			val = (String)Executions.evaluate(page, val, String.class);

			if (val == null || val.length() == 0) {
				sb.append(' ').append(nm).append("=\"\"");
			} else {
				HTMLs.appendAttribute(sb, nm, val);
			}
		}

		return sb.append("/>").toString();
	}
}
