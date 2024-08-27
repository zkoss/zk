/* JsContentRenderer.java

	Purpose:

	Description:

	History:
		Wed Oct  1 19:08:57     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.owasp.encoder.Encode;

import org.zkoss.json.JSONAware;
import org.zkoss.json.JSONs;
import org.zkoss.lang.Strings;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.UiException;

/**
 * An implementation of {@link ContentRenderer} that renders
 * the content as a JavaScript property (i.e., name: 'value').
 * @author tomyeh
 * @since 5.0.0
 */
public class JsContentRenderer implements ContentRenderer {
	private final StringBuilder _buf = new StringBuilder(128);

	public JsContentRenderer() {
	}

	/** Returns the content being rendered.
	 */
	public CharSequence getBuffer() {
		return _buf;
	}

	/** Renders a string property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, String value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a Date property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, Date value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders an arbitrary object. */
	public void render(String name, Object value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders an integer property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, int value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a long property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, long value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a short property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, short value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a byte property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, byte value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a boolean property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, boolean value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a double property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, double value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a double property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, float value) {
		renderName(name);
		renderValue(value);
	}

	/** Renders a char property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, char value) {
		renderName(name);
		renderValue(value);
	}

	private void renderName(String name) {
		if (_buf.length() > 0)
			_buf.append(',');
		_buf.append(name).append(':');
	}

	private void renderValue(String value) {
		if (value == null)
			_buf.append((String) null);
		else {
			_buf.append('\'');
			_buf.append(Encode.forJavaScript(value));
			_buf.append('\'');
		}
	}

	private void renderValue(Date value) {
		if (value == null)
			_buf.append((String) null);
		else
			_buf.append("jq.j2d('").append(JSONs.d2j(value)).append("')");
	}

	private void renderValue(Component value) {
		if (value == null || value.getPage() == null)
			_buf.append((String) null);
		else
			_buf.append("{$u:'").append(value.getUuid()).append("'}");
	}

	private void renderValue(Object value) {
		if (value == null || value instanceof String) {
			renderValue((String) value);
			return;
		}
		if (value instanceof Date) {
			renderValue((Date) value);
			return;
		}
		if (value instanceof Component) {
			renderValue((Component) value);
			return;
		}
		if (value instanceof Integer) {
			renderValue(((Integer) value).intValue());
			return;
		}
		if (value instanceof Long) {
			renderValue(((Long) value).longValue());
			return;
		}
		if (value instanceof Double) {
			renderValue(((Double) value).doubleValue());
			return;
		}
		if (value instanceof Short) {
			renderValue(((Short) value).shortValue());
			return;
		}
		if (value instanceof Byte) {
			renderValue(((Byte) value).byteValue());
			return;
		}
		if (value instanceof Boolean) {
			renderValue(((Boolean) value).booleanValue());
			return;
		}

		if (value instanceof Float) {
			renderValue(((Float) value).floatValue());
			return;
		}
		if (value instanceof Character) {
			renderValue(((Character) value).charValue());
			return;
		}
		if (value instanceof BigDecimal) {
			renderValue(((BigDecimal) value).toString());
			return;
		}
		if (value instanceof Map) {
			_buf.append('{');
			boolean first = true;
			for (Iterator it = ((Map) value).entrySet().iterator(); it.hasNext();) {
				final Map.Entry me = (Map.Entry) it.next();
				if (first)
					first = false;
				else
					_buf.append(',');
				_buf.append('\'').append(me.getKey()).append("':");
				renderValue(me.getValue());
			}
			_buf.append('}');
			return;
		}
		if (value instanceof List) {
			_buf.append('[');
			int j = 0;
			for (Iterator it = ((List) value).iterator(); it.hasNext(); j++) {
				if (j > 0)
					_buf.append(',');
				renderValue(it.next());
			}
			_buf.append(']');
			return;
		}
		//handle array
		if (value instanceof Object[]) {
			_buf.append('[');
			final Object[] ary = (Object[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof int[]) {
			_buf.append('[');
			final int[] ary = (int[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof long[]) {
			_buf.append('[');
			final long[] ary = (long[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof short[]) {
			_buf.append('[');
			final short[] ary = (short[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof float[]) {
			_buf.append('[');
			final float[] ary = (float[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof double[]) {
			_buf.append('[');
			final double[] ary = (double[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof byte[]) {
			_buf.append('[');
			final byte[] ary = (byte[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof char[]) {
			_buf.append('[');
			final char[] ary = (char[]) value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0)
					_buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof JSONAware)
			_buf.append(((JSONAware) value).toJSONString());
		else
			renderValue(value.toString());
	}

	private void renderValue(int value) {
		_buf.append(value);
	}

	private void renderValue(long value) {
		_buf.append(value);
	}

	private void renderValue(short value) {
		_buf.append(value);
	}

	private void renderValue(byte value) {
		_buf.append(value);
	}

	private void renderValue(boolean value) {
		_buf.append(value);
	}

	private void renderValue(double value) {
		_buf.append(value);
	}

	private void renderValue(float value) {
		_buf.append(value);
	}

	private void renderValue(char value) {
		_buf.append('\'');
		switch (value) {
		case '\'':
		case '\\':
			_buf.append('\\');
			break;
		case '\n':
			_buf.append('\\');
			value = 'n';
			break;
		case '\t':
			_buf.append('\\');
			value = 't';
			break;
		case '\r':
			_buf.append('\\');
			value = 'r';
			break;
		case '\f':
			_buf.append('\\');
			value = 'f';
			break;
		}
		_buf.append(value).append('\'');
	}

	/** Renders the JavaScript code snippet.
	 */
	public void renderDirectly(String name, Object value) {
		renderName(name);
		if (value != null && !(value instanceof String))
			throw new UnsupportedOperationException("Only String or null allowed, not " + value.toString());
		_buf.append((String) value);
	}

