/* AuService.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 24 17:33:06     2009, Created by tomyeh

Copyright (C) 2009 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 3.0 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zk.au;

/**
 * An AU request service used to handle the AU requests ({@link AuRequest}).
 * It is usually implemented by an application and plugged to a component
 * via {@link org.zkoss.zk.ui.Component#setAuService},
 * or plugged to a deskto via {@link org.zkoss.zk.ui.Desktop#addListener}.
 *
 * <p>Notice that the AU service plugged to a component takes the higher
 * proirity than the default handling ({@link org.zkoss.zk.ui.sys.ComponentCtrl#service}).
 *
 * <p>See also <a href="http://docs.zkoss.org/wiki/Zk.Event#How_to_process_data_with_JSON">how to process data with JSON</a>.
 * @author tomyeh
 * @since 5.0.0
 */
public interface AuService {
	/** Handles an AU request.
	 *
	 * @param request the request sent from the client.
	 * @param everError whether any error ever occured before
	 * processing this request.
	 * @return whether the request has been processed.
	 * If false is returned, the defult process (handled by the component)
	 * will take place.
	 * @since 5.0.0
	 */
	public boolean service(AuRequest request, boolean everError);
}
