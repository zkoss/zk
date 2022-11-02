/* Va08Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va08Test extends ClientBindTestCase {
	@Override
	protected boolean isHeadless() {
		return false;
	}

	@Test
	public void test() {
		connect();
		JQuery keywordBoxAgent = jq("$keywordBox");
		JQuery keywordLabel = jq("$keywordLabel");

		type(keywordBoxAgent, "123");
		waitResponse();
		assertEquals("123", keywordLabel.text());

		type(keywordBoxAgent, "1234"); //type will trigger clear first
		waitResponse();
		assertEquals("", keywordLabel.text());
	}
}
