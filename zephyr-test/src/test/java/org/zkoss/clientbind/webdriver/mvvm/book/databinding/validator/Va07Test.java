/* Va07Test.java

		Purpose:
		
		Description:
		
		History:
				Tue May 11 11:03:59 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.clientbind.webdriver.mvvm.book.databinding.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import org.zkoss.clientbind.webdriver.ClientBindTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author jameschu
 */
public class Va07Test extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		JQuery ageBoxAgent = jq("$ageBox");
		JQuery ageLabel = jq("$ageLabel");

		type(ageBoxAgent, "-1");
		waitResponse();
		assertEquals("0", ageLabel.text());

		type(ageBoxAgent, "2");
		waitResponse();
		assertEquals("2", ageLabel.text());
	}
}
