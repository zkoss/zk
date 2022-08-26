/* F50_3299344Test.java

	Purpose:
		
	Description:
		
	History:
		Wed Apr 10 17:50:53 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;
import org.zkoss.test.webdriver.ztl.JQuery;

/**
 * @author rudyhuang
 */
public class F50_3299344Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		JQuery t1 = jq("@textbox:eq(0)");
		JQuery t2 = jq("@textbox:eq(1)");
		JQuery t3 = jq("@textbox:eq(2)");
		JQuery t4 = jq("@textbox:eq(3)");
		String t1val = t1.val();
		String t2val = t2.val();
		String t3val = t3.val();
		String t4val = t4.val();

		click(jq("@button"));
		waitResponse();
		Assertions.assertEquals(t1val, t1.val());
		Assertions.assertEquals(t2val, t2.val());
		Assertions.assertEquals(t3val, t3.val());
		Assertions.assertEquals(t4val, t4.val());
		Assertions.assertEquals("validationForGrid called:CA - bean:User - property:state\n" +
				"validationForGrid called:TX - bean:User - property:state\n" +
				"validationForGrid called:WA - bean:User - property:state\n" +
				"onBindingValidate() called\n" +
				"onClick() called", getZKLog());
	}
}
