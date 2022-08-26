/* B90_ZK_4526Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Mar 12 11:51:58 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.zats.mimic.DesktopAgent;
import org.zkoss.zktest.zats.ZATSTestCase;

public class B90_ZK_4526Test extends ZATSTestCase {
	@Test
	public void test() {
		DesktopAgent agent = connect();
		List<String> zklog = agent.getZkLog();
		Assertions.assertEquals(1, zklog.size());
		Assertions.assertEquals("init VM2", zklog.get(0));
	}
}
