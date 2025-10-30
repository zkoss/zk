/* B96_ZK_4798Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jul 08 12:31:01 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B96_ZK_4798Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@treeitem").eq(0));
		waitResponse();
		click(jq("@button"));
		waitResponse();
		Assertions.assertFalse(hasError());
	}
}
