/* SafeHtmlValue.java

	Purpose:
		
	Description:
		
	History:
		2:39 PM 2023/12/20, Created by jumperchen

Copyright (C) 2023 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zk.ui;

import java.util.Objects;

import org.zkoss.json.JSONValue;

/**
 * A string-like value that is safe to be used as HTML content.
 * @author jumperchen
 * @since 10.0.0
 */
public class SafeHtmlValue implements org.zkoss.json.JSONAware, java.io.Serializable {
	private static final long serialVersionUID = 202312201440L;
	private final String _value;

	/** An empty SafeHtmlValue instance. */
	public static final SafeHtmlValue EMPTY = new SafeHtmlValue("");

	/**
	 * Constructor.
	 * @param value the value to be wrapped.
	 */
	public SafeHtmlValue(String value) {
		_value = value;
	}

	/**
	 * Returns the wrapped value.
	 */
	public String getValue() {
		return _value;
	}

	@Override
	public String toString() {
		return _value;
	}

	@Override
	public String toJSONString() {
		return JSONValue.toJSONString(_value);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SafeHtmlValue that = (SafeHtmlValue) o;
		return Objects.equals(_value, that._value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(_value);
	}

	/**
	 * Returns a SafeHtmlValue instance for the specified value.
	 * @param value the value to be wrapped.
	 * @since 10.0.0
	 */
	public static SafeHtmlValue valueOf(String value) {
		return new SafeHtmlValue(value);
	}
}
