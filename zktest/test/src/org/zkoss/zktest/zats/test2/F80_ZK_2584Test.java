/* F80_ZK_2584Test.java

	Purpose:
		
	Description:
		
	History:
		10:17 AM 12/18/15, Created by jumperchen

Copyright (C) 2015 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.zkoss.zktest.zats.WebDriverTestCase;
import org.zkoss.zktest.zats.ztl.JQuery;

/**
 * @author jumperchen
 */
public class F80_ZK_2584Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		JQuery button = jq("button");

		click(button.get(0));
		waitResponse();
		assertEquals("Foo0 Bar0", getZKLog());
		closeZKLog();

		click(button.get(1));
		waitResponse();
		assertEquals("Foo0 Bar0 Foo0 Bar0", getZKLog());
		closeZKLog();

		click(button.get(2));
		waitResponse();
		assertEquals("Foo2 Bar2", getZKLog());
	}
}
