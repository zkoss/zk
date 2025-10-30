/* B85_ZK_3527_2Test.java

		Purpose:
		
		Description:
		
		History:
				Wed May 29 15:00:01 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import org.zkoss.lang.Library;
import org.zkoss.test.webdriver.WebDriverTestCase;


public class B85_ZK_3527_2Test extends WebDriverTestCase {
	@BeforeAll
	public static void setTZpath() {
		Library.setProperty("org.zkoss.zk.moment.timezone.path", "/test2/data/2017a.json");
	}
	
	@AfterAll
	public static void cleanUp() {
		Library.setProperty("org.zkoss.zk.moment.timezone.path", null);
	}
	
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq("@button").eq(0));
		waitResponse();
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertNotEquals("Thu Apr 01 2038 00:00:00 GMT-0600\nThu Apr 01 2038 00:00:00 GMT+0200", getZKLog());
	}
}
