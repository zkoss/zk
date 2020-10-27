/* B70_ZK_2397Test.java

	Purpose:
		
	Description:
		
	History:
		Mon Jul 01 16:27:46 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import org.zkoss.zktest.zats.ExternalZkXml;
import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
public class B70_ZK_2397Test extends WebDriverTestCase {
	@ClassRule
	public static final ExternalZkXml CONFIG = new ExternalZkXml(B70_ZK_2397Test.class);

	@Test
	public void test() {
		connect();
		waitResponse();
		sleep(1000);

		click(jq("@textbox"));
		waitResponse();

		Assert.assertTrue(hasError());
		Assert.assertEquals("Session timeout. Please reload.", getMessageBoxContent());

		click(jq("@button:contains(Reload)"));
		sleep(100);
		Assert.assertFalse(hasError());
	}
}
