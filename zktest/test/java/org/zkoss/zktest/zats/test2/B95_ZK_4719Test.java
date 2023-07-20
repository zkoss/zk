/* B95_ZK_4719Test.java

		Purpose:
		
		Description:
		
		History:
				Mon Nov 16 17:33:38 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.TouchWebDriverTestCase;
import org.zkoss.zktest.zats.ztl.Element;

public class B95_ZK_4719Test extends TouchWebDriverTestCase {

	@Test
	public void test() {
		connect();
		Element btn = widget("@datebox").$n("btn");
		try {
			tap(toElement(btn));
			waitResponse();
			Assert.assertTrue("should show calendar instead of time wheel.", jq(".z-calendar-text").exists());
		} finally {
			click(jq("@button")); // reset library property
			waitResponse();
		}
	}
}
