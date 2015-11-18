/* F80_ZK_2959.java

	Purpose:
		
	Description:
		
	History:
		Tue Nov 17 15:56:44 CST 2015, Created by wenning

Copyright (C) 2015 Potix Corporation. All Rights Reserved.

This program is distributed under LGPL Version 2.1 in the hope that
it will be useful, but WITHOUT ANY WARRANTY.
*/
package org.zkoss.zktest.test2;

import org.zkoss.util.media.MediaTypeResolver;

/**
 * 
 * @author wenning
 */
public class F80_ZK_2959 implements MediaTypeResolver {

	public String resolve(String format) {
		if ("svg".equals(format)) return "image/svg+xml(F80-ZK-2959);";
		else return null;
	}

}
