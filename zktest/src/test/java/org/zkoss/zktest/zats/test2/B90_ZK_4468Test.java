/* B90_ZK_4468Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 03 14:56:18 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zel.ImportHandler;

/**
 * @author rudyhuang
 */
public class B90_ZK_4468Test {
	@Test
	public void testReImportAfterFailedResolve() {
		ImportHandler importHandler = new ImportHandler();
		String simpleName = B90_ZK_4468Test.class.getSimpleName();
		Assertions.assertNull(
				importHandler.resolveClass(simpleName),
				"initially not imported class should resolved to null");

		importHandler.importClass(B90_ZK_4468Test.class.getName());
		Assertions.assertEquals(
				B90_ZK_4468Test.class,
				importHandler.resolveClass(simpleName),
				"after importing should be resolved");
	}
}
