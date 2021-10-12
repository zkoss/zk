/* B95_ZK_4410VM.java

	Purpose:
		
	Description:
		
	History:
		Wed Jul 08 11:29:03 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.test2;

import java.util.Arrays;

/**
 * @author rudyhuang
 */
public class B95_ZK_4410VM {
	private final Class<?>[] classes = new Class<?>[] { String.class, Integer.class };

	public Class<?>[] getClasses() {
		return Arrays.copyOf(classes, classes.length);
	}
}