	/** Renders the JavaScript code snippet for event listeners
	 * registered for the peer widget.
	 */
	public void renderWidgetListeners(Map<String, String> listeners) {
		if (listeners == null || listeners.isEmpty())
			return;

		renderName("listeners");
		_buf.append('{');
		for (Iterator it = listeners.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry) it.next();
			_buf.append(me.getKey()).append(":function(event){\n").append(me.getValue()).append("\n},");
		}
		_buf.setCharAt(_buf.length() - 1, '}');
	}

	/** Renders the JavaScript codes snippet to override the methods
	 * and properties of the peer widget.
	 * This method uses the widget's setOverrides method (at client),
	 * so, if the value is a method, it will preserve the previous method
	 * as '$' + method_name
	 *
	 * @param overrides a map of methods and properties. Notice that the value
	 * must be a valid JavaScript snippet that can be evaluated to
	 * a value. In fact, the map will be generated as follows.<br/>
	 * <code>{name1: value1, name2: value2}</code>.
	 * Examples of values: <code>function () {}</code>, <code>123</code>,
	 * <code>new Date()</code>, and <code>"a literal string"</code>
	 */
	public void renderWidgetOverrides(Map<String, String> overrides) {
		if (overrides == null || overrides.isEmpty())
			return;

		renderName("overrides");
		_buf.append('{');
		for (Iterator it = overrides.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry) it.next();
			final String name = (String) me.getKey();
			final String value = (String) me.getValue();
			if (value != null) {
				//It is too costly to detect if it is a legal expression
				//so we only check the most common illegal case
				final String v = value.trim();
				char cc;
				if (!v.isEmpty() && ((cc = v.charAt(v.length() - 1)) == ';' || cc == ','
						|| (!v.contains("function") && v.indexOf(';') >= 0)))
					throw new UiException("Illegal client override: " + v
							+ (name.startsWith("on")
									? "\nTo listen an event, remember to captalize the third letter, such as onClick"
									: "\nIt must be a legal JavaScript expression (not statement)"));
			}
			_buf.append(name).append(":\n").append(Strings.isEmpty(value) ? "''" : value).append("\n,");
		}
		_buf.setCharAt(_buf.length() - 1, '}');
	}

	public void renderWidgetAttributes(Map<String, String> attrs) {
		renderClientAttributes(attrs);
	}

	public void renderClientAttributes(Map<String, String> attrs) {
		if (attrs == null || attrs.isEmpty())
			return;

		renderName("domExtraAttrs");
		_buf.append('{');
		for (Iterator it = attrs.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry) it.next();
			renderValue(me.getKey()); //allow ':' or others
			_buf.append(':');
			renderValue(me.getValue());
			_buf.append("\n,");
		}
		_buf.setCharAt(_buf.length() - 1, '}');
	}
}
