/* B85_ZK_3320Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Nov 2 09:34:19 CST 2017, Created by jameschu

Copyright (C) 2017 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

import static org.junit.Assert.assertTrue;

/**
 * @author jameschu
 */
public class B85_ZK_3320Test extends WebDriverTestCase {
	@Test
	public void testOverrideAuOnResp() throws Exception {
		connect();
		waitResponse();
		String jqScript = "zk.log($(\"iframe\").contents().find('pre:eq(1)').html());";
		((ZKWebDriver) getWebDriver()).executeScript(jqScript);
		waitResponse();
		assertTrue(getZKLog().contains("Property \"myParam\" is not allowed"));
	}
}
