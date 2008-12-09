/* AuRequestProcessor.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec  9 18:55:05     2008, Created by tomyeh

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

This program is distributed under GPL Version 2.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.ui.util;

import org.zkoss.zk.au.AuRequest;

/**
 * The AU request processor.
 * It is used to change how a desktop handles an AU request.
 * To register a processotr, invoke {@link org.zkoss.zk.ui.Desktop#addListener}.
 * When an AU request is received, {@link org.zkoss.zk.ui.sys.DesktopCtrl#process} invokes
 * the registered processotr one-by-one, until {@link #process}
 * return true.
 *
 * @author tomyeh
 * @since 5.0.0
 */
public interface AuRequestProcessor {
	/** Called when a deskto receives an AU request.
	 * In other words, it is called when
	 * {@link org.zkoss.zk.ui.sys.DesktopCtrl#process} was called.
	 *
	 * @param everError if any error ever happened before
	 * processing this request. In other words, indicates if the previous
	 * request causes any exception.
	 * @return whether the request has been processed.
	 * If false is returned, the desktop will continue to the processing.
	 */
	public boolean process(AuRequest request, boolean everError);
}
