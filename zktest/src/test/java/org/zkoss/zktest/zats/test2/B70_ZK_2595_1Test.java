/* B70_ZK_2595Test.java

	Purpose:
		
	Description:
		
	History:
		Tue Dec 15 10:05:08 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.ZatsException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2595_1Test extends ZATSTestCase {
	// Since ZK8 it's not allowed to use both width and hflex together
	@Test
	public void test() throws Throwable {
		Assertions.assertThrows(UiException.class, () -> {
			try {
				connect("/test2/B70-ZK-2595_1.zul");
			} catch (ZatsException e) {
				throw e.getCause();
			}
		});
	}
}
