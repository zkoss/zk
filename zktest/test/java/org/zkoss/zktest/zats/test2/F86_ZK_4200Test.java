/* F86_ZK_4200Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Jan 29 10:26:23 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import org.zkoss.zel.ImportHandler;

/**
 * @author rudyhuang
 */
public class F86_ZK_4200Test {
	@Test(timeout = 2000)
	public void test() {
		for (int i = 0; i < 10; i++) {
			testResolveVar("test", 4000);
			testResolveVar("test", 10000);
			testResolveVar("test", 100000);
			testResolveVar("Object", 4000);
			testResolveVar("Object", 10000);
			testResolveVar("Object", 100000);
		}
	}

	private static void testResolveVar(String var, int count) {
		ImportHandler handler = ImportHandler.getImportHandler();
		long start = System.nanoTime();
		for (int i = 0; i < count; i++) {
			handler.resolveClass(var);
		}
		long end = System.nanoTime();
		System.out.printf("%d lookups for '%s' took %d ms\n",
				count, var, TimeUnit.MILLISECONDS.convert(end - start, TimeUnit.NANOSECONDS));
	}
}
