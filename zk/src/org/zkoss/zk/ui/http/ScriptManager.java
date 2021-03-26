/* ScriptManager.java

	Purpose:
		
	Description:
		
	History:
		Tue Oct 15 10:24:33 TST 2013, Created by RaymondChao

Copyright (C) 2013 Potix Corporation. All Rights Reserved.


{{IS_RIGHT
	This program is distributed under LGPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zk.ui.http;

import jakarta.servlet.ServletRequest;

/**
 * A manager to manage the script of WPD.
 * See {@link WpdExtendlet}
 *
 * @author RaymondChao
 * @since 7.0.0
 */
public interface ScriptManager {
	/** Check whether the script should be loaded or ignored.
	 * @param request the client request information.
	 * @param jspath the javascript's path
	 * @return true if it should be ignored.
	 */
	public boolean isScriptIgnored(ServletRequest request, String jspath);
}
