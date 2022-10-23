/* InvokationHelper.java

	Purpose:
		
	Description:
		
	History:
		11:54 AM 2022/1/12, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.immutables.generator;

/**
 * Workaround for Immutables 2.8.8 version.
 * @author jumperchen
 */
public class InvokationHelper {
	public static Templates.Invokation newInstance() {
		return Templates.Invokation.initial();
	}
}
