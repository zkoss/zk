/* JsContentRenderer.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Wed Oct  1 19:08:57     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.sys;

import java.util.Map;
import java.util.Iterator;
import java.util.Date;

import org.zkoss.lang.Objects;

/**
 * An implementation of {@link ContentRenderer} that renders
 * the content as a JavaScript property (i.e., name: 'value').
 * @author tomyeh
 * @since 5.0.0
 */
public class JsContentRenderer implements ContentRenderer {
	private final StringBuffer _buf = new StringBuffer(128);

	public JsContentRenderer() {
	}

	/** Returns the content being rendered.
	 */
	public StringBuffer getBuffer() {
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
	private void renderName(String name) {
		if (_buf.length() > 0) _buf.append(',');
		_buf.append(name).append(':');
	}
	private void renderValue(String value) {
		if (value == null) _buf.append((String)null);
		else {
			_buf.append('\'');
			for (int j = 0, len = value.length(); j < len; ++j) {
				char cc = value.charAt(j);
				switch (cc) {
				case '\'':
				case '\\': _buf.append('\\'); break;
				case '\n': _buf.append('\\'); cc = 'n'; break;
				case '\t': _buf.append('\\'); cc = 't'; break;
				case '\r': _buf.append('\\'); cc = 'r'; break;
				case '\f': _buf.append('\\'); cc = 'f'; break;
				}
				_buf.append(cc);
			}
			_buf.append('\'');
		}
	}
	/** Renders a Date property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, Date value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(Date value) {
		if (value == null) _buf.append((String)null);
		else _buf.append("new Date(")
			.append(((Date)value).getTime())
			.append(')');
	}
	/** Renders an arbitary object. */
	public void render(String name, Object value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(Object value) {
		if (value == null || value instanceof String) {
			renderValue((String)value);
			return;
		}
		if (value instanceof Date) {
			renderValue((Date)value);
			return;
		}
		if (value instanceof Integer) {
			renderValue(((Integer)value).intValue());
			return;
		}
		if (value instanceof Long) {
			renderValue(((Long)value).longValue());
			return;
		}
		if (value instanceof Double) {
			renderValue(((Double)value).doubleValue());
			return;
		}
		if (value instanceof Short) {
			renderValue(((Short)value).shortValue());
			return;
		}
		if (value instanceof Byte) {
			renderValue(((Byte)value).byteValue());
			return;
		}
		if (value instanceof Float) {
			renderValue(((Float)value).floatValue());
			return;
		}
		if (value instanceof Character) {
			renderValue(((Character)value).charValue());
			return;
		}

		//handle array
		if (value instanceof Object[]) {
			_buf.append('[');
			final Object[] ary = (Object[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof int[]) {
			_buf.append('[');
			final int[] ary = (int[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof long[]) {
			_buf.append('[');
			final long[] ary = (long[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof short[]) {
			_buf.append('[');
			final short[] ary = (short[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof double[]) {
			_buf.append('[');
			final double[] ary = (double[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof byte[]) {
			_buf.append('[');
			final byte[] ary = (byte[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		if (value instanceof char[]) {
			_buf.append('[');
			final char[] ary = (char[])value;
			for (int j = 0; j < ary.length; ++j) {
				if (j > 0) _buf.append(',');
				renderValue(ary[j]);
			}
			_buf.append(']');
			return;
		}
		throw new UnsupportedOperationException("Unknown "+value.getClass());
	}

	/** Renders an integer property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, int value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(int value) {
		_buf.append(value);
	}
	/** Renders a long property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, long value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(long value) {
		_buf.append(value);
	}
	/** Renders a short property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, short value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(short value) {
		_buf.append(value);
	}
	/** Renders a byte property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, byte value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(byte value) {
		_buf.append(value);
	}
	/** Renders a boolean property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, boolean value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(boolean value) {
		_buf.append(value);
	}
	/** Renders a double property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, double value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(double value) {
		_buf.append(value);
	}
	/** Renders a double property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, float value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(float value) {
		_buf.append(value);
	}
	/** Renders a char property.
	 * @param name the property name. Note: it must be a legal JavaScript
	 * variable name.
	 */
	public void render(String name, char value) {
		renderName(name);
		renderValue(value);
	}
	private void renderValue(char value) {
		_buf.append('\'');
		switch (value) {
		case '\'':
		case '\\': _buf.append('\\'); break;
		case '\n': _buf.append('\\'); value = 'n'; break;
		case '\t': _buf.append('\\'); value = 't'; break;
		case '\r': _buf.append('\\'); value = 'r'; break;
		case '\f': _buf.append('\\'); value = 'f'; break;
		}
		_buf.append(value).append('\'');
	}

	/** Renders the JavaScript code snippet.
	 */
	public void renderDirectly(String name, Object value) {
		renderName(name);
		if (value != null && !(value instanceof String))
			throw new UnsupportedOperationException("Only String or null allowed, not "+value.toString());
		_buf.append((String)value);
	}

	/** Renders the JavaScript code snippet for event listeners
	 * registered for the peer widget.
	 */
	public void renderWidgetListeners(Map listeners) {
		if (listeners == null || listeners.isEmpty())
			return;

		renderName("listeners");
		_buf.append('{');
		for (Iterator it = listeners.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			_buf.append(me.getKey()).append(":function(event){\n")
				.append(me.getValue()).append("\n},");
		}
		_buf.setCharAt(_buf.length() - 1, '}');
	}
	/** Renders the JavaScript codes nippet to override the methods
	 * of the peer widget.
	 */
	public void renderWidgetMethods(Map methods) {
		if (methods == null || methods.isEmpty())
			return;

		renderName("methods");
		_buf.append('{');
		for (Iterator it = methods.entrySet().iterator(); it.hasNext();) {
			final Map.Entry me = (Map.Entry)it.next();
			_buf.append(me.getKey()).append(":\n")
				.append(me.getValue()).append("\n,");
		}
		_buf.setCharAt(_buf.length() - 1, '}');
	}
}
