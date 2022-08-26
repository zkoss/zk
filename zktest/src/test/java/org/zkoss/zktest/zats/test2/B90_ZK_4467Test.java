/* B90_ZK_4463Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Dec 19 17:46:06 CST 2019, Created by jameschu

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

/**
 * @author jameschu
 */
public class B90_ZK_4467Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		click(jq(".z-groupbox-title"));
		waitResponse();
		Assertions.assertEquals(jq(".z-groupbox-content").height(), jq("@div").height(), 3);
	}
}
