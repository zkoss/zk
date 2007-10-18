/* AuGoURLPage.java

{{IS_NOTE
	Purpose:
		
	Description:
		
	History:
		2007/6/8 11:28:25, Created by henrichen
}}IS_NOTE

Copyright (C) 2007 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under GPL Version 2.0 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/

package org.zkoss.mil.au.out;

import org.zkoss.zk.au.AuResponse;

/**
 * A response to request ZK Mobile to go to its home page.
 *  
 * @author henrichen
 */
public class AuGoHome extends AuResponse {
	public AuGoHome(String url) {
		super("home", url);
	}
}
