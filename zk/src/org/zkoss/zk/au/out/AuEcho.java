/* AuEcho.java

	Purpose:
		
	Description:
		
	History:
		Fri Jan 20 23:28:02     2006, Created by tomyeh

Copyright (C) 2006 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import java.util.Map;
import java.util.HashMap;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;
import org.zkoss.zk.ui.impl.Attributes;

/**
 * A response to ask client to send a dummy request back to the server.
 *
 * <p>It is used by {@link org.zkoss.zk.ui.sys.UiEngine} to solve a special
 * case.
 *
 * <p>There are two formats
 * <ul>
 * <li>data[0]: desktop Id</li>
 * <li>data[0]: the component's UUID<br/>
 * data[1]: the event name<br/>
 * data[2]: the extra data
 * </li>
 * </ul>
 * 
 * @author tomyeh
 * @since 3.0.0
 */
public class AuEcho extends AuResponse {
	/** Contructs an echo response with the specified desktop.
	 *
	 * @param desktop the desktop to send the echo response to.
	 * If null, the echo response is sent to each desktop in the
	 * same browser window.
	 * @since 3.0.0
	 */
	public AuEcho(Desktop desktop) {
		super("echo", desktop != null ? desktop.getId(): null);
	}
	/** Contructs an echo response for each desktop in the same browser
	 * window
	 */
	public AuEcho() {
		this((Desktop)null);
	}
	/** Constructs an echo response that will cause an event to fire
	 * when the client echoes back.
	 * <p>It is the same as <code>AuEcho(comp, evtnm, (Object)data)</code>.
	 * @since 3.0.2
	 */
	public AuEcho(Component comp, String evtnm, String data) {
		this(comp, evtnm, (Object)data);
	}
	/** Constructs an echo response that will cause an event to fire
	 * when the client echoes back.
	 *
	 * @since 5.0.4
	 * @param comp the component to echo the event to (never null).
	 * @param evtnm the event name
	 * @param data the extra infor, or null if not available
	 */
	public AuEcho(Component comp, String evtnm, Object data) {
		super("echo2", comp,
			data != null ?
				new Object[] {comp.getUuid(), evtnm, getKeyOfData(comp, data)}:
				(Object[])(new String[] {comp.getUuid(), evtnm}));
	}
	/** Note: data must be non-null. */
	private static Integer getKeyOfData(Component comp, Object data) {
		Object[] inf = (Object[])comp.getAttribute(Attributes.ECHO_DATA);
		if (inf == null)
			comp.setAttribute(Attributes.ECHO_DATA,
				inf = new Object[] {new Integer(0), new HashMap(4)});
		final Integer key = (Integer)inf[0];
		((Map)inf[1]).put(key, data);
		inf[0] = new Integer(key.intValue() + 1);
		return key;
	}
	/** Retrieves the data associated with an echo event.
	 * Notice that the data will be removed, so the next call always returns null.
	 * @since 5.0.4
	 */
	public static Object getData(Component comp, Object key) {
		final Object[] inf = (Object[])comp.getAttribute(Attributes.ECHO_DATA);
		if (inf != null) {
			final Map map = (Map)inf[1];
			final Object data = map.remove(key);
			if (map.isEmpty())
				comp.removeAttribute(Attributes.ECHO_DATA);
			return data;
		}
		return null;
	}

	/** Default: "zk.echo" if {@link #getDepends} is null (desktop level),
	* null if {@link #getDepends} is not null (component level).
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return getDepends() != null ? null/*event might diff*/: "zk.echo";
	}
}
