/* AuRequest.java

	Purpose:
		
	Description:
		
	History:
		Tue May 31 11:31:13     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au;

import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.ComponentNotFoundException;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.sys.PageCtrl;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.sys.ComponentCtrl;

/**
 * A request sent from the client to the server.
 *
 * @author tomyeh
 */
public class AuRequest {
	/** Indicates whether it can be ignored by the server when the server
	 * receives the same requests consecutively.</dd>
	 */
	public static final int BUSY_IGNORE = ComponentCtrl.CE_BUSY_IGNORE;
	/**
	 * Indicates Whether it can be ignored by the server when the server
	 * receives the same requests that was not processed yet.</dd>
	 */
	public static final int REPEAT_IGNORE = ComponentCtrl.CE_REPEAT_IGNORE;
	/** Indicates whether it can be ignored by the server when the server
	 * receives the same requests consecutively.</dd>
	 */
	public static final int DUPLICATE_IGNORE = ComponentCtrl.CE_DUPLICATE_IGNORE;

	private final String _cmd;
	private final Desktop _desktop;
	private Integer _opts;
	private Page _page;
	private Component _comp;
	private final Map _data;
	/** Component's UUID. Used only if _comp is not specified directly. */
	private String _uuid;

	/** Constructor for a request sent from a component.
	 *
	 * @param desktop the desktop containing the component; never null.
	 * @param uuid the component ID (never null)
	 * @param cmd the command of the request (never null)
	 * @param data the data; might be null.
	 * @since 5.0.0
	 */
	public AuRequest(Desktop desktop, String uuid,
	String cmd, Map data) {
		if (desktop == null || uuid == null || cmd == null)
			throw new IllegalArgumentException();
		_desktop = desktop;
		_uuid = uuid;
		_cmd = cmd;
		_data = data != null ? data: Collections.EMPTY_MAP;
	}
	/** Constructor for a general request sent from client.
	 * This is usully used to ask server to log or report status.
	 *
	 * @param cmd the command of the request (never null)
	 * @param data the data; might be null.
	 * @since 5.0.0
	 */
	public AuRequest(Desktop desktop, String cmd, Map data) {
		if (desktop == null || cmd == null)
			throw new IllegalArgumentException();
		_desktop = desktop;
		_cmd = cmd;
		_data = data != null ? data: Collections.EMPTY_MAP;
	}

	/** Activates this request.
	 * <p>Used internally to identify the component and page after
	 * an execution is activated. Applications rarely need to access this
	 * method.
	 * @since 3.0.5
	 */
	public void activate()
	throws ComponentNotFoundException {
		if (_uuid != null) {
			_comp = _desktop.getComponentByUuidIfAny(_uuid);

			if (_comp != null) {
				_page = _comp.getPage();
			} else {
				_page = _desktop.getPageIfAny(_uuid); //it could be page UUID
				if (_page == null)
					throw new ComponentNotFoundException("Component not found: "+_uuid);
			}
			_uuid = null;
		}
	}

	/** Returns the command of this request, such as onClick.
	 * @since 5.0.0
	 */
	public String getCommand() {
		return _cmd;
	}
	/** Returns the options,
	 * a combination of {@link #BUSY_IGNORE},
	 * {@link #DUPLICATE_IGNORE} and {@link #REPEAT_IGNORE}.
	 * @since 5.0.0
	 */
	public int getOptions() {
		if (_opts == null) {
			if (_comp != null)
				_opts = (Integer)((ComponentCtrl)_comp).getClientEvents().get(_cmd);
			if (_opts == null)
				_opts = new Integer(0);
		}
		return _opts.intValue();
	}
	/** Returns the desktop; never null.
	 */
	public Desktop getDesktop() {
		return _desktop;
	}
	/** Returns the page that this request is applied for, or null
	 * if this reqeuest is a general request -- regardless any page or
	 * component.
	 */
	public Page getPage() {
		return _page;
	}
	/** Returns the component that this request is applied for, or null
	 * if it applies to the whole page or a general request.
	 * @exception ComponentNotFoundException if the component is not found
	 */
	public Component getComponent() {
		return _comp;
	}
	/** Returns the data of this request.
	 * If the client sends a string, a number or an array as data,
	 * the data can be retrieved by the key, "". For example,
	 * <code>getData().getInt("")</code>.
	 *
	 * <p>See also <a href="http://docs.zkoss.org/wiki/How_to_Process_Request_with_JSON">how to process data with JSON</a>.
	 *
	 * <p>Notice that, since 5.0.4, this method never returns null.
	 * If no data at all, it simply returns an empty map.
	 * @since 5.0.0
	 */
	public Map getData() {
		return _data;
	}

	//-- Object --//
	public final boolean equals(Object o) { //prevent override
		return this == o;
	}
	public String toString() {
		if (_comp != null)
			return "[comp="+_comp+", cmd="+_cmd+']';
		else if (_page != null)
			return "[page="+_page+", cmd="+_cmd+']';
		else
			return "[uuid="+_uuid+", cmd="+_cmd+']';
	}
}
