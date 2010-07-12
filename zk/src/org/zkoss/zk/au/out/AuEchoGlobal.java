/* AuEchoGlobal.java

	Purpose:
		
	Description:
		
	History:
		Thu Jul  8 19:16:35 TST 2010, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

*/
package org.zkoss.zk.au.out;

import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;

import org.zkoss.zk.ui.Desktop;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask client to send the specified event for all qualified desktops.
 *
 * <p>Notice that, unlike other requests, {@link AuEchoGlobal} will check all browser windows
 * belonging to the same top browser window -- not just the current browser window.
 *
 * @author tomyeh
 * @since 5.0.4
 */
public class AuEchoGlobal extends AuResponse {
	/**
	 * @param evtnm the event name to echo back
	 * @param data the data to sent with the event when echoed back
	 * @param dt the desktop to receive the event.
	 */
	public AuEchoGlobal(String evtnm, String data, Desktop dt) {
		super("echoGx", new String[] {evtnm, data, dt.getId()});
	}
	/**
	 * @param evtnm the event name to echo back
	 * @param data the data to sent with the event when echoed back
	 * @param dts a collection of desktops ({@link Desktop}) to receive the event.
	 */
	public AuEchoGlobal(String evtnm, String data, Collection dts) {
		super("echoGx", toArray(evtnm, data, dts));
	}
	private static String[] toArray(String evtnm, String data, Collection dts) {
		final List l = new LinkedList();
		l.add(evtnm);
		l.add(data);
		for (Iterator it = dts.iterator(); it.hasNext();)
			l.add(((Desktop)it.next()).getId());
		return (String[])l.toArray(new String[l.size()]);
	}
}
