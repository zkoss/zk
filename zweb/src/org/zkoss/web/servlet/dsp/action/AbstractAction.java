/* AbstractAction.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Sep  7 14:55:38     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.web.servlet.dsp.action;

import java.util.Map;
import java.io.StringWriter;
import java.io.IOException;

import org.zkoss.lang.Strings;
import org.zkoss.xml.XMLs;

import org.zkoss.web.servlet.dsp.DspException;

/**
 * A skeletal implementation to simplify the implementation of actions.
 *
 * @author tomyeh
 */
abstract public class AbstractAction implements Action {
	/** A special integer to denote no attribute is assigned. */
	protected static final int NULL_INT = Integer.MIN_VALUE + 0x68;
	/** A special short to denote no attribute is assigned. */
	protected static final short NULL_SHORT = Short.MIN_VALUE + 0x68;

	private boolean _if = true;
	private boolean _unless = false;

	/** Returns the if condition.
	 * If the if condition is false, this tag is ignored.
	 * If the unless condition ({@link #getUnless}) is true, this tag is
	 * ignored, too.
	 * Tags deriving from this class shall invoke {@link #isEffective} to
	 * know the result.
	 *
	 * <p>Default: true.
	 */
	public boolean getIf() {
		return _if;
	}
	/** Sets the if condition.
	 */
	public void setIf(boolean ifcond) {
		_if = ifcond;
	}
	/** Returns the unless condition.
	 * If the unless condition is true, this tag is ignored.
	 * If the if condition ({@link #getIf}) is true, this tag is ignored, too.
	 * Tags deriving from this class shall invoke {@link #isEffective} to
	 * know the result.
	 *
	 * <p>Default: false.
	 */
	public boolean getUnless() {
		return _unless;
	}
	/** Sets the unless condition.
	 */
	public void setUnless(boolean unless) {
		_unless = unless;
	}

	/** Returns whether this tag is effecive. If false, this tag does nothing
	 * (as if not specified at all).
	 */
	public boolean isEffective() {
		return _if && !_unless;
	}

	//-- Utilities --//
	/** Returns one of {@link ActionContext#PAGE_SCOPE}, {@link ActionContext#REQUEST_SCOPE},
	 * {@link ActionContext#SESSION_SCOPE} and {@link ActionContext#APPLICATION_SCOPE}
	 * of the specified scope name.
	 *
	 * @param scope one of "page", "request", "session" and "application".
	 */
	protected static final int toScope(String scope) {
		return "request".equals(scope) ? ActionContext.REQUEST_SCOPE:
			"session".equals(scope) ? ActionContext.SESSION_SCOPE:
			"application".equals(scope) ? ActionContext.APPLICATION_SCOPE:
				ActionContext.PAGE_SCOPE;
	}
	/** Puts an attribute to the map,
	 * if <code>attrValue</code> is not null.
	 */
	protected static final
	void put(Map params, String attrName, String attrValue) {
		if (attrValue != null)
			params.put(attrName, attrValue);
	}
	/** Puts an attribute to the map.
	 * Unlike othere put(...), it always puts the specified attribute.
	 */
	protected static final
	void put(Map params, String attrName, boolean avail) {
		params.put(attrName, Boolean.valueOf(avail));
	}
	/** Puts an attribute to the map,
	 * if <code>val</code> is not NULL_INT.
	 */
	protected static final
	void put(Map params, String attrName, int val) {
		if (val != NULL_INT)
			params.put(attrName, new Integer(val));
	}
	/** Puts an object if it is not null.
	 */
	protected static final
	void put(Map params, String attrName, Object attrValue) {
		if (attrValue != null)
			params.put(attrName, attrValue);
	}

	/** Appends an attribute to the string buffer,
	 * if <code>attrValue</code> is not null.
	 */
	protected static final
	void append(StringBuffer sb, String attrName, String attrValue) {
		if (attrValue != null)
			sb.append(' ').append(attrName).append("=\"")
			.append(XMLs.encodeAttribute(attrValue)).append('"');
			//it might contain " or other special characters
	}
	/** Appends an attribute to the string buffer,
	 * if <code>avail</code> is true.
	 */
	protected static //not final, e.g., xul has different format
	void append(StringBuffer sb, String attrName, boolean avail) {
		if (avail)
			sb.append(' ').append(attrName);
	}
	/** Appends an attribute to the string buffer,
	 * if <code>val</code> is not NULL_INT.
	 */
	protected static final
	void append(StringBuffer sb, String attrName, int val) {
		if (val != NULL_INT)
			sb.append(' ').append(attrName).append("=\"")
				.append(Integer.toString(val)).append('"');
	}

	//package-level utilities//
	/** Returns the output for rendering fragment, or null if
	 * trim is false.
	 *
	 * <p>The return value is passed to {@link #renderFragment}
	 * directly.
	 */
	/*package*/ static
	StringWriter getFragmentOut(ActionContext ac, boolean trim)
	throws IOException {
		return trim && !(ac.getOut() instanceof StringWriter) ?
			new StringWriter(): null;
	}
	/** Invokes ac.renderFragment() and trim the output if trim is true.
	 *
	 * @param out the return value of {@link #getFragmentOut}.
	 */
	/*package*/ static
	void renderFragment(ActionContext ac, StringWriter out, boolean trim)
	throws DspException, IOException {
		if (trim) {
			final StringBuffer buf =
				(out != null ? out: (StringWriter)ac.getOut()).getBuffer();
				//ac.getOut must be StringWriter if out is null and trim
			final int index = buf.length();
			ac.renderFragment(out);
			Strings.trim(buf, index);
		} else {
			ac.renderFragment(out);
		}
	}
}
