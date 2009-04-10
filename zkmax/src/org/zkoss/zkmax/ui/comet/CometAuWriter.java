/* CometAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue May  6 17:07:24     2008, Created by tomyeh
}}IS_NOTE

Copyright (C) 2008 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 3.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.ui.comet;

import org.zkoss.zk.au.http.HttpAuWriter;

/**
 * The AU writer used with {@link CometServerPush}.
 *
 * @author tomyeh
 * @since 3.5.0
 */
/*package*/ class CometAuWriter extends HttpAuWriter {
	//AuWriter//
	/** Returns au to represent the response channel for AU requests.
	 */
	public String getChannel() {
		return "cm";
	}
}
