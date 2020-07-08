/* B90_ZK_4468Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Mar 03 14:56:18 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zel.ImportHandler;

/**
 * @author rudyhuang
 */
public class B90_ZK_4468Test {
	@Test
	public void testReImportAfterFailedResolve() {
		ImportHandler importHandler = new ImportHandler();
		String simpleName = B90_ZK_4468Test.class.getSimpleName();
		Assert.assertNull(
				"initially not imported class should resolved to null",
				importHandler.resolveClass(simpleName));

		importHandler.importClass(B90_ZK_4468Test.class.getName());
		Assert.assertEquals(
				"after importing should be resolved",
				B90_ZK_4468Test.class,
				importHandler.resolveClass(simpleName));
	}
}
