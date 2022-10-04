/* B96_ZK_5167Test.java

	Purpose:
		
	Description:
		
	History:
		5:06 PM 2022/10/4, Created by jumperchen

Copyright (C) 2022 Potix Corporation. All Rights Reserved.
*/
package org.zkoss.zktest.zats.test2;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import org.zkoss.zktest.zats.WebDriverTestCase;

/**
 * @author jumperchen
 */
public class B96_ZK_5167Test extends WebDriverTestCase {
	@Test
	public void test() {
		connect();
		click(jq("@button"));
		waitResponse();
		assertTrue(Integer.parseInt(jq("#zk_showBusy-m").css("zIndex")) > 100);
	}
}
