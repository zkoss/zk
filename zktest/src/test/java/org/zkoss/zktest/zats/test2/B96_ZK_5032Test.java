/* B96_ZK_5032Test.java

	Purpose:
		
	Description:
		
	History:
		3:29 PM 2021/11/16, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import org.junit.Assert;
import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5032Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		Assert.assertEquals("myString", jq("@label:eq(1)").text());
		Assert.assertEquals("myString", jq("@label:eq(3)").text());
		Assert.assertEquals("myString", jq("@label:eq(5)").text());
	}
}