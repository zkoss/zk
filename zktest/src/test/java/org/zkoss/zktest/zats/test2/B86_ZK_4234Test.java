/* B86_ZK_4234Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 30 11:51:58 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4234Test extends WebDriverTestCase {
	@Test
	public void test() {
		String customClass = "mycustom";
		connect();
		waitResponse();
		Assertions.assertTrue(jq("@bandbox").hasClass(customClass));
		Assertions.assertTrue(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-combobox-popup").hasClass(customClass));
		click(jq("@button:first"));
		waitResponse();
		Assertions.assertFalse(jq("@bandbox").hasClass(customClass));
		Assertions.assertFalse(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assertions.assertFalse(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assertions.assertFalse(jq(".z-combobox-popup").hasClass(customClass));
		click(jq("@button:last"));
		waitResponse();
		Assertions.assertTrue(jq("@bandbox").hasClass(customClass));
		Assertions.assertTrue(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assertions.assertTrue(jq(".z-combobox-popup").hasClass(customClass));
	}
}
