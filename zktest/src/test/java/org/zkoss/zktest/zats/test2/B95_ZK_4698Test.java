/* B95_ZK_4698Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Oct 15 12:22:50 CST 2020, Created by rudyhuang

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

/**
 * @author rudyhuang
 */
public class B95_ZK_4698Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent desktop = connect();
		desktop.query("button").click();
		List<String> zklog = desktop.getZkLog();
		Assertions.assertEquals(1, zklog.size());
		Assertions.assertEquals("myParam = my parameter value", zklog.get(0));
	}
}
