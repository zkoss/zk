/* F95_ZK_4544Test.java

		Purpose:
		
		Description:
		
		History:
				Thu May 14 10:23:07 CST 2020, Created by leon

Copyright (C) 2020 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

public class F95_ZK_4544Test extends WebDriverTestCase {
	@Test
	public void radioTest() {
		connect();
		
		JQuery radio = jq(".z-radio");
		JQuery radioDisabledButton = jq("@button").eq(0);
		
		Assert.assertTrue(radio.eq(0).hasClass("z-radio-off"));
		Assert.assertTrue(radio.eq(1).hasClass("z-radio-on"));
		Assert.assertTrue(radio.eq(2).hasClass("z-radio-disabled"));
		
		click(jq(".z-radio-content").eq(0));
		waitResponse();
		Assert.assertTrue(radio.eq(0).hasClass("z-radio-on"));
		
		click(jq(".z-radio-content").eq(1));
		waitResponse();
		Assert.assertFalse(radio.eq(0).hasClass("z-radio-on"));
		Assert.assertTrue(radio.eq(1).hasClass("z-radio-on"));
		
		click(radioDisabledButton);
		waitResponse();
		Assert.assertTrue(radio.eq(0).hasClass("z-radio-disabled"));
		
		click(radioDisabledButton);
		waitResponse();
		Assert.assertFalse(radio.eq(0).hasClass("z-radio-disabled"));
	}
	
	@Test
	public void checkboxTest() {
		connect();
		
		JQuery checkbox = jq(".z-checkbox");
		JQuery checkboxContent = jq(".z-checkbox-content").eq(0);
		JQuery checkboxDisabledButton = jq("@button").eq(1);
		
		Assert.assertTrue(checkbox.eq(0).hasClass("z-checkbox-off"));
		Assert.assertTrue(checkbox.eq(1).hasClass("z-checkbox-on"));
		Assert.assertTrue(checkbox.eq(2).hasClass("z-checkbox-disabled"));
		
		click(checkboxContent);
		waitResponse();
		Assert.assertTrue(checkbox.eq(0).hasClass("z-checkbox-on"));
		
		click(checkboxContent);
		waitResponse();
		Assert.assertTrue(checkbox.eq(0).hasClass("z-checkbox-off"));
		
		click(checkboxDisabledButton);
		waitResponse();
		Assert.assertTrue(checkbox.eq(0).hasClass("z-checkbox-disabled"));
		
		click(checkboxDisabledButton);
		waitResponse();
		Assert.assertFalse(checkbox.eq(0).hasClass("z-checkbox-disabled"));
	}
}
