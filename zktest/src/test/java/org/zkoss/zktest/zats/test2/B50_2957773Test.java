/* B50_2957773Test.java

	Purpose:
		
	Description:
		
	History:
		Thu Mar 21 16:15:28 CST 2019, Created by rudyhuang

Copyright (C) 2019 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import net.jcip.annotations.NotThreadSafe;
import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author rudyhuang
 */
@NotThreadSafe
public class B50_2957773Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();

		try {
			click(widget("@datebox").$n("btn"));
			waitResponse();

			Assert.assertFalse(jq(".z-datebox-popup .z-calendar-selected").hasClass("z-calendar-weekend"));
		} finally {
			click(jq("body"));
			waitResponse();
			click(jq("@button"));
			waitResponse();
		}
	}
}
