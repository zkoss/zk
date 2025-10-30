/* B86_ZK_4102Test.java

		Purpose:
		
		Description:
		
		History:
				Thu Jun 13 15:25:01 CST 2019, Created by leon

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import org.zkoss.test.webdriver.WebDriverTestCase;

public class B86_ZK_4102Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		waitResponse();
		Assertions.assertEquals(200, jq("@slider").eq(0).height());
		Assertions.assertEquals(32, jq("@slider").eq(0).width());
		Assertions.assertEquals(32, jq("@slider").eq(1).height());
		Assertions.assertEquals(200, jq("@slider").eq(1).width());
		
		click(jq("@button").eq(0));
		waitResponse();
		Assertions.assertEquals(jq(".z-hlayout").height(), jq("@slider").eq(0).height());
		
		click(jq("@button").eq(1));
		waitResponse();
		Assertions.assertEquals(jq(".z-hlayout").width() - 37, jq("@slider").eq(1).width());
		
		click(jq("@button").eq(2));
		waitResponse();
		Assertions.assertEquals(300, jq("@slider").eq(0).width());
		
		click(jq("@button").eq(3));
		waitResponse();
		Assertions.assertTrue(jq("@slider").eq(1).hasClass("z-slider-vertical"));
		Assertions.assertEquals(jq(".z-hlayout").width() - 305, jq("@slider").eq(1).width());
		Assertions.assertEquals(200, jq("@slider").eq(1).height());
		
		click(jq("@button").eq(4));
		waitResponse();
		Assertions.assertTrue(jq("@slider").eq(0).hasClass("z-slider-horizontal"));
		Assertions.assertEquals(jq(".z-hlayout").height(), jq("@slider").eq(0).height());
		Assertions.assertEquals(300, jq("@slider").eq(0).width());
	}
}
