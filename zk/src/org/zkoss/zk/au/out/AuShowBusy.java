/* AuShowBusy.java

	Purpose:
		
	Description:
		
	History:
		Dec 27, 2007 9:54:04 AM , Created by jumperchen

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.au.out;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.au.AuResponse;

/**
 * A response to ask the client to show the busy message such that
 * the user knows the system is busy.
 * 
 * @author jumperchen
 * @since 3.0.2
 * @see AuClearBusy
 */
public class AuShowBusy extends AuResponse {
	/** Constructs a busy message covering the whole browser.
	 * To close, use {@link AuClearBusy()}.
	 * @param mesg the message to show. Ignored if open is false.
	 * @param open whether to show the busy message, or to close it.
	 * If open is false, the message is ignored.
	 * @since 5.0.0
	 */
	public AuShowBusy(String mesg) {
		super("showBusy", mesg != null ? mesg: "");
	}
	/** Constructs a busy message covering only the specified component.
	 * To close, use {@link AuClearBusy(Component)}.
	 * @param comp the component that the busy message to cover.
	 * Ignored if null. Notice that if the component is not found,
	 * the message won't be shown.
	 * @param mesg the message to show.
	 * @since 5.0.0
	 */
	public AuShowBusy(Component comp, String mesg) {
		super("showBusy", comp, new String[] {comp.getUuid(), mesg != null ? mesg: ""});
	}
}
