/* AuResponse.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:35:49     2005, Created by tomyeh
}}IS_NOTE

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.lang.Objects;
import org.zkoss.json.JSONArray;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.util.DeferredValue;

/**
 * A response sent from the server to the client via
 * {@link org.zkoss.zk.ui.sys.UiEngine}.
 *
 * <p>Application developers rarely need access this class and its derived
 * directly.
 * Rather, use {@link org.zkoss.zk.ui.util.Clients} instead.
 * If you prefer to use the derives directly, you can use them with
 * {@link org.zkoss.zk.ui.Execution#addAuResponse}.
 *
 * @author tomyeh
 */
public class AuResponse {
	protected String _cmd;
	private final Object _depends;
	/** Either String or DeferredValue. */
	protected Object[] _data;

	/** Constructs a component-independent response.
	 */
	protected AuResponse(String cmd) {
		this(cmd, (Component)null, (Object[])null);
	}
	/** Constructs a component-independent response.
	 *
	 * @param data the data. It can be null, String, Date, primitive
	 * types ({@link org.zkoss.zk.device.marshal.$boolean}, {@link org.zkoss.zk.device.marshal.$int}...), and an array of the above time.
	 */
	protected AuResponse(String cmd, Object data) {
		this(cmd, (Component)null, data);
	}
	/** Constructs a component-independent response.
	 */
	protected AuResponse(String cmd, Object[] data) {
		this(cmd, (Component)null, data);
	}
	/** Constructs a response with one or zero data.
	 *
	 * @param depends specifies whether this response depends on whether
	 * the depends component.
	 * If depends is not null, this response shall be purged if the depends
	 * component is removed.
	 * If null, this response is called component-independent, and
	 * always sent to the client.
	 *
	 * <p>Note: info of the depends component doesn't send to the client.
	 * It is used only to optimize what responses to send.
	 *
	 * @param data specifies the data to be sent. If null, no data at all.
	 */
	protected AuResponse(String cmd, Component depends, Object data) {
		this(cmd, depends, data != null ? new Object[] {data}: null);
	}
	/** Constructs a response with multiple data.
	 */
	protected AuResponse(String cmd, Component depends, Object[] data) {
		if (cmd == null || cmd.length() == 0)
			throw new IllegalArgumentException("cmd");
		_cmd = cmd;
		_depends = depends;
		_data = data;
	}
	/** Constructs a response with single data.
	 */
	protected AuResponse(String cmd, Page depends, Object data) {
		this(cmd, depends, data != null ? new Object[] {data}: null);
	}
	/** Constructs a response with multiple data.
	 * @param data an array of data (null to ignore).
	 * Each element must be an instance of String, {@link DeferredValue}
	 * or null.
	 * @exception IllegalArgumentException if an element of data
	 * is neither String nor DeferredValue.
	 * @since 3.0.5
	 */
	protected AuResponse(String cmd, Page depends, Object[] data) {
		if (cmd == null || cmd.length() == 0)
			throw new IllegalArgumentException("cmd");
		_cmd = cmd;
		_depends = depends;
		_data = data;
	}

	/** Returns the command of this response (never null).
	 */
	public String getCommand() {
		return _cmd;
	}
	/** Returns the evaluated result of the associated data of
	 * this response (might be null).
	 * 
	 * <p>Note: when this method is called, {@link DeferredValue}
	 * will be evaluated. Thus, don't call it until the rendering phase.
	 * If you want to access it other than the render phase,
	 * use {@link #getRawData} instead.
	 * <p>Note: it is a readonly array. Don't change its value.
	 * @see #getRawData
	 * @since 5.0.0
	 */
	public String getEncodedData() {
		if (_data == null)
			return null;

		final JSONArray encdata = new JSONArray();
		for (int j = 0; j < _data.length; ++j) {
			Object d = _data[j];
			if (d instanceof DeferredValue)
				d = ((DeferredValue)d).getValue();
			encdata.put(d);
		}
		return encdata.toString();
	}
	/** Returns the associated data of this response in the original
	 * format (might be null).
	 * <p>Note: it is a readonly array. Don't change its value.
	 * @see #getEncodedData
	 * @since 3.0.5
	 */
	public Object[] getRawData() {
		return _data;
	}

	/** Returns the component or page that this response depends on.
	 * If it is not null and the depends component/page is removed,
	 * this response shall be removed, too.
	 *
	 * <p>Note: the returned object is either a {@link Component} or a
	 * {@link Page}.
	 */
	public final Object getDepends() {
		return _depends;
	}

	//-- Object --//
	public final boolean equals(Object o) { //prevent override
		return this == o;
	}
	public String toString() {
		//Don't call getCommand and getData since it causes
		//AuSetDeferredAttribute to evaluate the deferred value
		final StringBuffer sb =
			new StringBuffer(60).append("[cmd=").append(_cmd);
		if (_data != null && _data.length > 0) {
			sb.append(", data0=").append(_data[0]);
			if (_data.length > 1) {
				sb.append(", data1=").append(trimOutput(_data[1]));
				if (_data.length > 2)
					sb.append(", data2=").append(trimOutput(_data[2]));
			}
		}
		return sb.append(']').toString();
	}
	private static String trimOutput(Object data) {
		String s = Objects.toString(data);
		if (s == null) return null;
		s = s.trim();
		return s.length() <= 36 ?  s: s.substring(0, 36) + "...";
	}
}
