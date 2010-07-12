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

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

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
	 *
	 * @since 3.0.2
	 * @param comp the component to echo the event to (never null).
	 * @param evtnm the event name
	 * @param data the extra infor, or null if not available
	 */
	public AuEcho(Component comp, String evtnm, String data) {
		super("echo2", comp,
			data != null ? new String[] {comp.getUuid(), evtnm, data}:
				new String[] {comp.getUuid(), evtnm});
	}

	/** Default: "zk.echo" if {@link #getDepends} is null (desktop level),
	* null if {@link #getDepends} is not null (component level).
	 * @since 5.0.2
	 */
	public final String getOverrideKey() {
		return getDepends() != null ? null/*event might diff*/: "zk.echo";
	}
}
