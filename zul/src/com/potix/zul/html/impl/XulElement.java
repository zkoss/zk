/* XulElement.java

{{IS_NOTE
	$Id: XulElement.java,v 1.13 2006/03/17 10:06:39 tomyeh Exp $
	Purpose:
		
	Description:
		
	History:
		Mon Jun 20 16:01:40     2005, Created by tomyeh@potix.com
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package com.potix.zul.html.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import com.potix.lang.Objects;
import com.potix.lang.Strings;
import com.potix.xml.HTMLs;

import com.potix.zk.ui.Component;
import com.potix.zk.ui.HtmlBasedComponent;
import com.potix.zk.ui.WrongValueException;
import com.potix.zk.ui.ComponentNotFoundException;

/**
 * The fundamental class for XUL elements.
 *
 * @author <a href="mailto:tomyeh@potix.com">tomyeh@potix.com</a>
 * @version $Revision: 1.13 $ $Date: 2006/03/17 10:06:39 $
 */
abstract public class XulElement extends HtmlBasedComponent {
	/** The action. */
	private String _action;
	/** The parsed actions. */
	private Map _actmap;

	/** Returns the client-side action (CSA).
	 * <p>The format: <br>
	 * action1: javascript1; javascript2; action2: javascript3
	 *
	 * <p>You could specify any action as long as JavaScript supports,
	 * such as onfocus, onblur, onmouseover and onmousout.
	 */
	public String getAction() {
		return _action;
	}
	/** Sets the client-side action.
	 */
	public void setAction(String action) {
		if (action != null && action.length() == 0) action = null;
		if (!Objects.equals(_action, action)) {
			final Map old = _actmap;
			_actmap = parseAction(action);
				//do it first because parseAction might fail

			_action = action;

			if (_actmap != null)
				for (Iterator it = _actmap.entrySet().iterator(); it.hasNext();) {
					final Map.Entry me = (Map.Entry)it.next();
					final String actnm = (String)me.getKey();
					final String actval = (String)me.getValue();
					if (old == null || !actval.equals(old.get(actnm)))
						smartUpdate(actnm, toJavaScript(actval));
				}
			if (old != null)
				for (Iterator it = old.keySet().iterator(); it.hasNext();) {
					final String actnm = (String)it.next();
					if (_actmap == null || !_actmap.containsKey(actnm))
						smartUpdate(actnm, null);
				}
		}
	}

	/** Returns a map of actions (String name, String javascript).
	 */
	private static final Map parseAction(String action) {
		//1. Look for the first ':'
		final Map map = new HashMap();
		int k = action.indexOf(':');
		if (k < 0) throw new WrongValueException("Unknown action: "+action);

		int j = 0, len = action.length();
		for (;;) {
			final String actnm = action.substring(j, k).trim();
			if (actnm.length() == 0) throw new WrongValueException("Unknown action: "+action);

			//2. next ':'
			int l = len; //next ':'
			for (j = ++k; k < len; ++k) {
				final char cc = action.charAt(k);
				if (cc == '\'' || cc == '"') {
					while (++k < len) {
						final char c2 = action.charAt(k);
						if (c2 == cc) break;
						if (c2 == '\\') ++k;
					}
				} else if (cc == ';') {
					l = Strings.skipWhitespaces(action, k + 1);
					for (; l < len; ++l) {
						final char c2 = action.charAt(l);
						if (c2 < 'a' || c2 > 'z')
							break;
					}
					l = Strings.skipWhitespaces(action, l);
					if (l >= len || action.charAt(l) == ':') {
						++k;
						break; //found
					}
				}
			}

			//3. generate it
			final String val = action.substring(j, k > len ? len: k).trim();
			if (val.length() > 0) map.put(actnm, val);
			if (l >= len) return map; //done
			j = k;
			k = l;
		}
	}
	/** Converts an action to JavaScript by interpreting a fellow into
	 * 'uuid'
	 */
	private final String toJavaScript(String action) {
		if (action == null) return null;
		StringBuffer sb = null;
		char quote = (char)0;
		for (int j = 0, len = action.length(); j < len; ++j) {
			char cc = action.charAt(j);
			if (quote == (char)0 &&
			(cc == '_' || (cc >= 'a' && cc <='z') || (cc >= 'A' && cc <='Z'))) {
				int k = j + 1;
				for (; k < len; ++k) {
					cc = action.charAt(k);
					if (cc != '_' && (cc < 'a' || cc > 'z')
					&& (cc < 'A' || cc > 'Z') && (cc < '0' || cc > '9'))
						break;
				}
				final String var = action.substring(j, k);
				try {
					final Component fellow = getFellow(var);
					if (sb == null)
						sb = new StringBuffer(len + 16)
							.append(action.substring(0, j));
					sb.append('\'').append(fellow.getUuid()).append('\'');
				} catch (ComponentNotFoundException ex) {
					if (sb != null) sb.append(var);
				}
				j = k - 1;
			} else {
				if (sb != null) sb.append(cc);
				if (quote != (char)0) {
					if (cc == quote) quote = (char)0;
					else if (cc == '\\' && ++j < len && sb != null)
						sb.append(action.charAt(j));
				} else if (cc == '\'' || cc == '"') {
					quote = cc;
				}
			}
		}
		return sb != null ? sb.toString(): action;
	}

	//-- super --//
	/** Generates the Client-Side-Action attributes to the interior tag.
	 * Reason: onfocus is the main use.
	 */
	public String getInnerAttrs() {
		final String attrs = super.getInnerAttrs();
		if (_actmap == null)
			return attrs;

		final StringBuffer sb = new StringBuffer(100).append(attrs);
		for (Iterator it = _actmap.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			HTMLs.appendAttribute(sb,
				(String)me.getKey(), toJavaScript((String)me.getValue()));
		}
		return sb.toString();
	}
}
