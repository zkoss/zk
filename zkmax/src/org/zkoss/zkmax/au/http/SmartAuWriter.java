/* SmartAuWriter.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		Tue Dec  4 09:58:48     2007, Created by tomyeh
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zkmax.au.http;

import org.zkoss.zk.au.http.HttpAuWriter;

/**
 * A smart AU writer that will generate some output to client first if
 * the processing takes more than the time specified in the timeout argument
 * of {@link open}.
 *
 * @author tomyeh
 * @since 3.0.1
 */
public class SmartAuWriter extends HttpAuWriter {
	public SmartAuWriter() {
	}
}
