/* B86_ZK_4234Test.java

		Purpose:
		
		Description:
		
		History:
				Tue Apr 30 11:51:58 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;

public class B86_ZK_4234Test extends WebDriverTestCase {
	@Test
	public void test() {
		String customClass = "mycustom";
		connect();
		waitResponse();
		Assert.assertTrue(jq("@bandbox").hasClass(customClass));
		Assert.assertTrue(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assert.assertTrue(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assert.assertTrue(jq(".z-combobox-popup").hasClass(customClass));
		click(jq("@button:first"));
		waitResponse();
		Assert.assertFalse(jq("@bandbox").hasClass(customClass));
		Assert.assertFalse(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assert.assertFalse(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assert.assertFalse(jq(".z-combobox-popup").hasClass(customClass));
		click(jq("@button:last"));
		waitResponse();
		Assert.assertTrue(jq("@bandbox").hasClass(customClass));
		Assert.assertTrue(jq("@combobox").hasClass(customClass));
		click(jq(".z-bandbox-button"));
		waitResponse();
		Assert.assertTrue(jq(".z-bandbox-popup").hasClass(customClass));
		click(jq(".z-combobox-button"));
		waitResponse();
		Assert.assertTrue(jq(".z-combobox-popup").hasClass(customClass));
	}
}
