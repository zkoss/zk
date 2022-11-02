/* BindComposerTest.java
	Purpose:

	Description:

	History:
		Tue May 04 15:42:43 CST 2021, Created by jameschu

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zephyr.webdriver.mvvm.book.databinding.bindcomposer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import org.zkoss.zephyr.webdriver.ClientBindTestCase;

/**
 * @author jameschu
 */
public class BindComposerTest extends ClientBindTestCase {
	@Test
	public void test() {
		connect();
		//[Step 1]
		assertEquals("A-1", jq("$win1 $result1").text());
		//[Step 2]
		click(jq("$win1 $btn2"));
		waitResponse();
		assertEquals("doGlobalCommand called", getZKLog());
		//[Step 3]
		assertTrue(jq("$win1 $result3").text().startsWith("org.zkoss.clientbind.ClientBinderImpl@"));
		//[Step 4]
		assertEquals("123", jq("@vlayout $result4").text());
		//[Step 5]
		assertEquals("00", jq("@vlayout $result5").text());
	}
}
