/* B96_ZK_4509Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Oct 28 17:21:16 CST 2021, Created by leon

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;

public class B96_ZK_4509Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(2)"));
		waitResponse();
		Assertions.assertEquals(20, jq(".z-listitem").length());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(3)"));
		waitResponse();
		Assertions.assertEquals(0, jq(".z-listitem").length());

		click(jq(".z-combobox-button"));
		waitResponse();
		click(jq(".z-comboitem:contains(2)"));
		waitResponse();
		Assertions.assertEquals(20, jq(".z-listitem").length());
		Assertions.assertFalse(hasError());
	}
}
