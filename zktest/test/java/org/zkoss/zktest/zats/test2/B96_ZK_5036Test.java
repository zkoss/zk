/* B96_ZK_5036Test.java

	Purpose:
		
	Description:
		
	History:
		3:26 PM 2021/11/15, Created by jumperchen

Copyright (C) 2021 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5036Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		assertEquals("14 de out de 2021", jq("@datebox").eq(0).find("input").val());
		assertEquals("14 de outubro de 2021", jq("@datebox").eq(1).find("input").val());
	}
}