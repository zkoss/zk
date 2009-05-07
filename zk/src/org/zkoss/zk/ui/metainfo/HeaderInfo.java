/* HeaderInfo.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Thu Sep 14 22:02:29     2006, Created by tomyeh
}}IS_NOTE

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
}}IS_RIGHT
*/
package org.zkoss.zk.ui.metainfo;

import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;

import org.zkoss.xml.HTMLs;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.xel.ExValue;
import org.zkoss.zk.xel.Evaluator;

/**
 * Represents a header element, such as &lt;?link&gt; and &lt;?meta&gt;
 * directives on the ZUML page.
 * They are usually represented as directives in ZUML.
 * For example, the link and meta directives represent &lt;link&gt;
 * and &lt;meta&gt; HTML tags, respectively.
 *
 * <p>It is not serializable.
 *
 * @author tomyeh
 */
public class HeaderInfo {
	private final String _name;
	/** A list of [String nm, ExValue val]. */
	private final List _attrs;

	/** Constructor.
	 *
	 * <p>Note: it detects the href attribute (from the attrs argument), and
	 * encodes it with {@link Executions#encodeURL}.
	 *
	 * @param name the tag name, such as link (never null or empty).
	 * @param attrs a map of (String, String) attributes.
	 */
	public HeaderInfo(String name, Map attrs) {
		if (name == null || name.length() == 0)
			throw new IllegalArgumentException("empty");

		_name = name;
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

				_attrs.add(new Object[] {
					nm, new ExValue((String)val, String.class)});
			}
		}
	}
	/** Returns the tag name of this header element.
	 */
	public String getName() {
		return _name;
	}

	/** Returns as HTML tag(s) representing this header element.
	 *
	 * @param page the page containing this header element.
	 * It is used to evaluate EL expression, if any, contained in the value.
	 */
	public String toHTML(PageDefinition pgdef, Page page) {
		final StringBuffer sb = new StringBuffer(128)
			.append('<').append(_name);

		final boolean bScript = "script".equals(_name);
		String scriptContent = null;
		final Evaluator eval = pgdef.getEvaluator();
		for (Iterator it = _attrs.iterator(); it.hasNext();) {
			final Object[] p = (Object[])it.next();
			final String nm = (String)p[0];
			String val = (String)((ExValue)p[1]).getValue(eval, page);
			if (bScript && "content".equals(nm)) {
				scriptContent = val;
				continue;
			}

			if (val == null || val.length() == 0) {
				sb.append(' ').append(nm).append("=\"\"");
			} else {
				if ("href".equals(nm) || (bScript && "src".equals(nm)))
					val = Executions.encodeURL(val);
				HTMLs.appendAttribute(sb, nm, val);
			}
		}

		if (bScript) {
			sb.append(">\n");
			if (scriptContent != null)
				sb.append(scriptContent).append('\n');
			return sb.append("</script>").toString();
		}
		return sb.append("/>").toString();
	}
}
